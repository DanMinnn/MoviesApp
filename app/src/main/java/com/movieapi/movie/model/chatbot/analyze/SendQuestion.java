package com.movieapi.movie.model.chatbot.analyze;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.movieapi.movie.utils.Constants;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SendQuestion {
    private static final String BACKEND_URL = Constants.LOCAL_URL + "analyze-question";

    public interface AnalyzeCallback {
        void onSuccess(String intent, Map<String, String> entities);
        void onError(String error);
    }

    public static void analyzeQuestion(String question, AnalyzeCallback callback) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        // Tạo JSON yêu cầu
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("question", question);
        } catch (Exception e) {
            callback.onError("Failed to create JSON request");
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        // Tạo yêu cầu
        Request request = new Request.Builder()
                .url(BACKEND_URL)
                .post(body)
                .build();

        // Gửi yêu cầu
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ChatBotHelper", "Request failed: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() -> callback.onError("Failed to connect to server"));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String responseBody = response.body().string();
                        JSONObject jsonResponse = new JSONObject(responseBody);

                        String intent = jsonResponse.optString("intent");
                        JSONObject entitiesJson = jsonResponse.optJSONObject("entities");

                        Map<String, String> entities = new HashMap<>();
                        if (entitiesJson != null) {
                            Iterator<String> keys = entitiesJson.keys(); // Sử dụng keys() để lấy các khóa
                            while (keys.hasNext()) {
                                String key = keys.next();
                                entities.put(key, entitiesJson.optString(key));
                            }
                        }

                        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(intent, entities));
                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(() -> callback.onError("Failed to parse response"));
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Error: " + response.message()));
                }
            }
        });
    }
}
