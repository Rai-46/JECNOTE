package jp.ac.jec.cm0146.jecnote.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import jp.ac.jec.cm0146.jecnote.databinding.ItemConteinerRecievedMessageBinding;
import jp.ac.jec.cm0146.jecnote.databinding.ItemConteinerSentMessageBinding;
import jp.ac.jec.cm0146.jecnote.models.ChatMessage;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ChatMessage> chatMessages;
    private final String senderId;

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;


    public ChatAdapter(List<ChatMessage> chatMessages, String senderId) {
        this.chatMessages = chatMessages;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SENT) {
            // SentMessageViewHolderのインスタンスを返す
            return new SentMessageViewHolder(
                    ItemConteinerSentMessageBinding.inflate(
                            /*
                            xmlファイルからビューを作成するために使うのがInflate()です。inflateは「膨らます」という意味で、xmlファイルのレイアウトを膨らませて画面に表示させるイメージです。
                            レイアウトのビューを作成するためにLayoutInflaterを使います。LayoutInflater.from()メソッドに対象となるコンテキストを渡して、膨らませるLayoutInflaterを取得します。
                            そして、inflate()に作成した「LayoutInflater.from(parent.getContext())」を渡し、ビューを作ります。
                             */
                            // parent = item_container_sent_message.xmlかしら??????????
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {
            return new ReceivedMessageViewHolder(
                    ItemConteinerRecievedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessages.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).getSenderId().equals(senderId)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    // 自分がメッセージを送った時
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemConteinerSentMessageBinding binding;

        SentMessageViewHolder(ItemConteinerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.getMessage());
            binding.messageDate.setText(chatMessage.getDateTime());
        }
    }

    // 自分がメッセージを受け取った時
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemConteinerRecievedMessageBinding binding;

        ReceivedMessageViewHolder(ItemConteinerRecievedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot() );
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.getMessage());
            binding.messageDate.setText(chatMessage.getDateTime());
        }

    }

    // String -> Bitmap
    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        if(encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }
}
