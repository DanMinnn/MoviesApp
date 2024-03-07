package com.movieapi.movie.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.HapticFeedbackConstants;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.movieapi.movie.R;
import com.movieapi.movie.adapter.CastAdapter;
import com.movieapi.movie.adapter.TrailerAdapter;
import com.movieapi.movie.database.DatabaseHelper;
import com.movieapi.movie.database.movies.FavMovie;
import com.movieapi.movie.database.movies.MovieDatabase;
import com.movieapi.movie.databinding.ActivityMovieDetailsBinding;
import com.movieapi.movie.network.movie.Genre;
import com.movieapi.movie.network.movie.Movie;
import com.movieapi.movie.network.movie.MovieCastBrief;
import com.movieapi.movie.network.movie.MovieCreditsResponse;
import com.movieapi.movie.network.videos.Trailer;
import com.movieapi.movie.network.videos.TrailerResponse;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieDetailsActivity extends AppCompatActivity {
    ActivityMovieDetailsBinding binding;
    private int movieId;
    private String imdbId = "tt10676048";

    List<Trailer> trailerList;
    List<MovieCastBrief> mCast;

    TrailerAdapter trailerAdapter;
    CastAdapter castAdapter;

    Call<TrailerResponse> mMovieTrailersCall;
    Call<Movie> mMovieDetailsCall;
    Call<MovieCreditsResponse> mMovieCreditsResponseCall;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent receivedItent = getIntent();
        movieId = receivedItent.getIntExtra("movie_id", -1);

        trailerList = new ArrayList<>();
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.movieDetailsTrailer);
        trailerAdapter = new TrailerAdapter(MovieDetailsActivity.this, trailerList);
        binding.movieDetailsTrailer.setAdapter(trailerAdapter);
        binding.movieDetailsTrailer.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));

        mCast = new ArrayList<>();
        castAdapter = new CastAdapter(MovieDetailsActivity.this, mCast);
        binding.movieDetailsCast.setAdapter(castAdapter);
        binding.movieDetailsCast.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));

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

                setFavourite(response.body().getId(), response.body().getPoster_path(), response.body().getTitle(), favMovie);
                setYear(response.body().getRelease_date());
                setDuration(response.body().getRuntime());
                setGenres(response.body().getGenres());
                setTrailers();
                setCast();
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {

            }
        });
    }

    private void setFavourite(final Integer movieId, final String posterPath, final String movieTitle, final FavMovie mFavMovie) {
        if (DatabaseHelper.isFavMovie(MovieDetailsActivity.this, movieId)) {
            binding.movieDetailsFavouriteBtn.setTag(Constants.TAG_FAV);
            binding.movieDetailsFavouriteBtn.setImageResource(R.drawable.ic_favourite_filled);
            binding.movieDetailsFavouriteBtn.setColorFilter(Color.argb(1, 236, 116, 85));
        } else {
            binding.movieDetailsFavouriteBtn.setTag(Constants.TAG_NOT_FAV);
            binding.movieDetailsFavouriteBtn.setImageResource(R.drawable.ic_favourite_outlined);
        }

        binding.movieDetailsFavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View    view) {

                class SaveMovie extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        FavMovie favMovie = new FavMovie(movieId, movieTitle, posterPath);
                        MovieDatabase.getInstance(getApplicationContext())
                                .movieDao()
                                .insertMovie(favMovie);

                        return null;
                    }
                }

                class DeleteMovie extends AsyncTask<Void, Void, Void>{

                    @Override
                    protected Void doInBackground(Void... voids) {
                        MovieDatabase.getInstance(getApplicationContext())
                                .movieDao()
                                .deleteMovieById(movieId);

                        return null;
                    }
                }

                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if ((int) binding.movieDetailsFavouriteBtn.getTag() == Constants.TAG_FAV) {
                    binding.movieDetailsFavouriteBtn.setTag(Constants.TAG_NOT_FAV);
                    binding.movieDetailsFavouriteBtn.setImageResource(R.drawable.ic_favourite_outlined);
                    DeleteMovie deleteMovie = new DeleteMovie();
                    deleteMovie.execute();
                } else {
                    binding.movieDetailsFavouriteBtn.setTag(Constants.TAG_FAV);
                    binding.movieDetailsFavouriteBtn.setImageResource(R.drawable.ic_favourite_filled);
                    binding.movieDetailsFavouriteBtn.setColorFilter(Color.argb(1, 236, 116, 85));
                    SaveMovie saveMovie = new SaveMovie();
                    saveMovie.execute();
                }
            }
        });
    }

    private void setYear(String releaseDateString) {
        if (releaseDateString != null && !releaseDateString.trim().isEmpty()) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
            try {
                Date releaseDate = sdf1.parse(releaseDateString);
                binding.movieDetailsYear.setText(sdf2.format(releaseDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            binding.movieDetailsYear.setText("");
        }
    }

    private void setGenres(List<Genre> genresList) {
        String genres = "";
        if (genresList != null) {
            if(genresList.size() < 3) {
                for (int i = 0; i < genresList.size(); i++) {
                    if (genresList.get(i) == null) continue;
                    if (i == genresList.size() - 1) {
                        if(genresList.get(i).getGenreName().equals("Science Fiction")) {
                            genres = genres.concat("Sci-Fi");
                        } else {
                            genres = genres.concat(genresList.get(i).getGenreName());
                        }
                    } else {
                        if(genresList.get(i).getGenreName().equals("Science Fiction")) {
                            genres = genres.concat("Sci-Fi" + ", ");
                        } else {
                            genres = genres.concat(genresList.get(i).getGenreName() + ", ");
                        }
                    }
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    if (genresList.get(i) == null) continue;
                    if (i == 2) {
                        if(genresList.get(i).getGenreName().equals("Science Fiction")) {
                            genres = genres.concat("Sci-Fi");
                        } else {
                            genres = genres.concat(genresList.get(i).getGenreName());
                        }
                    } else {
                        if(genresList.get(i).getGenreName().equals("Science Fiction")) {
                            genres = genres.concat("Sci-Fi" + ", ");
                        } else {
                            genres = genres.concat(genresList.get(i).getGenreName() + ", ");
                        }
                    }
                }
            }
        }
        String year = binding.movieDetailsYear.getText().toString();
        if(!year.equals("") && !genres.equals("")){
            binding.movieDetailsYearSeperator.setVisibility(View.VISIBLE);
        }
        binding.movieDetailsGenre.setText(genres);
    }

    private void setDuration(Integer runtime){
        String detailsString = "";

        if (runtime != null && runtime != 0) {
            if (runtime < 60) {
                detailsString += runtime + " min(s)";
            } else {
                detailsString += runtime / 60 + " hr " + runtime % 60 + " mins";
            }
        }

        String genre = binding.movieDetailsGenre.getText().toString();
        if(!genre.equals("") && !detailsString.equals("")){
            binding.movieDetailsGenre.setVisibility(View.VISIBLE);
        }
        binding.movieDetailsDuration.setText(detailsString);
    }

    private void setTrailers() {
        ApiInterface apiService = ApiClient.getMovieApi();
        mMovieTrailersCall = apiService.getTrailerMovie(movieId, Constants.API_KEY);
        mMovieTrailersCall.enqueue(new Callback<TrailerResponse>() {
            @Override
            public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                if (!response.isSuccessful()) {
                    mMovieTrailersCall = call.clone();
                    mMovieTrailersCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getTrailers() == null) return;

                for (Trailer video : response.body().getTrailers()) {
                    if (video != null && video.getSite() != null && video.getSite().equals("YouTube") && video.getType() != null && video.getType().equals("Trailer"))
                        trailerList.add(video);
                }

                if(!trailerList.isEmpty()) {
                    binding.movieDetailsTrailerHeading.setVisibility(View.VISIBLE);
                }

                trailerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {

            }

        });
    }

    private void setCast(){
        ApiInterface apiService = ApiClient.getMovieApi();
        mMovieCreditsResponseCall = apiService.getMovieCredits(movieId, Constants.API_KEY);
        mMovieCreditsResponseCall.enqueue(new Callback<MovieCreditsResponse>() {
            @Override
            public void onResponse(Call<MovieCreditsResponse> call, Response<MovieCreditsResponse> response) {
                if(!response.isSuccessful()){
                    mMovieCreditsResponseCall = call.clone();
                    mMovieCreditsResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getCasts() == null) return;

                for (MovieCastBrief castBrief : response.body().getCasts()){
                    if (castBrief != null && castBrief.getName() != null)
                        mCast.add(castBrief);
                }

                if (!mCast.isEmpty())
                    binding.movieDetailsCastHeading.setVisibility(View.VISIBLE);

                castAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieCreditsResponse> call, Throwable t) {

            }
        });
    }
}
