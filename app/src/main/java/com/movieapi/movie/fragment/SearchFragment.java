package com.movieapi.movie.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.movieapi.movie.R;
import com.movieapi.movie.activity.SearchResultActivity;
import com.movieapi.movie.adapter.FilterAdapter;
import com.movieapi.movie.adapter.RecentSearchAdapter;
import com.movieapi.movie.adapter.movies.MovieBriefSmallAdapter;
import com.movieapi.movie.database.DatabaseHelper;
import com.movieapi.movie.database.search.RecentSearch;
import com.movieapi.movie.database.search.SearchDatabase;
import com.movieapi.movie.model.ButtonItem;
import com.movieapi.movie.model.ShareButtonList;
import com.movieapi.movie.model.movie.GenreMoviesResponse;
import com.movieapi.movie.model.movie.MovieBrief;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchFragment extends Fragment{
    EditText edSearchView;
    FloatingActionButton fabFilter, fabSearch;
    TextView searchRecent;
    RecyclerView recentSearchRecView, filterRecView, filter_genre_movie_recView;
    FilterAdapter adapter;
    private List<ButtonItem> buttonItemList, oldDataButtonList;
    String query;
    SharedPreferences prefUser;
    private String userId;
    List<MovieBrief> filterMoviesList;
    MovieBriefSmallAdapter filterMoviesAdapter;
    Call<GenreMoviesResponse> mGenreMoviesResponse;
    boolean pagesOver = false;
    int presentPage = 1;
    private ShareButtonList shareButtonList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edSearchView = view.findViewById(R.id.searchView);
        fabFilter = view.findViewById(R.id.sort_filter_fab);
        fabSearch = view.findViewById(R.id.search_fab);
        searchRecent = view.findViewById(R.id.search_recents_textView);
        recentSearchRecView = view.findViewById(R.id.search_recView_recents);
        filterRecView = view.findViewById(R.id.filter_recView);
        filter_genre_movie_recView = view.findViewById(R.id.filter_genre_movie_recView);

        buttonItemList = new ArrayList<>();

        prefUser = getActivity().getApplicationContext().getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
        userId = prefUser.getString("idUser", "");

        LiveData<List<RecentSearch>> recentSearches = SearchDatabase.getInstance(requireContext())
                .searchDao()
                .getAllRecentSearch(userId);

        recentSearches.observe(requireActivity(), new Observer<List<RecentSearch>>() {
            @Override
            public void onChanged(List<RecentSearch> recentSearches) {
                Collections.reverse(recentSearches);
                RecentSearchAdapter recentSearchAdapter = new RecentSearchAdapter(getActivity(), recentSearches);
                recentSearchRecView.setAdapter(recentSearchAdapter);
                recentSearchRecView.setLayoutManager(new LinearLayoutManager(getContext()));

                if (recentSearches.isEmpty()){
                    searchRecent.setVisibility(View.GONE);
                    recentSearchRecView.setVisibility(View.GONE);
                }
                else{
                    searchRecent.setVisibility(View.VISIBLE);
                    recentSearchRecView.setVisibility(View.VISIBLE);
                }

            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {

            class SaveSearch extends AsyncTask<Void, Void, Void>{
                @Override
                protected Void doInBackground(Void... voids) {
                    query = edSearchView.getText().toString().trim().toLowerCase();
                    if (!DatabaseHelper.isRecentSearch(requireContext(), query, userId)){
                        RecentSearch recentSearch = new RecentSearch(query,userId);
                        SearchDatabase.getInstance(requireContext())
                                .searchDao()
                                .insertSearch(recentSearch);
                    }
                    return null;
                }
            }

            @Override
            public void onClick(View v) {
                query = edSearchView.getText().toString().trim().toLowerCase();
                SaveSearch saveSearch = new SaveSearch();
                saveSearch.execute();

                Intent iSearchResult = new Intent(getActivity(), SearchResultActivity.class);
                iSearchResult.putExtra("query", query);
                //iSearchResult.putExtra("filterFilm", (Serializable) buttonItemList);
                startActivity(iSearchResult);
            }
        });

        fabFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventSortFilter();
            }
        });

    }

    private void eventSortFilter(){
        Dialog dialog = new Dialog(getContext());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_sort_filter);

        // event handling
        findViewById(dialog);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    private void setAdapterButtonList(){
        adapter = new FilterAdapter(getContext(), buttonItemList);
        filterRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        filterRecView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    private void findViewById(Dialog dialog){
        LinearLayout lnRegions, lnGenre, lnTime;
        Button btnReset, btnApply;

        lnRegions = dialog.findViewById(R.id.lnRegions);
        lnGenre = dialog.findViewById(R.id.lnGenre);
        lnTime = dialog.findViewById(R.id.lnTime);

        buttonItemList.clear();

        for (int i = 0; i < lnRegions.getChildCount(); i++){
            View child = lnRegions.getChildAt(i);
            if (child instanceof Button){
                Button button = (Button) child;
                setToggleOnTouchListener(button);
            }
        }

        for (int i = 0; i < lnGenre.getChildCount(); i++){
            View child = lnGenre.getChildAt(i);
            if (child instanceof Button){
                Button button = (Button) child;
                setToggleOnTouchListener(button);
            }
        }


        for (int i = 0; i < lnTime.getChildCount(); i++){
            View child = lnTime.getChildAt(i);
            if (child instanceof Button){
                Button button = (Button) child;
                setToggleOnTouchListener(button);
            }
        }

        btnReset = dialog.findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lnRegions, lnGenre, lnTime;

                lnRegions = dialog.findViewById(R.id.lnRegions);
                for (int i = 0; i < lnRegions.getChildCount(); i++){
                    View child = lnRegions.getChildAt(i);
                    if (child instanceof Button){
                        Button button = (Button) child;
                        button.setPressed(false);
                    }
                }

                lnGenre = dialog.findViewById(R.id.lnGenre);
                for (int i = 0; i < lnGenre.getChildCount(); i++){
                    View child = lnGenre.getChildAt(i);
                    if (child instanceof Button){
                        Button button = (Button) child;
                        button.setPressed(false);
                    }
                }

                lnTime = dialog.findViewById(R.id.lnTime);
                for (int i = 0; i < lnTime.getChildCount(); i++){
                    View child = lnTime.getChildAt(i);
                    if (child instanceof Button){
                        Button button = (Button) child;
                        button.setPressed(false);
                    }
                }
            }
        });

        btnApply = dialog.findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setAdapterButtonList();

                filterRecView.setVisibility(View.VISIBLE);
                dialog.dismiss();

                CallApiGenres();

            }
        });
    }

    private void CallApiGenres(){

        String region = "";

        Map<String, String> regionMap = new HashMap<String, String>() {{
            put("Viet Nam", "vi");
            put("US", "en");
            put("South Korean", "ko");
            put("Japan", "ja");
            put("Hong Kong", "zh");
            put("France", "fr");
        }};

        Integer year = null;
        List<Integer> genreNumbers = new ArrayList<>();

        for (ButtonItem btn: buttonItemList){

            String btnText = btn.getButtonText().toString();

            if (regionMap.containsKey(btnText))
                region = btn.getRegion();
            else if (btnText.matches("\\d{4}")) {
                year = Integer.parseInt(btnText);
            } else if (btn.getIdGenre() != 0) {
                genreNumbers.add(btn.getIdGenre());
            }
        }

        Log.d("year - region - genres: ",  year + "-" + region + "-" + genreNumbers);

        filterMoviesList = new ArrayList<>();
        filterMoviesAdapter = new MovieBriefSmallAdapter(filterMoviesList, getContext());
        filter_genre_movie_recView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        filter_genre_movie_recView.setAdapter(filterMoviesAdapter);

        ApiInterface apiService = ApiClient.getMovieApi();
        mGenreMoviesResponse = apiService.getMoviesByGenre(Constants.API_KEY, region, year, genreNumbers, presentPage);
        mGenreMoviesResponse.enqueue(new Callback<GenreMoviesResponse>() {
            @Override
            public void onResponse(Call<GenreMoviesResponse> call, Response<GenreMoviesResponse> response) {
                if (!response.isSuccessful()){
                    mGenreMoviesResponse = call.clone();
                    mGenreMoviesResponse.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                for (MovieBrief movieBrief : response.body().getResults()){
                    if (movieBrief != null && movieBrief.getTitle() != null && movieBrief.getPosterPath() != null)
                        filterMoviesList.add(movieBrief);
                }

                filterMoviesAdapter.notifyDataSetChanged();
                if (response.body().getPage() == response.body().getTotalPages())
                    pagesOver = true;
                else
                    presentPage++;
            }

            @Override
            public void onFailure(Call<GenreMoviesResponse> call, Throwable t) {

            }
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setToggleOnTouchListener(Button button){
        button.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    boolean isPress = !button.isPressed();
                    button.setPressed(isPress);

                    ButtonItem buttonItem = new ButtonItem(button.getText().toString());
                    String buttonText = button.getText().toString();

                    if(buttonItemList.contains(buttonItem)){
                        int index = buttonItemList.indexOf(buttonItem);
                        buttonItem = buttonItemList.get(index);
                        buttonItem.setSelected(!buttonItem.isSelected());
                        removeButtonFromList(buttonText);
                    }else {
                        buttonItem.setSelected(true);

                        //set categoriesId
                        setCateId(buttonText, buttonItem);
                        //set genresId
                        setIdGenre(buttonText, buttonItem);
                        //set time
                        setTime(buttonText, buttonItem);
                        //set region
                        setRegion(buttonText, buttonItem);

                        buttonItemList.add(buttonItem);

                    }
                    button.setPressed(buttonItem.isSelected());
                }
                return true;
            }
        });
    }

    private void removeButtonFromList(String buttonText){
        for (Iterator<ButtonItem> iterator = buttonItemList.iterator(); iterator.hasNext(); ) {
            ButtonItem buttonItem = iterator.next();
            if (buttonItem.getButtonText().equals(buttonText)) {
                iterator.remove();
            }
        }
    }

    private void setIdGenre(String genres, ButtonItem buttonItem){
        switch (genres){
            case "Action":
                buttonItem.setIdGenre(Constants.ACTION_MOVIES_TYPE);
                break;
            case "Comedy":
                buttonItem.setIdGenre(Constants.COMEDY_MOVIES_TYPE);
                break;
            case "Romance":
                buttonItem.setIdGenre(Constants.ROMANCE_MOVIES_TYPE);
                break;
            case "Aventure":
                buttonItem.setIdGenre(Constants.ADVENTURE_MOVIES_TYPE);
                break;
            case "Animation":
                buttonItem.setIdGenre(Constants.ANIMATION_MOVIES_TYPE);
                break;
            case "Crime":
                buttonItem.setIdGenre(Constants.CRIME_MOVIES_TYPE);
                break;
            case "Documentary":
                buttonItem.setIdGenre(Constants.DOCUMENTARY_MOVIES_TYPE);
                break;
            case "Drama":
                buttonItem.setIdGenre(Constants.DRAMA_MOVIES_TYPE);
                break;
            case "Horror":
                buttonItem.setIdGenre(Constants.HORROR_MOVIES_TYPE);
                break;
            case "Scientific":
                buttonItem.setIdGenre(Constants.SCIFI_MOVIES_TYPE);
                break;
            case "Thriller":
                buttonItem.setIdGenre(Constants.THRILLER_MOVIES_TYPE);
                break;
            case "War":
                buttonItem.setIdGenre(Constants.WAR_MOVIES_TYPE);
                break;
            case "Western":
                buttonItem.setIdGenre(Constants.WESTERN_MOVIES_TYPE);
                break;
            default:
                break;
        }
    }

    private void setCateId(String cate, ButtonItem buttonItem){
        switch (cate){
            case "Movie":
                buttonItem.setMediaType("movie");
                break;
            case "TV Shows":
                buttonItem.setMediaType("tv");
                break;
            case "K-Drama":
                buttonItem.setMediaType("k-drama");
                break;
            case "Anime":
                buttonItem.setMediaType("anime");
                break;
            default:
                break;
        }
    }

    private void setTime(String time, ButtonItem buttonItem){
        switch (time){
            case "2024":
                buttonItem.setTime("2024");
                break;
            case "2023":
                buttonItem.setTime("2023");
                break;
            case "2022":
                buttonItem.setTime("2022");
                break;
            case "2021":
                buttonItem.setTime("2021");
                break;
            case "2020":
                buttonItem.setTime("2020");
                break;
            case "2019":
                buttonItem.setTime("2019");
                break;
            case "2018":
                buttonItem.setTime("2018");
                break;
            default:
                break;
        }
    }

    private void setRegion(String region, ButtonItem buttonItem){
        switch (region){
            case "Viet Nam":
                buttonItem.setRegion("vi");
                break;
            case "US":
                buttonItem.setRegion("en");
                break;
            case "Hong Kong":
                buttonItem.setRegion("zh");
                break;
            case "Japan":
                buttonItem.setRegion("ja");
                break;
            case "South Korea":
                buttonItem.setRegion("ko");
                break;
            default:
                break;
        }
    }

}
