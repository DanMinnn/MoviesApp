package com.movieapi.movie.model.chatbot;

import android.util.Log;

import com.google.gson.Gson;
import com.movieapi.movie.utils.Constants;

import java.util.Collections;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class GPTApi {

    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson;

    public GPTApi() {
        this.client = new OkHttpClient();
        this.gson = new Gson();
    }

    public void sendMessage(String userMessage, Callback callback) {
        // Tạo message
        MessageAPI message = new MessageAPI("user", userMessage);

        // Tạo GPTRequest
        GPTRequest request = new GPTRequest("gpt-4o-mini", Collections.singletonList(message));

        // Chuyển request thành JSON
        String jsonRequest = gson.toJson(request);

        // Log request JSON
        Log.d("GPT_REQUEST", "Request JSON: " + jsonRequest);


        // Tạo yêu cầu HTTP
        RequestBody body = RequestBody.create(jsonRequest, JSON);
        Request httpRequest = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + Constants.GPT_URL)
                .header("Content-Type", "application/json")
                .post(body)
                .build();

        // Thực hiện HTTP request
        client.newCall(httpRequest).enqueue(callback);
    }
}
