package com.example.doan_shopsmartphone.adapter;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doan_shopsmartphone.R;
import com.example.doan_shopsmartphone.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatMessage> list;
    private String myId;

    public ChatAdapter(List<ChatMessage> list, String myId) {
        this.list = list;
        this.myId = myId;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getSenderId().equals(myId) ? 1 : 2; // 1: Sent, 2: Received
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 1) ? R.layout.item_chat_sent : R.layout.item_chat_received;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ChatViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = list.get(position);
        ChatViewHolder vh = (ChatViewHolder) holder;

            vh.tvMsg.setText(msg.getContent());

    }

    @Override
    public int getItemCount() { return list.size(); }

    class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvMsg;
        ImageView imgMsg;
        ChatViewHolder(View v) {
            super(v);
            tvMsg = v.findViewById(R.id.tvMessage); // ID này phải có trong cả 2 layout item
            imgMsg = v.findViewById(R.id.imgMessage);
        }
    }
}