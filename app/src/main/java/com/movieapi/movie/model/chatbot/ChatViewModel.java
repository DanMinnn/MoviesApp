package com.movieapi.movie.model.chatbot;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private final MutableLiveData<List<Message>> messages = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Message>> getMessages(){
        return messages;
    }

    public void addMessage(Message message){
        Log.d("ChatViewModel", "Added message: " + message.getText() + " | isUserMessage: " + message.isFromUser());
        List<Message> currentMessages = messages.getValue(); // Lấy danh sách hiện tại

        if (currentMessages == null) {
            currentMessages = new ArrayList<>();
        }
        currentMessages.add(message);
        messages.setValue(currentMessages); // Cập nhật LiveData trên luồng chính
        Log.d("ChatViewModel", "Messages LiveData updated: " + messages.getValue().size());
    }
}
