package com.movieapi.movie.fragment.series;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.movieapi.movie.activity.series.ViewAllTvSeriesActivity;
import com.movieapi.movie.adapter.series.MainSeriesAdapter;
import com.movieapi.movie.adapter.series.SeriesCarouselAdapter;
import com.movieapi.movie.databinding.FragmentSeriesBinding;
import com.movieapi.movie.model.series.AiringTodaySeriesResponse;
import com.movieapi.movie.model.series.OnTheAirSeriesResponse;
import com.movieapi.movie.model.series.PopularSeriesResponse;
import com.movieapi.movie.model.series.SeriesBrief;
import com.movieapi.movie.model.series.TopRatedSeriesResponse;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
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

        tvAirTodayList = new ArrayList<>();
        tvOnTheAirList = new ArrayList<>();
        tvPopularList = new ArrayList<>();
        tvTopRatedList = new ArrayList<>();

        //carousel
        seriesCarouselAdapter = new SeriesCarouselAdapter(getContext(), tvAirTodayList);
        carouselLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.carouselSeriesRecView.setLayoutManager(carouselLayoutManager);
        binding.carouselSeriesRecView.setAdapter(seriesCarouselAdapter);

        //on the air
        tvOnTheAirAdapter = new MainSeriesAdapter(getContext(), tvOnTheAirList);
        binding.ontheairSeriesRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.ontheairSeriesRecView.setAdapter(tvOnTheAirAdapter);

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

        binding.viewOnTheAir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iViewAllOnTheAir = new Intent(getContext(), ViewAllTvSeriesActivity.class);
                iViewAllOnTheAir.putExtra(Constants.VIEW_ALL_TV_SHOWS_TYPE, Constants.ON_THE_AIR_TV_SHOWS_TYPE);
                startActivity(iViewAllOnTheAir);
            }
        });

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
        loadOnTheAir();
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

    private void loadOnTheAir(){
        ApiInterface apiInterface = ApiClient.getMovieApi();
        onTheAirSeriesResponseCall = apiInterface.getOnTheAirSeries(Constants.API_KEY, 1);
        onTheAirSeriesResponseCall.enqueue(new Callback<OnTheAirSeriesResponse>() {
            @Override
            public void onResponse(Call<OnTheAirSeriesResponse> call, Response<OnTheAirSeriesResponse> response) {
                if(!response.isSuccessful()){
                    onTheAirSeriesResponseCall = call.clone();
                    onTheAirSeriesResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                for (SeriesBrief seriesBrief : response.body().getResults()){
                    if (seriesBrief != null && seriesBrief.getBackdropPath() != null)
                        tvOnTheAirList.add(seriesBrief);
                }
                tvOnTheAirAdapter.notifyDataSetChanged();
                tvOnTheAirLoaded = true;
                checkAllDataLoad();
            }

            @Override
            public void onFailure(Call<OnTheAirSeriesResponse> call, Throwable t) {

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
        if(tvOnTheAirLoaded && tvOnAirTodayLoaded && tvPopularLoaded && tvTopRateLoaded){

            binding.movieProgressBar.setVisibility(View.GONE);
            binding.carouselSeriesRecView.setVisibility(View.VISIBLE);

            binding.onTheAirHeading.setVisibility(View.VISIBLE);
            binding.ontheairSeriesRecView.setVisibility(View.VISIBLE);

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
