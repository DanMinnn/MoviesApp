package com.movieapi.movie.model.chatbot;

import java.util.List;

public class GPTRequest {

    private String model;
    List<MessageAPI> messages;

    public GPTRequest(String model, List<MessageAPI> messages) {
        this.model = model;
        this.messages = messages;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public List<MessageAPI> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageAPI> messages) {
        this.messages = messages;
    }
}
