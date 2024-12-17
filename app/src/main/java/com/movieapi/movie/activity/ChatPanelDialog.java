package com.movieapi.movie.activity;

import static com.google.android.material.internal.ContextUtils.getActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.movieapi.movie.R;
import com.movieapi.movie.adapter.ChatAdapter;
import com.movieapi.movie.databinding.DialogChatPanelBinding;
import com.movieapi.movie.model.chatbot.ChatViewModel;
import com.movieapi.movie.model.chatbot.GPTApi;
import com.movieapi.movie.model.chatbot.GPTResponse;
import com.movieapi.movie.model.chatbot.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChatPanelDialog extends BottomSheetDialog {

    RecyclerView chatRecView;
    EditText edMessage;
    ImageView sendMessage;

    ChatAdapter chatAdapter;
    List<Message> messageList;
    private ChatViewModel chatViewModel;
    private LifecycleOwner lifecycleOwner;
    private final ArrayList<Message> messages = new ArrayList<>();

    private static final long REQUEST_DELAY_MS = 30000;
    private long lastRequestTime = 0;

    public ChatPanelDialog(@NonNull LifecycleOwner lifecycleOwner) {
        super((Context) lifecycleOwner);
        this.lifecycleOwner = lifecycleOwner;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("ChatPanelDialog", "onCreate called");

        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_chat_panel, null);
        setContentView(view);

        chatRecView = view.findViewById(R.id.chatRecView);
        edMessage = view.findViewById(R.id.edAddMessage);
        sendMessage = view.findViewById(R.id.send_message);

        chatAdapter = new ChatAdapter(getContext(), new ArrayList<>());
        chatRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        chatRecView.setAdapter(chatAdapter);

        //save chat
        chatViewModel = new ViewModelProvider((ViewModelStoreOwner) lifecycleOwner).get(ChatViewModel.class);

        if (lifecycleOwner instanceof ViewModelStoreOwner) {
            chatViewModel = new ViewModelProvider((ViewModelStoreOwner) lifecycleOwner).get(ChatViewModel.class);
            chatViewModel.getMessages().observe(lifecycleOwner, messages -> {
                Log.d("ChatPanelDialog", "Messages updated: " + messages.size());
                chatAdapter.updateMessages(messages);
                chatRecView.scrollToPosition(messages.size() - 1);
            });
        } else {
            throw new IllegalArgumentException("LifecycleOwner must implement ViewModelStoreOwner");
        }


        sendMessage();
    }

    private void sendMessage() {

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMessage = edMessage.getText().toString().trim();
                if (!userMessage.isEmpty()) {

                    chatViewModel.addMessage(new Message(userMessage, true));
                    edMessage.setText("");
                    sendMessageToBot(userMessage);
                }
            }
        });
    }

    private void sendMessageToBot(String userMessage) {

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastRequestTime < REQUEST_DELAY_MS) {
            long remainingTime = (REQUEST_DELAY_MS - (currentTime - lastRequestTime)) / 1000;
            Toast.makeText(getContext(), "Throttling requests. Please wait." + remainingTime + "s", Toast.LENGTH_SHORT).show();
            return;
        }
        lastRequestTime = currentTime;

        setSendButtonState(false);
        new Handler(Looper.getMainLooper()).postDelayed(() ->
                setSendButtonState(true), REQUEST_DELAY_MS - (currentTime - lastRequestTime));

        GPTApi gptApi = new GPTApi();

        gptApi.sendMessage(userMessage, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("API_ERROR", "API call failed: " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    GPTResponse gptResponse = new Gson().fromJson(responseBody, GPTResponse.class);
                    String botMessage = gptResponse.getChoices().get(0).getMessage().getContent();

                    new Handler(Looper.getMainLooper()).post(() -> {

                        chatViewModel.addMessage(new Message(botMessage, false));
                        Log.d("ChatBot", "Bot message added: " + botMessage);
                    });
                } else {
                    Log.e("API_RESPONSE", "Response failed: " + response.code());
                }
            }
        });
    }

    private void setSendButtonState(boolean isEnabled) {
        sendMessage.setEnabled(isEnabled);
        sendMessage.setAlpha(isEnabled ? 1.0f : 0.5f);
    }
}
