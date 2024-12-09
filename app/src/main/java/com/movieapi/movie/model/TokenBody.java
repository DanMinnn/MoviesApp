package com.movieapi.movie.model;

public class TokenBody {

    public String request_token;

    public TokenBody(String requestToken) {
        this.request_token = requestToken;
    }

    public String getRequestToken() {
        return request_token;
    }

    public void setRequestToken(String requestToken) {
        this.request_token = requestToken;
    }
}
