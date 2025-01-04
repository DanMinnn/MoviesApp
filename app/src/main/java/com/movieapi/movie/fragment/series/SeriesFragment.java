package com.movieapi.movie.fragment.series;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.movieapi.movie.activity.series.ViewAllTvSeriesActivity;
import com.movieapi.movie.adapter.series.MainSeriesAdapter;
import com.movieapi.movie.adapter.series.RecommendSeriesAdapter;
import com.movieapi.movie.adapter.series.SeriesCarouselAdapter;
import com.movieapi.movie.databinding.FragmentSeriesBinding;
import com.movieapi.movie.model.recommend.Recommendation;
import com.movieapi.movie.model.recommend.RecommendationsResponse;
import com.movieapi.movie.model.series.AiringTodaySeriesResponse;
import com.movieapi.movie.model.series.OnTheAirSeriesResponse;
import com.movieapi.movie.model.series.PopularSeriesResponse;
import com.movieapi.movie.model.series.SeriesBrief;
import com.movieapi.movie.model.series.TopRatedSeriesResponse;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.request.ApiLocal;
import com.movieapi.movie.request.ApiServiceLocal;
import com.movieapi.movie.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeriesFragment extends Fragment {

    FragmentSeriesBinding binding;
    //carousel
    LinearLayoutManager carouselLayoutManager;
    List<SeriesBrief> tvAirTodayList;
    SeriesCarouselAdapter seriesCarouselAdapter;

    //on the air
    List<SeriesBrief> tvOnTheAirList;
    MainSeriesAdapter tvOnTheAirAdapter;

    //popular
    List<SeriesBrief> tvPopularList;
    MainSeriesAdapter tvPopularAdapter;

    //popular
    List<SeriesBrief> tvTopRatedList;
    MainSeriesAdapter tvTopRatedAdapter;

    private boolean tvOnAirTodayLoaded;
    private boolean tvOnTheAirLoaded;
    private boolean tvPopularLoaded;
    private boolean tvTopRateLoaded;

    Call<AiringTodaySeriesResponse> airingTodaySeriesResponseCall;
    Call<OnTheAirSeriesResponse> onTheAirSeriesResponseCall;
    Call<PopularSeriesResponse> popularSeriesResponseCall;
    Call<TopRatedSeriesResponse> topRatedSeriesResponseCall;

    //Recommendation
    Call<RecommendationsResponse> recommendationsResponseCall;
    SharedPreferences prefUser;
    String userId;
    List<Recommendation> forUSeries;
    RecommendSeriesAdapter forUAdapter;
    private boolean forULoaded;

    Timer timer;
    TimerTask timerTask;
    int position;

    public SeriesFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSeriesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvOnAirTodayLoaded = false;
        tvOnTheAirLoaded = false;
        tvPopularLoaded = false;
        tvTopRateLoaded = false;
        forULoaded = false;

        tvAirTodayList = new ArrayList<>();
        tvOnTheAirList = new ArrayList<>();
        tvPopularList = new ArrayList<>();
        tvTopRatedList = new ArrayList<>();
        forUSeries = new ArrayList<>();

        //user id
        prefUser = getContext().getApplicationContext().getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
        userId = prefUser.getString("idUser", "");

        //carousel
        seriesCarouselAdapter = new SeriesCarouselAdapter(getContext(), tvAirTodayList);
        carouselLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.carouselSeriesRecView.setLayoutManager(carouselLayoutManager);
        binding.carouselSeriesRecView.setAdapter(seriesCarouselAdapter);

        //on the air
        forUAdapter = new RecommendSeriesAdapter(forUSeries, getContext());
        binding.forUSeriesRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.forUSeriesRecView.setAdapter(forUAdapter);

        // popular
        tvPopularAdapter = new MainSeriesAdapter(getContext(), tvPopularList);
        binding.popularSeriesRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.popularSeriesRecView.setAdapter(tvPopularAdapter);

        // top rated
        tvTopRatedAdapter = new MainSeriesAdapter(getContext(), tvTopRatedList);
        binding.topRatedSeriesRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.topRatedSeriesRecView.setAdapter(tvTopRatedAdapter);

        binding.carouselSeriesRecView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == 1)
                    stopAutoScrollCarousel();
                else if (newState == 0){
                    position = carouselLayoutManager.findFirstCompletelyVisibleItemPosition();
                    runAutoScrollingCarousel();
                }
            }
        });

        /*binding.viewAllForU.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iViewAllOnTheAir = new Intent(getContext(), ViewAllTvSeriesActivity.class);
                iViewAllOnTheAir.putExtra(Constants.VIEW_ALL_TV_SHOWS_TYPE, Constants.ON_THE_AIR_TV_SHOWS_TYPE);
                startActivity(iViewAllOnTheAir);
            }
        });*/

        binding.viewSeriesPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iViewAllPopular = new Intent(getContext(), ViewAllTvSeriesActivity.class);
                iViewAllPopular.putExtra(Constants.VIEW_ALL_TV_SHOWS_TYPE, Constants.POPULAR_TV_SHOWS_TYPE);
                startActivity(iViewAllPopular);
            }
        });

        binding.viewTopRatedSeries.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iViewAllTop = new Intent(getContext(), ViewAllTvSeriesActivity.class);
                iViewAllTop.putExtra(Constants.VIEW_ALL_TV_SHOWS_TYPE, Constants.TOP_RATED_TV_SHOWS_TYPE);
                startActivity(iViewAllTop);
            }
        });

        initViews();
    }

    private void stopAutoScrollCarousel(){
        if (timer != null && timerTask != null){
            timerTask.cancel();
            timer.cancel();
            timer = null;
            timerTask = null;
            position = carouselLayoutManager.findFirstCompletelyVisibleItemPosition();
        }
    }

    private void runAutoScrollingCarousel(){
        if(timer == null && timerTask == null){
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (position == tvAirTodayList.size() - 1){
                        binding.carouselSeriesRecView.post(new Runnable() {
                            @Override
                            public void run() {
                                position = 0;
                                binding.carouselSeriesRecView.smoothScrollToPosition(position);
                                binding.carouselSeriesRecView.smoothScrollBy(5, 0);
                            }
                        });
                    }else {
                        position++;
                        binding.carouselSeriesRecView.smoothScrollToPosition(position);
                    }
                }
            };
            timer.schedule(timerTask, 4000, 4000);
        }
    }

    private void initViews() {
        loadOnAirTodaySeries();
        loadRecommendation(userId);
        loadPopularMovie();
        loadTopRatedMovie();
    }

    private void loadOnAirTodaySeries(){
        ApiInterface apiInterface = ApiClient.getMovieApi();
        airingTodaySeriesResponseCall = apiInterface.getAiringTodaySeries(Constants.API_KEY, 1);
        airingTodaySeriesResponseCall.enqueue(new Callback<AiringTodaySeriesResponse>() {
            @Override
            public void onResponse(Call<AiringTodaySeriesResponse> call, Response<AiringTodaySeriesResponse> response) {
                if(!response.isSuccessful()){
                    airingTodaySeriesResponseCall = call.clone();
                    airingTodaySeriesResponseCall.enqueue(this);
                    return;
                }

                if(response.body() == null) return;
                if (response.body().getResults() == null) return;

                for (SeriesBrief seriesBrief : response.body().getResults()){
                    if (seriesBrief != null && seriesBrief.getBackdropPath() != null)
                        tvAirTodayList.add(seriesBrief);
                }
                seriesCarouselAdapter.notifyDataSetChanged();
                tvOnAirTodayLoaded = true;
                checkAllDataLoad();
            }
            @Override
            public void onFailure(Call<AiringTodaySeriesResponse> call, Throwable t) {

            }
        });
    }

    private void loadRecommendation(String userId){

        ApiServiceLocal apiService = ApiLocal.getApiLocal();
        recommendationsResponseCall = apiService.getRecommendationsSeries(userId);
        recommendationsResponseCall.enqueue(new Callback<RecommendationsResponse>() {
            @Override
            public void onResponse(Call<RecommendationsResponse> call, Response<RecommendationsResponse> response) {
                if (!response.isSuccessful()) {
                    recommendationsResponseCall = call.clone();
                    recommendationsResponseCall.enqueue(this);

                    Log.e("Retrofit", "Error: " + response.code() + " - " + response.message());
                    Toast.makeText(getContext(), "Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getRecommendations() == null) return;

                for (Recommendation series : response.body().getRecommendations()){
                    if (series.getItem_id() != 0 && series.getPredicted_rating() != 0){
                        forUSeries.add(series);
                    }
                }

                forULoaded = true;
                forUAdapter.notifyDataSetChanged();
                checkAllDataLoad();
            }

            @Override
            public void onFailure(Call<RecommendationsResponse> call, Throwable t) {
                Log.e("Error", "Network request failed in fragment: " + t.getMessage());
            }
        });
    }

    private void loadPopularMovie(){
        ApiInterface apiInterface = ApiClient.getMovieApi();
        popularSeriesResponseCall = apiInterface.getPopularSeries(Constants.API_KEY, 1);
        popularSeriesResponseCall.enqueue(new Callback<PopularSeriesResponse>() {
            @Override
            public void onResponse(Call<PopularSeriesResponse> call, Response<PopularSeriesResponse> response) {
                if(!response.isSuccessful()){
                    popularSeriesResponseCall = call.clone();
                    popularSeriesResponseCall.enqueue(this);
                    return;
                }

                if(response.body() == null) return;
                if (response.body().getResults() == null) return;

                for (SeriesBrief seriesBrief : response.body().getResults()){
                    if (seriesBrief != null && seriesBrief.getBackdropPath() != null)
                        tvPopularList.add(seriesBrief);
                }
                tvPopularAdapter.notifyDataSetChanged();
                tvPopularLoaded = true;
                checkAllDataLoad();
            }

            @Override
            public void onFailure(Call<PopularSeriesResponse> call, Throwable t) {

            }
        });
    }

    private void loadTopRatedMovie(){
        ApiInterface apiService = ApiClient.getMovieApi();
        topRatedSeriesResponseCall = apiService.getTopRatedSeries(Constants.API_KEY, 1);
        topRatedSeriesResponseCall.enqueue(new Callback<TopRatedSeriesResponse>() {
            @Override
            public void onResponse(Call<TopRatedSeriesResponse> call, Response<TopRatedSeriesResponse> response) {
                if (!response.isSuccessful()){
                    topRatedSeriesResponseCall = call.clone();
                    topRatedSeriesResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                for(SeriesBrief seriesBrief : response.body().getResults()){
                    if (seriesBrief != null && seriesBrief.getPosterPath() != null)
                        tvTopRatedList.add(seriesBrief);
                }

                tvTopRatedAdapter.notifyDataSetChanged();
                tvTopRateLoaded = true;
                checkAllDataLoad();
            }

            @Override
            public void onFailure(Call<TopRatedSeriesResponse> call, Throwable t) {

            }
        });
    }

    private void checkAllDataLoad() {
        if(tvOnAirTodayLoaded && forULoaded && tvPopularLoaded && tvTopRateLoaded){

            binding.movieProgressBar.setVisibility(View.GONE);
            binding.carouselSeriesRecView.setVisibility(View.VISIBLE);

            binding.forUHeadingSeries.setVisibility(View.VISIBLE);
            binding.forUSeriesRecView.setVisibility(View.VISIBLE);

            binding.popularSeriesHeading.setVisibility(View.VISIBLE);
            binding.popularSeriesRecView.setVisibility(View.VISIBLE);

            binding.topRatedSeriesHeading.setVisibility(View.VISIBLE);
            binding.topRatedSeriesRecView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoScrollCarousel();
    }

    @Override
    public void onResume() {
        super.onResume();
        runAutoScrollingCarousel();
    }
}
