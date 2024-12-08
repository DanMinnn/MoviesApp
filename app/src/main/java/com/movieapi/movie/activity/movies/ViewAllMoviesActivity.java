package com.movieapi.movie.activity.movies;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.movieapi.movie.adapter.movies.MovieBriefSmallAdapter;
import com.movieapi.movie.databinding.ActivityViewAllMoviesBinding;
import com.movieapi.movie.model.movie.GenreMoviesResponse;
import com.movieapi.movie.model.movie.MovieBrief;
import com.movieapi.movie.model.movie.PopularMoviesResponse;
import com.movieapi.movie.model.movie.TopRatedMoviesResponse;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewAllMoviesActivity extends AppCompatActivity {
    ActivityViewAllMoviesBinding binding;
    List<MovieBrief> mMovies;
    MovieBriefSmallAdapter mMoviesAdapter;

    int mMovieType;
    boolean pagesOver = false;
    int presentPage = 1;
    boolean loading = true;
    int previousTotal = 0;
    int visibleThreshold = 5;

    Call<PopularMoviesResponse> mPopularMoviesCall;
    Call<TopRatedMoviesResponse> mTopRatedMovieCall;
    Call<GenreMoviesResponse> mGenreMoviesResponse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewAllMoviesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.viewMoviesToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent receivedIntend = getIntent();
        mMovieType = receivedIntend.getIntExtra(Constants.VIEW_ALL_MOVIES_TYPE, -1);

        if (mMovieType == -1) finish();

        switch (mMovieType){
            case Constants.POPULAR_MOVIES_TYPE:
                setTitle("Popular Movies");
                binding.viewMoviesToolbar.setTitleTextColor(Color.WHITE);
                break;
            case Constants.TOP_RATED_MOVIES_TYPE:
                setTitle("Top Rated Movies");
                binding.viewMoviesToolbar.setTitleTextColor(Color.WHITE);
                break;
        }

        mMovies = new ArrayList<>();
        mMoviesAdapter = new MovieBriefSmallAdapter(mMovies, ViewAllMoviesActivity.this);
        binding.viewMoviesRecView.setAdapter(mMoviesAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(ViewAllMoviesActivity.this, 2);
        binding.viewMoviesRecView.setLayoutManager(gridLayoutManager);

        binding.viewMoviesRecView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    loadMovies(mMovieType);
                    loading = true;
                }
            }
        });

        loadMovies(mMovieType);
    }

    private void loadMovies(int movieType){
        if (pagesOver) return;

        ApiInterface apiService = ApiClient.getMovieApi();

        switch (movieType){
            case Constants.POPULAR_MOVIES_TYPE:
                mPopularMoviesCall = apiService.getPopularMovies(Constants.API_KEY, presentPage);
                mPopularMoviesCall.enqueue(new Callback<PopularMoviesResponse>() {
                    @Override
                    public void onResponse(Call<PopularMoviesResponse> call, Response<PopularMoviesResponse> response) {
                        if (!response.isSuccessful()) {
                            mPopularMoviesCall = call.clone();
                            mPopularMoviesCall.enqueue(this);
                            return;
                        }

                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        for (MovieBrief movieBrief : response.body().getResults()) {
                            if (movieBrief != null && movieBrief.getTitle() != null && movieBrief.getPosterPath() != null)
                                mMovies.add(movieBrief);
                        }
                        mMoviesAdapter.notifyDataSetChanged();
                        if (response.body().getPage() == response.body().getTotalPages())
                            pagesOver = true;
                        else
                            presentPage++;
                        }

                    @Override
                    public void onFailure(Call<PopularMoviesResponse> call, Throwable t) {

                    }
                });
                break;
            case Constants.TOP_RATED_MOVIES_TYPE:
                mTopRatedMovieCall = apiService.getTopRatedMovies(Constants.API_KEY, presentPage, "US");
                mTopRatedMovieCall.enqueue(new Callback<TopRatedMoviesResponse>() {
                    @Override
                    public void onResponse(Call<TopRatedMoviesResponse> call, Response<TopRatedMoviesResponse> response) {
                        if (!response.isSuccessful()){
                            mTopRatedMovieCall = call.clone();
                            mTopRatedMovieCall.enqueue(this);
                            return;
                        }

                        if (response.body() == null) return;
                        if (response.body().getResults() == null) return;

                        for(MovieBrief movieBrief : response.body().getResults()){
                            if (movieBrief != null && movieBrief.getTitle() != null && movieBrief.getPosterPath() != null)
                                mMovies.add(movieBrief);
                        }

                        mMoviesAdapter.notifyDataSetChanged();
                        if (response.body().getPage() == response.body().getTotalPages()){
                            pagesOver = true;
                        }else
                            presentPage++;
                    }

                    @Override
                    public void onFailure(Call<TopRatedMoviesResponse> call, Throwable t) {

                    }
                });
                break;
            /*default:
                mGenreMoviesResponse = apiService.getMoviesByGenre(Constants.API_KEY, movieType, presentPage);
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
                                mMovies.add(movieBrief);
                        }

                        mMoviesAdapter.notifyDataSetChanged();
                        if (response.body().getPage() == response.body().getTotalPages())
                            pagesOver = true;
                        else
                            presentPage++;
                    }

                    @Override
                    public void onFailure(Call<GenreMoviesResponse> call, Throwable t) {

                    }
                });*/
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
