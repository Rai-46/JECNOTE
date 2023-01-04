package jp.ac.jec.cm0146.jecnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jp.ac.jec.cm0146.jecnote.adapters.RecentConversationsAdapter;
import jp.ac.jec.cm0146.jecnote.databinding.ActivityChatListBinding;
import jp.ac.jec.cm0146.jecnote.listener.ConversionListener;
import jp.ac.jec.cm0146.jecnote.models.ChatMessage;
import jp.ac.jec.cm0146.jecnote.models.StudentUser;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;
import jp.ac.jec.cm0146.jecnote.utilities.PreferenceManager;

public class ChatListActivity extends AppCompatActivity implements ConversionListener {

    private ActivityChatListBinding binding;
    private PreferenceManager preferenceManager;
    private List<ChatMessage> conversations;
    private RecentConversationsAdapter conversationsAdapter;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new PreferenceManager(getApplicationContext());

        init();
        getToken();
        setListener();
        listenConversations();
    }

    private void init() {
        conversations = new ArrayList<>();
        conversationsAdapter = new RecentConversationsAdapter(conversations, this, ChatListActivity.this);
        // RecyclerViewにconversationsAdapterをセット（ListViewみたいなやつ）
        binding.conversationsRecyclerView.setAdapter(conversationsAdapter);
        database = FirebaseFirestore.getInstance();
    }



    private void setListener() {
        binding.backBtn.setOnClickListener(v -> finish());

        // ユーザが教員だったら、searchUsersを表示する。
        if((preferenceManager.getBoolean(Constants.IS_TEACHER) != null) && preferenceManager.getBoolean(Constants.IS_TEACHER)) {
            binding.searchUsers.setVisibility(View.VISIBLE);
            binding.searchUsers.setOnClickListener(v -> {
                Intent intent = new Intent(getApplicationContext(), SearchUserActivity.class);
                startActivity(intent);
            });
        }
    }

    private void listenConversations() {
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                // 送り主がユーザ（端末の）時
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                // イベントが走るかどうかを見張っている？？
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSATIONS)
                // 受け取り主がユーザ（端末の）時
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManager.getString(Constants.KEY_USER_ID))
                // イベントが走るかどうかを見張っている？？
                .addSnapshotListener(eventListener);
    }

    // EventListenerは関数型インターフェース。だからラムダ式でかける！（要はオーバーライドしてる）（多分ここ使わない。。。）
    /*
    onEvent は新しい値、またはエラーが発生した場合はエラーと共に呼び出されます。valueとerrorのどちらか一方が非NULLであることが保証される。
    パラメタ．
    value - イベントの値．エラーが発生した場合は，null が返されます．
    error - エラーがあった場合は，そのエラー．
     */
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null) {
            // エラーがあればリターン
            return;
        }
        if(value != null) {
            // データがあればデータの分だけ繰り返し処理。
            /*
            getDocumentChanges()
            最後のスナップショット以降に変更されたドキュメントの一覧を返します。最初のスナップショットであれば、すべてのドキュメントが追加された変更としてリストに含まれます。
            メタデータにのみ変更があったドキュメントは含まれません。
            返り値
            直近のスナップショットからのドキュメントの変更内容の一覧。
             */
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if(documentChange.getType() == DocumentChange.Type.ADDED) { // 追加された時の
                    // 送り主ID
                    String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    // 受け取り主ID
                    String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    // ChatMessageのインスタンス生成
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderId(senderId);
                    chatMessage.setReceiverId(receiverId);

                    if(preferenceManager.getString(Constants.KEY_USER_ID).equals(senderId)) {// 自分が送り主だった時
                        // 受け取り主のアイコンを代入
                        chatMessage.setFirstReceiverImage(documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE));
                        // 受け取り主の名前を代入
                        chatMessage.setFirstReceiverName(documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME));
                        // 受け取り主のIDを代入
                        chatMessage.setConversionId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                        // 最初の送り主には、自分のID
                        chatMessage.setFirstSenderId(documentChange.getDocument().getString(Constants.KEY_USER_ID));
                        // 最初の送り主名前は、自分の名前
                        chatMessage.setFirstSenderName(documentChange.getDocument().getString(Constants.KEY_USER_NAME));
                    } else {// 自分以外が送り主だった時
                        // 送り主のアイコンを代入
                        chatMessage.setFirstReceiverImage(documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE));
                        // 送り主の名前を代入
                        chatMessage.setFirstReceiverName(documentChange.getDocument().getString(Constants.KEY_SENDER_NAME));
                        // 送り主のIDを代入
                        chatMessage.setConversionId(documentChange.getDocument().getString(Constants.KEY_SENDER_ID));
                        chatMessage.setFirstSenderId(documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID));
                    }
                    // チャットメッセージを代入
                    chatMessage.setMessage(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                    // 時刻データ？を代入
                    chatMessage.setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                    // 最後の送信ユーザ
                    chatMessage.setLastSenderID(documentChange.getDocument().getString(Constants.LAST_SEND_MESSAGE_USERID));
                    // isRead TODO　ここがうまく効いてない説
                    Log.i("isRead ChatListActivity", ":" + documentChange.getDocument().getBoolean(Constants.KEY_IS_READ));
                    chatMessage.setIsRead(documentChange.getDocument().getBoolean(Constants.KEY_IS_READ));

                    // ArrayListに追加
                    // 同じユーザが入るのは止めたい
                    for(ChatMessage cm: conversations) {
                        // 今回の送り主と受け取り主が同じデータがArrayListにも入っていたらreturnする
                        // 同じチャットるーむが作られるのを阻止したい

                        if(chatMessage.getSenderId().equals(cm.getSenderId()) && chatMessage.getReceiverId().equals(cm.getReceiverId())) {
                            // 待ってこれじゃダメかも -> いや、大丈夫そう（２回目）
                            Log.i("butbut??", "入ってしまった、、、");
                            return;
                        }
                    }
                    conversations.add(chatMessage);
                } else if(documentChange.getType() == DocumentChange.Type.MODIFIED) { // 変更された時（もしかしたら、変更された項目だけ取ってくる感じかも）

                    for(int i = 0; i < conversations.size(); i++) {
                        // 変更された項目のsenderIDを代入
                        String senderId = documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        // 変更された項目のreceiverIdを代入
                        String receiverId = documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if(conversations.get(i).getSenderId().equals(senderId) && conversations.get(i).getReceiverId().equals(receiverId)) {
                            conversations.get(i).setMessage(documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE));
                            conversations.get(i).setDateObject(documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP));
                            conversations.get(i).setIsRead(documentChange.getDocument().getBoolean(Constants.KEY_IS_READ));
                            conversations.get(i).setLastSenderID(documentChange.getDocument().getString(Constants.LAST_SEND_MESSAGE_USERID));
                            break;
                        }
                    }
                }
            }
            // 並び替えてるけど、、日付で並び替えてる？
            Collections.sort(conversations, (obj1, obj2) -> obj2.getDateObject().compareTo(obj1.getDateObject()));
            // Adapterに？データセットが変更されたことを登録されたオブザーバに通知する？？
            conversationsAdapter.notifyDataSetChanged();
            // アダプタの一へスムーズにスクロールをする。新しいメッセージがきたらスクロールされる
            binding.conversationsRecyclerView.smoothScrollToPosition(0);
            // recyclerViewを表示
            binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
        }
    };

    private void getToken() {
        // 通知に必要なFCMトークン
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateTokens);
    }

    private void updateTokens(String token) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference = database.collection(Constants.KEY_COLLECTION_USER)
                .document(preferenceManager.getString(Constants.KEY_USER_ID));
        documentReference.update(Constants.USER_FCM_TOKEN, token);
    }

//    @Override
//    protected void onRestart(){
//        super.onRestart();
//        reload();
//    }
//    public void reload(){
//        Intent intent = getIntent();
//        overridePendingTransition(0, 0);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//        finish();
//        overridePendingTransition(0,0);
//        startActivity(intent);
//    }


    @Override
    public void onConversionClicked(StudentUser user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("read", true);
        startActivity(intent);
    }
}