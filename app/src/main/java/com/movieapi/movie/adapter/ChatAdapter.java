package com.movieapi.movie.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.movieapi.movie.R;
import com.movieapi.movie.model.chatbot.Message;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {

    private final Context context;
    private final List<Message> messageList;

    public ChatAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatAdapter.ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatAdapter.ChatHolder((LayoutInflater.from(context).inflate(R.layout.item_message, parent, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatHolder holder, int position) {

        Message message = messageList.get(position);
        if (message.isFromUser()){
            holder.userMessage.setVisibility(View.VISIBLE);
            holder.ln_bot.setVisibility(View.GONE);
            holder.userMessage.setText(message.getText());
        }else {
            holder.userMessage.setVisibility(View.GONE);
            holder.ln_bot.setVisibility(View.VISIBLE);
            holder.botMessage.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public void updateMessages(List<Message> messages) {
        Log.d("ChatAdapter", "Updating messages: " + messages.size());
        this.messageList.clear();
        this.messageList.addAll(messages);
        notifyDataSetChanged();
    }

    public class ChatHolder extends RecyclerView.ViewHolder {

        TextView userMessage, botMessage;
        LinearLayout ln_bot;

        public ChatHolder(@NonNull View itemView) {
            super(itemView);

            userMessage = itemView.findViewById(R.id.txtUserMessage);
            botMessage = itemView.findViewById(R.id.txtBotMessage);
            ln_bot = itemView.findViewById(R.id.ln_bot);
        }
    }
}
