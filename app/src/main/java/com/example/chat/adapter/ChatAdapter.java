package com.example.chat.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat.databinding.ItemContainerReceivedMessageBinding;
import com.example.chat.databinding.ItemContainerSendMessageBinding;
import com.example.chat.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<ChatMessage> chatMessages;
    private Bitmap receiverProfileImage;
    private final String senderId;
    public static final int VIEW_TYPE_SEND= 1;
    public static final int VIEW_TYPE_RECEIVED= 2;

    public void setReceiverProfileImage(Bitmap bitmap){
        receiverProfileImage= bitmap;
    }

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, String senderId) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.senderId = senderId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_SEND){
            return new SendMessageViewHolder(
                    ItemContainerSendMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext())
                            ,parent
                            ,false)
            );
        }else {
            return new ReceiverMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext())
                            ,parent
                            ,false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_SEND){
            ((SendMessageViewHolder) holder).setData(chatMessages.get(position));
        }else {
            ((ReceiverMessageViewHolder) holder).setData(chatMessages.get(position), receiverProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(chatMessages.get(position).senderId.equals(senderId)){
            return VIEW_TYPE_SEND;
        }else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SendMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerSendMessageBinding binding;
        SendMessageViewHolder(ItemContainerSendMessageBinding itemContainerSendMessageBinding){
            super(itemContainerSendMessageBinding.getRoot());
            binding= itemContainerSendMessageBinding;
        }
        void setData(ChatMessage chatMessage){
            binding.textMessageItemContainerSendMessage.setText(chatMessage.message);
            binding.textDataTimeItemContainerSendMessage.setText(chatMessage.dataTime);
        }
    }

    static class ReceiverMessageViewHolder extends RecyclerView.ViewHolder{
        private final ItemContainerReceivedMessageBinding binding;
        ReceiverMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding){
            super(itemContainerReceivedMessageBinding.getRoot());
            binding= itemContainerReceivedMessageBinding;
        }
        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage){
            binding.textMessageItemContainerReceivedMessage.setText(chatMessage.message);
            binding.textDataTimeItemContainerReceivedMessage.setText(chatMessage.dataTime);
            if (receiverProfileImage != null){
                binding.imageProfileItemContainerReceivedMessage.setImageBitmap(receiverProfileImage);
            }
        }
    }

}
