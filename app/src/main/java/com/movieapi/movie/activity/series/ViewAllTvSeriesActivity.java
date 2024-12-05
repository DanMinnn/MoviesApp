package com.movieapi.movie.activity.series;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.movieapi.movie.adapter.series.SeriesBriefSmallAdapter;
import com.movieapi.movie.databinding.ActivityViewAllTvSeriesBinding;
import com.movieapi.movie.model.series.OnTheAirSeriesResponse;
import com.movieapi.movie.model.series.PopularSeriesResponse;
import com.movieapi.movie.model.series.SeriesBrief;
import com.movieapi.movie.model.series.TopRatedSeriesResponse;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllTvSeriesActivity extends AppCompatActivity {

    ActivityViewAllTvSeriesBinding binding;
    List<SeriesBrief> seriesList;
    SeriesBriefSmallAdapter seriesBriefSmallAdapter;

    int seriesType;
    boolean pageOver = false;
    int presentPage = 1;
    boolean loading = true;
    int previousTotal = 0;
    int visibleThreshold = 5;

    Call<OnTheAirSeriesResponse> onTheAirSeriesResponseCall;
    Call<PopularSeriesResponse> popularSeriesResponseCall;
    Call<TopRatedSeriesResponse> topRatedSeriesResponseCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAllTvSeriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.viewTvSeriesToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent receivedIntent = getIntent();
        seriesType = receivedIntent.getIntExtra(Constants.VIEW_ALL_TV_SHOWS_TYPE, -1);

        if (seriesType == -1) finish();

        switch (seriesType){
            case Constants.ON_THE_AIR_TV_SHOWS_TYPE:
                setTitle("On The Air");
                binding.viewTvSeriesToolbar.setTitleTextColor(Color.WHITE);
                break;
            case Constants.POPULAR_TV_SHOWS_TYPE:
                setTitle("Popular Series");
                binding.viewTvSeriesToolbar.setTitleTextColor(Color.WHITE);
                break;
            case Constants.TOP_RATED_TV_SHOWS_TYPE:
                setTitle("Top rated series");
                binding.viewTvSeriesToolbar.setTitleTextColor(Color.WHITE);
                break;
        }

        seriesList = new ArrayList<>();
        seriesBriefSmallAdapter = new SeriesBriefSmallAdapter(ViewAllTvSeriesActivity.this, seriesList);
        binding.viewTvSeriesRecView.setAdapter(seriesBriefSmallAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ViewAllTvSeriesActivity.this, 2);
        binding.viewTvSeriesRecView.setLayoutManager(gridLayoutManager);

        binding.viewTvSeriesRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

                if (loading){
                    if (totalItemCount > previousTotal){
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleItemCount)){
                    loadSeries(seriesType);
                    loading = true;
                }
            }
        });

        loadSeries(seriesType);
    }

    private void loadSeries(int seriesType){

        if (pageOver) return;

        ApiInterface apiInterface = ApiClient.getMovieApi();

        switch (seriesType){
            case Constants.ON_THE_AIR_TV_SHOWS_TYPE:
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
                                seriesList.add(seriesBrief);
                        }
                        seriesBriefSmallAdapter.notifyDataSetChanged();

                        if (response.body().getPage() == response.body().getTotalPages())
                            pageOver = true;
                        else
                            presentPage++;
                    }

                    @Override
                    public void onFailure(Call<OnTheAirSeriesResponse> call, Throwable t) {

                    }
                });
                break;
            case Constants.POPULAR_TV_SHOWS_TYPE:
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
                                seriesList.add(seriesBrief);
                        }
                        seriesBriefSmallAdapter.notifyDataSetChanged();

                        if (response.body().getPage() == response.body().getTotalPages()){
                            pageOver = true;
                        }else
                            presentPage++;
                    }

                    @Override
                    public void onFailure(Call<PopularSeriesResponse> call, Throwable t) {

                    }
                });
                break;
            case Constants.TOP_RATED_TV_SHOWS_TYPE:
                topRatedSeriesResponseCall = apiInterface.getTopRatedSeries(Constants.API_KEY, 1);
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
                                seriesList.add(seriesBrief);
                        }

                        seriesBriefSmallAdapter.notifyDataSetChanged();

                        if (response.body().getPage() == response.body().getTotalPages()){
                            pageOver = true;
                        }else
                            presentPage++;
                    }

                    @Override
                    public void onFailure(Call<TopRatedSeriesResponse> call, Throwable t) {

                    }
                });
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
