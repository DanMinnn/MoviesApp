package com.movieapi.movie.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.movieapi.movie.database.movies.FavMovie;
import com.movieapi.movie.databinding.ActivityMovieDetailsBinding;
import com.movieapi.movie.network.movie.Movie;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {
    ActivityMovieDetailsBinding binding;
    private int movieId;
    private String imdbId = "tt0137523";

    Call<Movie> mMovieDetailsCall;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent receivedItent = getIntent();
        movieId = receivedItent.getIntExtra("movie_id", -1);

        final FavMovie favMovie = (FavMovie) getIntent().getSerializableExtra("name");
        loadActivity(favMovie);
    }

    private void loadActivity(FavMovie favMovie){
        ApiInterface apiInterface = ApiClient.getMovieApi();
        mMovieDetailsCall = apiInterface.getMovieDetails(movieId, Constants.API_KEY);
        mMovieDetailsCall.enqueue(new Callback<Movie>() {
            @SuppressLint("CheckResult")
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if(!response.isSuccessful()){
                    mMovieDetailsCall = call.clone();
                    mMovieDetailsCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                imdbId = response.body().getImdb_id();
                binding.movieDetailsFab.setEnabled(true);

                Glide.with(getApplicationContext())
                        .load(Constants.IMAGE_LOADING_BASE_URL_1280 + response.body().getBackdrop_path())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                binding.movieDetailsProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                binding.movieDetailsProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(binding.movieDetailsImv);

                if (response.body().getTitle() != null)
                    binding.movieDetailsTitle.setText(response.body().getTitle());
                else
                    binding.movieDetailsTitle.setText("");

                if (response.body().getOverview() != null && !response.body().getOverview().trim().isEmpty()){
                    binding.movieDetailsStorylineHeading.setVisibility(View.VISIBLE);
                    binding.movieDetailsStorylineContent.setText(response.body().getOverview());
                }else
                    binding.movieDetailsStorylineContent.setText("");
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

            }
        });
    }
}
