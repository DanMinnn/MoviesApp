package com.movieapi.movie.model.chatbot;

public class Message {
    private String text;
    private boolean isFromUser;

    public Message(String text, boolean isFromUser) {
        this.text = text;
        this.isFromUser = isFromUser;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isFromUser() {
        return isFromUser;
    }

    public void setFromUser(boolean fromUser) {
        isFromUser = fromUser;
    }
}
