package jp.ac.jec.cm0146.jecnote.activities;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.C;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import jp.ac.jec.cm0146.jecnote.adapters.ChatAdapter;
import jp.ac.jec.cm0146.jecnote.database.ChatDataOpenHelper;
import jp.ac.jec.cm0146.jecnote.databinding.ActivityChatBinding;
import jp.ac.jec.cm0146.jecnote.models.ChatMessage;
import jp.ac.jec.cm0146.jecnote.models.StudentUser;
import jp.ac.jec.cm0146.jecnote.network.ApiClient;
import jp.ac.jec.cm0146.jecnote.network.ApiService;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;
import jp.ac.jec.cm0146.jecnote.utilities.PreferenceManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private StudentUser receiverUser;
    private ArrayList<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversionId = null;

    private ChatDataOpenHelper chatDataOpenHelper;

    // 画面を開いているかどうか
    private boolean isOpenView = false;

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.i("fugafuga", "onCreate");

        loadReceiverDetails();
        init();
        setListener();
        listenMessages();
    }

    private void init() {
        Log.i("fugafuga", "init()");

        chatDataOpenHelper = new ChatDataOpenHelper(getApplicationContext());

        // ShardPreferenceのインスタンス生成
        preferenceManager = new PreferenceManager(getApplicationContext());
        // 更新日時更新
        preferenceManager.putString(Constants.DB_UPDATED_DATE, getReadableDateTime(new Date()));
        // チャットデータを格納するArrayList
        chatMessages = new ArrayList<>();
//        chatMessages = chatDataOpenHelper.selectChatData(receiverUser.id);
        Log.i("testDatabase", "chatMessages:" + chatMessages + " " + receiverUser.id);
        // アダプターを定義
        chatAdapter = new ChatAdapter(
                chatMessages,
                preferenceManager.getString(Constants.KEY_USER_ID)
        );
        binding.chatRecyclerView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();

        // 一度ここで、アプリ内通知バッチのpreferenceをfalseにする
        preferenceManager.putBoolean(Constants.FCM_RECEIVED_MESSAGE, false);

    }

    // Intentで送られてきたチャット相手の詳細を定義する
    private void loadReceiverDetails() {
        Log.i("fugafuga", "loadReceiverDetails()");
        /* getSerializableExtra
        インテントから拡張データを取得する。
        @param name 希望する項目の名前。
        putExtra()で追加された項目の値を返します。
        または、Serializable 値が見つからなかった場合は null を返します。
         */

        Intent intent = getIntent();

        // Intent putExtraで渡されたUserを取得
        receiverUser = (StudentUser)intent.getSerializableExtra("user");
        binding.conversationName.setText(receiverUser.userDisplayName);

    }

    private void setListener() {
        Log.i("fugafuga", "setListener()");
        binding.backBtn.setOnClickListener(v -> finish());
        binding.sendBtn.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {

        // インターネットへ接続されているのか確認
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if(info != null && info.isAvailable()) {
            // 接続している
            Log.i("インターネット", "接続");
        } else {
            // 接続されていない
            Log.i("インターネット", "接続していない");

            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
//        builder.setIcon(); TODO アプリのアイコン
            builder.setTitle("確認");
            builder.setMessage("インターネット接続を確認してください。");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.setNeutralButton("設定へ", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_WIRELESS_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.show();
            return;
        }

        // 文字をちゃんと入力しているか
        // FIXME 全角の空白文字は検知できず
        if(binding.inputMessage.getText().toString().isEmpty() || binding.inputMessage.getText().toString().trim().equals("")) {
            return;
        }


        Log.i("fugafuga", "sendMessage()");
        // messageを格納するHashMap
        HashMap<String, Object> message = new HashMap<>();
        // SENDER_IDがキー、shardPreferenceのKEY_USER_IDがバリュー
        message.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
        // KEY_RECEIVER_IDがキー、チャット相手のIDがValue
        message.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
        // KEY_MESSAGEがキー、入力されたメッセージがValue
        message.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());
        // KEY_TIMESTAMPがキー、今のDateがValue
        message.put(Constants.KEY_TIMESTAMP, new Date());
        // isRead SQLite用
        message.put(Constants.KEY_IS_READ, false);

        // databaseのコレクションに追加
        database.collection(Constants.KEY_COLLECTION_CHAT).add(message);

        // conversionIDを更新する？
//        if(conversionId == null) {
//            checkForConversion();
//        }

        // null出ない = 以前にも話したことがあるユーザ
        if(conversionId != null) {// TODO ここの条件が甘い、、別端末からチャットを始めるとここを通らず、新規ユーザとして追加されて、chatListに同じユーザが複数登録されてしまう
            // 更新する
            updateConversion(binding.inputMessage.getText().toString());
        } else {// conversionIdがNull = 初めて話すユーザ
            // TODO ここで、もし別端末からログインをしていたならば、conversionIDをどうにかして取得せねばならない（自分とチャット相手のルームID的なやつ）
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.KEY_SENDER_NAME, preferenceManager.getString(Constants.KEY_USER_NAME));
            conversion.put(Constants.KEY_RECEIVER_NAME, receiverUser.userDisplayName);
            conversion.put(Constants.KEY_SENDER_IMAGE, preferenceManager.getString(Constants.KEY_USER_IMAGE));
            conversion.put(Constants.KEY_RECEIVER_ID, receiverUser.id);
            conversion.put(Constants.KEY_RECEIVER_IMAGE, receiverUser.userImage);
            conversion.put(Constants.KEY_LAST_MESSAGE, binding.inputMessage.getText().toString());
            conversion.put(Constants.KEY_TIMESTAMP, new Date());
            conversion.put(Constants.KEY_IS_READ, false);
            conversion.put(Constants.LAST_SEND_MESSAGE_USERID, preferenceManager.getString(Constants.KEY_USER_ID));
            conversion.put(Constants.LAST_RECEIVED_MESSAGE_USERID, receiverUser.id);
            addConversion(conversion);
        }
        // 相手に通知を行う
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(receiverUser.token);

            JSONObject data = new JSONObject();
            data.put(Constants.KEY_USER_ID, preferenceManager.getString(Constants.KEY_USER_ID));
            data.put(Constants.KEY_USER_NAME, preferenceManager.getString(Constants.KEY_USER_NAME));
            data.put(Constants.USER_FCM_TOKEN, preferenceManager.getString(Constants.USER_FCM_TOKEN));
            data.put(Constants.KEY_MESSAGE, binding.inputMessage.getText().toString());

            JSONObject body = new JSONObject();
            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendNotification(body.toString());
        } catch (Exception exception) {
            showToast(exception.getMessage());
        }

        chatDataOpenHelper.insertMessageData(
                receiverUser.id,
                preferenceManager.getString(Constants.KEY_USER_ID),
                getReadableDateTime(new Date()),
                binding.inputMessage.getText().toString()
        );

        binding.inputMessage.setText(null);

    }

    private void sendNotification(String messageBody) {
        Log.i("testNotificationBody", "body : " + messageBody);
        ApiClient.getClient().create(ApiService.class).sendMessage(
                Constants.getRemoteMsgHeaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()) {
                    try {
                        if(response.body() != null) {
                            JSONObject responseJson = new JSONObject(response.body());
                            JSONArray results = responseJson.getJSONArray("results");
                            if(responseJson.getInt("failure") == 1) {
                                JSONObject error = (JSONObject) results.get(0);
                                showToast(error.getString("error"));
//                                showToast("ここ？");
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    showToast("Notification sent successfully");
                } else {
                    showToast("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                showToast(t.getMessage());
            }
        });
    }

    private String getReadableDateTime(Date date) {
        Log.i("fugafuga", "getReadableDateTime()");
        return new SimpleDateFormat("yyyy年 MM月 dd日 HH:mm:ss", Locale.getDefault()).format(date);
    }

    // もしかして、初めて会話した時のアイコンとかの情報をINSERTしてる？？？チャット情報の親玉？
    private void addConversion(HashMap<String, Object> conversion) {
        Log.i("fugafuga", "addConversion()");
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionId = documentReference.getId());
    }

    // 最後の会話と、時刻を更新している
    private void updateConversion(String message) {
        Log.i("fugafuga", "updateConversion()");
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(conversionId);
        documentReference.update(
                Constants.KEY_LAST_MESSAGE, message,
                Constants.KEY_TIMESTAMP, new Date(),
                Constants.LAST_SEND_MESSAGE_USERID, preferenceManager.getString(Constants.KEY_USER_ID),
                Constants.LAST_RECEIVED_MESSAGE_USERID, receiverUser.id,
                Constants.KEY_IS_READ, false
        );
        Log.i("fugafugaa", "自分のID  " + preferenceManager.getString(Constants.KEY_USER_ID));
    }

    // リッスンする関数？
    private void listenMessages() {
        Log.i("fugafuga", "listenMessages()");
        database.collection(Constants.KEY_COLLECTION_CHAT)// 自分が送った
                // 送り主IDが自分のIDと同じ
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                // 受け取り主IDが自分の
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverUser.id)
                // リスナーを定義しているのでしょうか？多分？変更したら動く的な？Swiftのcombine???
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CHAT)// 自分に送られた
                .whereEqualTo(Constants.KEY_SENDER_ID, receiverUser.id)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
//                .whereEqualTo(Constants.KEY_IS_READ, false) // 自分がまだ見ていないもので、
                .addSnapshotListener(eventListener);

    }

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {

        Log.i("happyNewYear", "チャットのイベントリスナー呼ばれたよ");

        // リアルタイムに既読管理
        // もし、画面を起動していたら、onResumeを呼び出す。
        if (isOpenView) {
            onResume();
        }

        Log.i("fugafuga", "EventListener<>");
        // エラーがあればreturn
        if(error != null) {
            return;
        }
        if (value != null) {// dataがあれば
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                    chatMessage.setReceiverId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_MESSAGE));
                    chatMessage.setDateTime(getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP)));
                    chatMessage.setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
//                    chatMessage.conversionId = documentChange.getDocument().getId()
                    chatMessages.add(chatMessage);

                    // TODO ここでSQLiteにchatMessagesデータを格納する + DBからSELECTしてきたArrayListに追加する。(DB + Firestoreのデータ）
                    // TODO ここでINSERTするべきなのか？？？　→　ChatListActivityのイベントリスナーでもいいような気が、、、
                    // TODO 　→　そうすると、毎回チャットデータをインサートする + chatMessagesに追加されてしまう、、、
                    // それとも、チャットデータにisAddを追加して、DBに追加したら、trueに、されていなかったら、falseに、、、（これだと、受け取り側ができなくなってNG）


                    /**
                     *TODO ここでは、相手からのチャットメッセージのみINSERTするとか
                     * 自分が送ったやつは、sendMessageないでINSERTさせる
                     */

                    /*
                    TODO 最終参照にちじを保存しておいて、それ以降のチャットデータ（日付）をINSERTできればいい
                    preferenceに登録するのは、現在の日時。（更新日時）
                    比べるのは、messageDataの日付で、preferenceに登録されている更新日時よりも後に送信されたものを
                    DBにINSERTする。だけど、これでは不十分。じゃないかもしれない。。。

                    その後、相手からのメッセージだったら、削除する。
                     */
                    //
                    if(parseDate(chatMessage.getDateTime()).compareTo(parseDate(preferenceManager.getString(Constants.DB_UPDATED_DATE))) >= 0) {
                        Log.i("happyNewYear", "めっせーじ：" + chatMessage.getDateTime());
                        Log.i("happyNewYear", "ぷりふぁれんす：" + preferenceManager.getString(Constants.DB_UPDATED_DATE));
                        Log.i("happyNewYear", "内容：" + chatMessage.getMessage() + "\n--------------------");
                        chatDataOpenHelper.insertMessageData(
                                receiverUser.id,
                                documentChange.getDocument().getString(Constants.KEY_SENDER_ID),
                                getReadableDateTime(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP)),
                                documentChange.getDocument().getString(Constants.KEY_MESSAGE)
                        );

                        // 読んだ　= DBに保存した
                        database.collection(Constants.KEY_COLLECTION_CHAT)
                                .document(documentChange.getDocument().getId())
                                .update(Constants.KEY_IS_READ, true);
//                        database.collection(Constants.KEY_COLLECTION_CHAT)
//                                .document(documentChange.getDocument().getId())
//                                .delete();
                    }



//                    chatDataOpenHelper.insertChatTable(receiverUser.id); // これは一度だけにしたい


                }
            }
            // selectChatDataの中でsortさせた
//            chatMessages = chatDataOpenHelper.selectChatData(receiverUser.id);

            // 並び替えている
            chatMessages.sort((obj1, obj2) -> parseDate(obj1.getDateTime()).compareTo(parseDate(obj2.getDateTime())));
            if (count == 0) {
                /**
                 * データセットが変更されたことを、登録されたオブザーバに通知する。
                 * データ変更イベントには、項目変更と構造変更の2種類がある。項目変更とは、1つの項目のデータが更新されたが、位置の変更はなかった場合である。構造的な変更は、データセット内で項目が挿入、削除、移動されたときです。
                 * このイベントは、データセットについて何が変更されたかを特定しないので、すべてのオブザーバは、既存のすべての項目と構造がもはや有効でないかもしれないと仮定することを余儀なくされる。LayoutManagerは、すべての可視ビューを完全に再バインドし、リレーアウトすることを余儀なくされます。
                 * RecyclerView は、このメソッドが使用されたとき、安定した ID を持つことを報告するアダプタのための可視構造変更イベントの合成を試みます。これは、アニメーションやビジュアルオブジェクトの永続化のために役立ちますが、個々のアイテムビューはまだリバウンドと再レイアウトする必要があります。
                 * もしアダプタを作成するのであれば、可能であればより詳細な変更イベントを使用したほうが効率的です。notifyDataSetChanged() を使用するのは最後の手段です。
                 *
                 * www.DeepL.com/Translator（無料版）で翻訳しました。
                 */
                chatAdapter.notifyDataSetChanged();
            } else {
                /**
                 * 現在反映されている itemCount のうち、positionStart から始まる項目が新たに挿入されたことを、登録されているオブザーバに通知する。今までpositionStart以降にあったアイテムが、positionStart + itemCountの位置から見つかるようになったことを通知する。
                 * これは構造変化イベントである。データセット内の他の既存項目の表現は、位置が変更されても最新とみなされ、リバウンドすることはない。
                 * パラメータは以下の通りです。
                 * positionStart - 挿入された最初のアイテムの位置．
                 * itemCount - 挿入されたアイテムの数
                 */
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                // RecyclerViewの表示位置？？を設定している？？
                binding.chatRecyclerView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            // recyclerviewを表示
            binding.chatRecyclerView.setVisibility(View.VISIBLE);
        }
        if(conversionId == null) {
            checkForConversion();
        }
    };


    private void checkForConversion() {
        Log.i("fugafuga", "checkForConversion()");
        if(chatMessages.size() != 0) {// チャットをしていたら
            checkForConversionRemotely(
                    preferenceManager.getString(Constants.KEY_USER_ID),
                    receiverUser.id
            );
            checkForConversionRemotely(
                    receiverUser.id,
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        }
    }

    // senderIdをreceiverIDから変化を監視するListenerを定義
    private void checkForConversionRemotely(String senderId, String receiverId) {
        Log.i("fugafuga", "checkForConversionRemotely()");
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.KEY_SENDER_ID, senderId)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, receiverId)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    // リスナーの内容？？
    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        Log.i("fugafuga", "OnCompleteListener<>");
        if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {// 成功かつ、データがあるかつ、データsizeが0以上のとき、
            /**
             * DocumentSnapshotには、Cloud Firestoreデータベース内のドキュメントから読み取られたデータが含まれています。データは、getData() または get(String) メソッドで抽出することができます。
             * DocumentSnapshotが存在しないドキュメントを指している場合、getData()とそれに対応するメソッドはnullを返します。exists() を呼び出すことで、常に明示的にドキュメントの存在を確認することができます。
             */
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionId = documentSnapshot.getId();
        }
    };


    // ここで相手が既読かどうかを調べ表示する
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("fugafuga", "onResume()");

        isOpenView = true;

        listenAvailabilityOfReceiver();
        readMessage();
        isRead();
    }

    // 相手は既読している？
    private void isRead() {

        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.LAST_SEND_MESSAGE_USERID, preferenceManager.getString(Constants.KEY_USER_ID))
                .whereEqualTo(Constants.LAST_RECEIVED_MESSAGE_USERID, receiverUser.id)
                .addSnapshotListener(ChatActivity.this, (value, error) -> {
                    if(error != null) {
                        return;
                    }
                    if(value != null) {
                        // 既読がtrueだったら、、、、、
                        for(DocumentSnapshot documentSnapshot : value.getDocuments()) {
                            if(Boolean.TRUE.equals(documentSnapshot.getBoolean(Constants.KEY_IS_READ))) {
                                Log.i("kidoku", "VISIBLE    \n" + documentSnapshot.getString(Constants.KEY_SENDER_NAME));
                                binding.alreadyLead.setVisibility(View.VISIBLE);
                                break;
                            } else {
                                Log.i("kidoku", "GONE    \n" + documentSnapshot.getString(Constants.KEY_SENDER_NAME));
                                binding.alreadyLead.setVisibility(View.GONE);
//                                break;
                            }
                        }
                    }
                });

        binding.alreadyLead.setVisibility(View.GONE);

    }

    // 既読フラグをつける + SQLiteに格納
    private void readMessage() {
        // TODO チャットデータ（FirestoreをSQLiteに格納）
        // TODO　ここでチャットデータを削除する

        //TODO ここでSQLiteにchatMessagesデータを格納する + DBからSELECTしてきたArrayListに追加する。(DB + Firestoreのデータ）

//        database.collection(Constants.KEY_COLLECTION_CHAT)
//                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
//                .whereEqualTo(Constants.KEY_IS_READ, false) // FIXME ここはでバック用にしてる。本当はいらないかも
//                .get()
//                .addOnCompleteListener( task -> {
//                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {// 成功かつ、データがあるかつ、データsizeが0以上のとき、
//                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
//                            // SQLiteに保存（自分が受け取ったメッセージ）
//                            chatDataOpenHelper = new ChatDataOpenHelper(getApplicationContext());
//                            chatDataOpenHelper.insertChatTable(receiverUser.id);
//                            chatDataOpenHelper.insertMessageData(
//                                    documentSnapshot.getString(Constants.KEY_RECEIVER_ID),
//                                    documentSnapshot.getString(Constants.KEY_SENDER_ID),
//                                    getReadableDateTime(documentSnapshot.getDate(Constants.KEY_TIMESTAMP)),
//                                    documentSnapshot.getString(Constants.KEY_MESSAGE)
//                            );
//                            database.collection(Constants.KEY_COLLECTION_CHAT).document(documentSnapshot.getId())
//                                    .update(Constants.KEY_IS_READ, true);
//                            Log.i("readtest", "is readed update!");
//
//                        }
//                    }
//                });

            // ラストメッセージの送信者が相手で、かつ、未読だったら、true(既読)に変える
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                .whereEqualTo(Constants.LAST_SEND_MESSAGE_USERID, receiverUser.id)
                .whereEqualTo(Constants.LAST_RECEIVED_MESSAGE_USERID, preferenceManager.getString(Constants.KEY_USER_ID))
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                            database.collection(Constants.KEY_COLLECTION_CONVERSATIONS).document(documentSnapshot.getId())
                                    .update(
                                            Constants.KEY_IS_READ, true
                                    );
                        }
                    }
                });
    }

    // 画面が表示（@onResume）された時に呼ばれる。
    private void listenAvailabilityOfReceiver() {
        Log.i("fugafuga", "listenAvailabilityOfreceiver()");
        /* document
        このコレクション内の指定されたパスにあるドキュメントを参照する DocumentReference のインスタンスを取得する。
        パラメータは以下のとおりです。
        documentPath - ドキュメントへのスラッシュで区切られた相対パス。
        戻り値
        DocumentReference のインスタンス。
         */
        /*
        Activity-scopedのリスナーを使用して、このDocumentReferenceで参照されるドキュメントのリスニングを開始します。
        リスナーはActivity.onStopの間に自動的に削除されます。
        パラメータは以下の通りです。
        activity - リスナーをスコープするアクティビティ．
        listener - スナップショットで呼び出されるイベントリスナーです。
        戻り値
        リスナーを削除するために使用することができる登録オブジェクト。
         */
        // KEY_COLLECTION_USERSの中のチャット相手のIDをリスナーする
        database.collection(Constants.KEY_COLLECTION_USER).document(
                receiverUser.id
        ).addSnapshotListener(ChatActivity.this, (value, error) -> {// リスナーの定義
            if(error != null) {
                return;
            }
            // valueは受け取り主データ
            if(value != null) {
                receiverUser.token = value.getString(Constants.USER_FCM_TOKEN);
                if(receiverUser.userImage == null) {
                    chatAdapter.notifyItemRangeChanged(0, chatMessages.size());
                }
            }


        });
    }

    // Date情報 String -> Date
    private Date parseDate(String dateStr) {

        Date date = null;

        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年 MM月 dd日 HH:mm:ss");
            date = sdf.parse(dateStr);
//            Log.i("dateInfo", String.valueOf(date));
            return date;

        } catch (ParseException e ) {
            e.printStackTrace();
        }

        return date;

    }

    @Override
    protected void onPause() {
        super.onPause();
        isOpenView = false;
    }
}