package jp.ac.jec.cm0146.jecnote.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.InputStream;
import java.util.List;

import jp.ac.jec.cm0146.jecnote.activities.ChatListActivity;
import jp.ac.jec.cm0146.jecnote.database.ChatDataOpenHelper;
import jp.ac.jec.cm0146.jecnote.databinding.ItemContainerRecentConversionBinding;
import jp.ac.jec.cm0146.jecnote.listener.ConversionListener;
import jp.ac.jec.cm0146.jecnote.models.ChatMessage;
import jp.ac.jec.cm0146.jecnote.models.StudentUser;
import jp.ac.jec.cm0146.jecnote.utilities.Constants;
import jp.ac.jec.cm0146.jecnote.utilities.PreferenceManager;

public class RecentConversationsAdapter extends RecyclerView.Adapter<RecentConversationsAdapter.ConversionViewHolder>{

    private final List<ChatMessage> chatMessage;
    private final ConversionListener conversionListener;
    private PreferenceManager preferenceManager;
    private ChatListActivity activity;

    public RecentConversationsAdapter(List<ChatMessage> chatMessage, ConversionListener conversionListener, ChatListActivity activity) {
        this.chatMessage = chatMessage;
        this.conversionListener = conversionListener;
        preferenceManager = new PreferenceManager(activity.getApplicationContext());
        this.activity = activity;
    }


    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ItemContainerRecentConversionBinding.inflate(
                        // 表示するレイアウト（呼び出し元？）
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        // setDataにchatDataを渡す（その中でデータをsetしてもらう）
        holder.setData(chatMessage.get(position));
        // why教員アカウントの際に、ここのchatMessageにデータが十分に入っていない -> 無理やり解決
//        Log.i("testtest", "onBIndVIewHolder   name   " + chatMessage.get(position).firstReceiverName);
    }

    @Override
    public int getItemCount() {
        return chatMessage.size();
    }

    // カスタムのViewHolder？
    class ConversionViewHolder extends RecyclerView.ViewHolder {

        ItemContainerRecentConversionBinding binding;

        ConversionViewHolder(ItemContainerRecentConversionBinding itemContainerRecentConversionBinding) {
            super(itemContainerRecentConversionBinding.getRoot());
            // Activityのアイテムオブジェクトを定義
            binding = itemContainerRecentConversionBinding;
        }

        void setData(ChatMessage chatMessage) {
            // 相手の画像をセット
            new DownloadUserImage(binding.recentUserImage).execute(chatMessage.getFirstReceiverImage());

            Log.i("arere", "kiteruyo");




            // isRead は trueが既読した falseが未読

            /*
            isReadがfalseかつ、相手がラストメッセージの時に表示
            自分がラストメッセージなら、非表示
             */



            Log.i("FirestoreCheck", "-------------------------------------");

                if (preferenceManager.getString(Constants.KEY_USER_ID).equals(chatMessage.getLastSenderID())) {
                    Log.i("FirestoreCheck", "自分ラストメッセージ");

                    binding.resentMessage.setText("");
                    binding.messageBatch.setVisibility(View.GONE);
                    preferenceManager.putBoolean(Constants.FCM_RECEIVED_MESSAGE, false);
                } else {
                    Log.i("FirestoreCheck", "相手ラストメッセージ");
                    if(!chatMessage.getIsRead()) {
                        Log.i("FirestoreCheck", "isRead false");

                        binding.resentMessage.setText("新着メッセージがあります。");

                        binding.messageBatch.setVisibility(View.VISIBLE);
                        preferenceManager.putBoolean(Constants.FCM_RECEIVED_MESSAGE, true);
                    } else {// 相手からのメッセージを既読した
                        binding.resentMessage.setText("");
                        binding.messageBatch.setVisibility(View.GONE);
                        preferenceManager.putBoolean(Constants.FCM_RECEIVED_MESSAGE, false);
                    }
                }


            //相手の名前をセット
            // もし、最初の受け取り主が自分だったら、表示するのは、firstSenderName
            // else 最初の受け取り主が相手だったら、表示するのは、firstReceiverName
            if (!preferenceManager.getString(Constants.KEY_USER_ID).equals(chatMessage.getConversionId())) {
                Log.i("testtest", "84");
                Log.i("FirestoreCheck", "" + chatMessage.getFirstReceiverName());
                binding.recentUserName.setText(chatMessage.getFirstReceiverName());

            } else {
                Log.i("testtest", "87");
                Log.i("FirestoreCheck", "" + chatMessage.getFirstSenderId());
                binding.recentUserName.setText(chatMessage.getFirstSenderName());
            }


            Log.i("FirestoreCheck", "-------------------------------------");

            // タッチイベントを定義?
            binding.getRoot().setOnClickListener(v -> {
                // タッチされた時に、消す
                binding.resentMessage.setText("");
                binding.messageBatch.setVisibility(View.GONE);

                StudentUser user = new StudentUser();
                user.id = chatMessage.getConversionId();
                user.userDisplayName = chatMessage.getFirstReceiverName();
                user.userImage = chatMessage.getFirstReceiverImage();
                conversionListener.onConversionClicked(user);
            });
        }
    }

    private Bitmap getConversionImage(String encodedImage) {
        // byte配列にBase64をデコード（画像）する。デフォルトで。
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public class DownloadUserImage extends AsyncTask<String, Void, Bitmap> {
        protected ImageView bmImage;

        // ImageViewの参照先を受け取る
        public DownloadUserImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        @Override
        public Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}
