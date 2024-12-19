package com.movieapi.movie.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.movieapi.movie.model.chatbot.ChatViewModel;
import com.movieapi.movie.model.chatbot.GPTApi;
import com.movieapi.movie.model.chatbot.GPTResponse;
import com.movieapi.movie.model.chatbot.Message;
import com.movieapi.movie.model.movie.MovieBrief;
import com.movieapi.movie.model.movie.PopularMoviesResponse;
import com.movieapi.movie.model.search.SearchResponse;
import com.movieapi.movie.model.search.SearchResult;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

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
    private ChatViewModel chatViewModel;
    private LifecycleOwner lifecycleOwner;

    private static final long REQUEST_DELAY_MS = 30000;
    private long lastRequestTime = 0;

    retrofit2.Call<SearchResponse> searchResponseCall;
    retrofit2.Call<PopularMoviesResponse> popularMoviesResponse;
    List<SearchResult> searchResultList;
    List<MovieBrief> movieBriefList;

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

        searchResultList = new ArrayList<>();

        //save chat
        chatViewModel = new ViewModelProvider((ViewModelStoreOwner) lifecycleOwner).get(ChatViewModel.class);

        if (chatViewModel.getMessages().getValue() == null || chatViewModel.getMessages().getValue().isEmpty()) {
            chatViewModel.addMessage(new Message(getContext().getString(R.string.welcome), false));
        }

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

        //find movie
        if (userMessage.toLowerCase().startsWith("find movie")){
            String movieName = userMessage.replaceFirst("(?i)find movie", "").trim();
            if (!movieName.isEmpty())
                searchMovie(movieName);
            else
                chatViewModel.addMessage(new Message("Enter movie name u want to find.", false));
        }else if (userMessage.startsWith("suggest movie")){
            suggestMovie();
        }else {
            callChatBot(userMessage);
        }

    }

    private void searchMovie(String query) {

        ApiInterface service = ApiClient.getMovieApi();
        searchResponseCall = service.searchWithBot(Constants.API_KEY, query);
        searchResponseCall.enqueue(new retrofit2.Callback<SearchResponse>() {
            @Override
            public void onResponse(retrofit2.Call<SearchResponse> call, retrofit2.Response<SearchResponse> response) {
                if(!response.isSuccessful()){
                    searchResponseCall = call.clone();
                    searchResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null || response.body().getResults() == null)
                    return;

                searchResultList = response.body().getResults();
                if (!searchResultList.isEmpty()){
                    StringBuilder botResponse = new StringBuilder("I found some movies: ");
                    for (int i = 0; i < Math.min(3, searchResultList.size()); i++){
                        SearchResult searchResult = searchResultList.get(i);
                        botResponse.append("\n- ")
                                .append(searchResult.getTitle())
                                .append(" (")
                                .append(searchResult.getReleaseDate())
                                .append(")\n")
                                .append(getShortOverview(searchResult.getOverview()))
                                .append("\n");
                    }

                    chatViewModel.addMessage(new Message(botResponse.toString(), false));
                }else {
                    chatViewModel.addMessage(new Message("Not matching movies found !", false));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<SearchResponse> call, Throwable t) {
                chatViewModel.addMessage(new Message("Unable to fetch movie details at the moment.", false));
            }
        });
    }

    private void suggestMovie(){
        ApiInterface popularMovie = ApiClient.getMovieApi();
        popularMoviesResponse = popularMovie.getPopularMovies(Constants.API_KEY, 1);
        popularMoviesResponse.enqueue(new retrofit2.Callback<PopularMoviesResponse>() {
            @Override
            public void onResponse(retrofit2.Call<PopularMoviesResponse> call, retrofit2.Response<PopularMoviesResponse> response) {
                if (!response.isSuccessful()){
                    popularMoviesResponse = call.clone();
                    popularMoviesResponse.enqueue(this);
                    return;
                }

                if (response.body() == null || response.body().getResults() == null) return;

                movieBriefList = response.body().getResults();
                if (!movieBriefList.isEmpty()){
                    StringBuilder botResponse = new StringBuilder("Popular movies:\n");
                    for (int i = 0; i < Math.min(5, movieBriefList.size()); i++){
                        MovieBrief movie = movieBriefList.get(i);
                        botResponse.append("\n- ")
                                .append(movie.getTitle())
                                .append("\n")
                                .append(getShortOverview(movie.getOverview()))
                                .append("\n");
                    }

                    chatViewModel.addMessage(new Message(botResponse.toString(), false));
                }else {
                    chatViewModel.addMessage(new Message("Not matching movies found !", false));
                }
            }

            @Override
            public void onFailure(retrofit2.Call<PopularMoviesResponse> call, Throwable t) {
                chatViewModel.addMessage(new Message("Unable to fetch suggestions. Please try again.", false));
            }
        });
    }

    private void callChatBot(String userMessage){
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
                    String errorBody = response.body() != null ? response.body().string() : "No error body";
                    Log.e("API_RESPONSE", "Response failed: " + response.code() + "-" + response.body());
                    Log.e("API_RESPONSE_DETAIL", "Error body: " + errorBody);
                }
            }
        });
    }

    private void setSendButtonState(boolean isEnabled) {
        sendMessage.setEnabled(isEnabled);
        sendMessage.setAlpha(isEnabled ? 1.0f : 0.5f);
    }

    private String getShortOverview(String overview) {
        if (overview == null || overview.isEmpty()) {
            return "No description.";
        }
        String[] sentences = overview.split("\\. "); // Tách các câu dựa trên dấu chấm và khoảng trắng
        StringBuilder shortOverview = new StringBuilder();
        for (int i = 0; i < Math.min(3, sentences.length); i++) { // Lấy tối đa 2 câu đầu
            shortOverview.append(sentences[i]).append(". ");
        }
        return shortOverview.toString().trim(); // Xóa khoảng trắng thừa ở cuối
    }
}
