package com.movieapi.movie.activity.movies;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.movieapi.movie.fragment.movies.PagerMoviesAdapter;
import com.movieapi.movie.R;
import com.movieapi.movie.adapter.CastAdapter;
import com.movieapi.movie.database.DatabaseHelper;
import com.movieapi.movie.database.movies.FavMovie;
import com.movieapi.movie.database.movies.MovieDatabase;
import com.movieapi.movie.databinding.ActivityMovieDetailsBinding;
import com.movieapi.movie.model.test.ApiResponse;
import com.movieapi.movie.model.recommend.RatingBody;
import com.movieapi.movie.model.test.RequestData;
import com.movieapi.movie.model.movie.Genre;
import com.movieapi.movie.model.movie.Movie;
import com.movieapi.movie.model.movie.MovieCastBrief;
import com.movieapi.movie.model.movie.MovieCreditsResponse;
import com.movieapi.movie.request.ApiLocal;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.request.ApiServiceLocal;
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
    List<MovieCastBrief> mCast;
    CastAdapter castAdapter;
    Call<Movie> mMovieDetailsCall;
    Call<MovieCreditsResponse> mMovieCreditsResponseCall;
    SharedPreferences prefUser, token;
    String userId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMovieDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent receivedItent = getIntent();

        movieId = receivedItent.getIntExtra("movie_id", -1);

        Log.d("movieId", movieId + "");

        if (movieId == -1 ) finish();

        final FavMovie favMovie = (FavMovie) getIntent().getSerializableExtra("name");

        binding.viewPagerMovieDetails.setAdapter(new PagerMoviesAdapter(getSupportFragmentManager(), MovieDetailsActivity.this));
        binding.tabViewPagerMovieDetails.setViewPager(binding.viewPagerMovieDetails);

        mCast = new ArrayList<>();
        castAdapter = new CastAdapter(MovieDetailsActivity.this, mCast);
        binding.movieDetailsCast.setAdapter(castAdapter);
        binding.movieDetailsCast.setLayoutManager(new LinearLayoutManager(MovieDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));

        //get userId
        prefUser = getSharedPreferences("sessionUser", MODE_PRIVATE);
        userId = prefUser.getString("idUser", "");

        addEvents();

        loadActivity(favMovie);
    }

    private void addEvents(){
        binding.movieDetailsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.movieDetailsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StreamMovie();
            }
        });

        binding.imvRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MovieDetailsActivity.this);
                dialog.setCanceledOnTouchOutside(true);
                dialog.setContentView(R.layout.dialog_rating);


                findViewById(dialog);

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();
            }
        });

        binding.imvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //shareOnSocial();
                sendDataToFlask();
            }
        });
    }

    private void findViewById(Dialog dialog){
        LinearLayout lnRatingBar;
        lnRatingBar = dialog.findViewById(R.id.lnRatingBar);

        for (int i = 0; i < lnRatingBar.getChildCount(); i++) {
            final int starIndex = i + 1; // 1-based index
            lnRatingBar.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateRating(lnRatingBar, starIndex);
                }
            });
        }
    }


    private void updateRating(LinearLayout ratingLayout, int selectedRating) {
        for (int i = 0; i < ratingLayout.getChildCount(); i++) {
            ImageView star = (ImageView) ratingLayout.getChildAt(i);
            if (i < selectedRating) {
                star.setImageResource(R.drawable.ic_filled_star); // Highlighted star
            } else {
                star.setImageResource(R.drawable.ic_outline_star); // Unselected star
            }
        }
        rating(selectedRating);
    }

    private void rating(float rating){

        RatingBody ratingBody = new RatingBody(userId, movieId, rating);
        ApiServiceLocal apiService = ApiLocal.getApiLocal();
        apiService.submitRating(ratingBody).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()){
                    Toast.makeText(MovieDetailsActivity.this, "Rating submitted successfully!", Toast.LENGTH_SHORT).show();
                }else{
                    String errorMessage = "Error: " + response.code() + " - " + response.message();
                    Toast.makeText(MovieDetailsActivity.this, "Failed to submit rating." + errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MovieDetailsActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendDataToFlask() {
        // Dữ liệu gửi đi
        RequestData requestData = new RequestData("Android User");

        // Lấy instance của Retrofit và ApiService
        ApiServiceLocal apiService = ApiLocal.getApiLocal();

        // Gửi yêu cầu POST
        apiService.sendData(requestData).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    // Phản hồi từ Flask
                    String message = response.body().getMessage();
                    Log.d("Retrofit", "Response: " + message);
                    Toast.makeText(getApplicationContext(), "Success: " + message, Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý lỗi
                    Log.e("Retrofit", "Error: " + response.code() + " - " + response.message());
                    Toast.makeText(getApplicationContext(), "Failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Lỗi kết nối
                Log.e("Retrofit", "Failure: " + t.getMessage());
                Toast.makeText(getApplicationContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void shareOnSocial(){

        Dialog dialog = new Dialog(MovieDetailsActivity.this);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_share);

        View cancel = dialog.findViewById(R.id.cancelDialog);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ImageView ins, fb, x, tiktok;

        ins = dialog.findViewById(R.id.imv_ins);
        ins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profilePath = "https://www.instagram.com/";
                String installPackageName = "com.instagram.android";
                toAnotherAppOpen(profilePath, installPackageName);
                        /*File img = new File("/storage/emulated/0/Download/pho.jpg");
                if(img.exists()){
                    Uri imageUri = FileProvider.getUriForFile(
                            MovieDetailsActivity.this,
                            "com.movieapi.movie.fileprovider",
                            img
                    );
                    shareToIns(imageUri);
                }*/

            }
        });

        fb = dialog.findViewById(R.id.imv_fb);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profilePath = "https://www.facebook.com/";
                String installPackageName = "com.facebook.katana";
                toAnotherAppOpen(profilePath, installPackageName);
            }
        });

        x = dialog.findViewById(R.id.imv_x);
        x.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profilePath = "https://www.twitter.com/";
                String installPackageName = "com.twitter.android";
                toAnotherAppOpen(profilePath, installPackageName);
            }
        });

        tiktok = dialog.findViewById(R.id.imv_tiktok);
        tiktok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profilePath = "https://www.tiktok.com/";
                String installPackageName = "com.aweme.opensdk.action.stay.in.dy";
                toAnotherAppOpen(profilePath, installPackageName);
            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }
    private void toAnotherAppOpen(String profilePath, String installPackageName) {
        String uri = String.valueOf(Uri.parse(profilePath));
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)).setPackage(installPackageName));
        }catch (Exception e){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        }
    }

   /* private void shareToIns(Uri imageUri){
        String profilePath = "https://www.instagram.com/_.dnm_/";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_STREAM, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(profilePath)).setPackage("com.instagram.android"));
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
            Toast.makeText(this, "Fail !", Toast.LENGTH_SHORT).show();
        }
    }*/

    public int getMovieId(){
        return movieId;
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

                setFavourite(response.body().getId(), response.body().getPoster_path(), response.body().getTitle(), response.body().getVote_average(), favMovie);
                setYear(response.body().getRelease_date());
                setGenres(response.body().getGenres());
                setDuration(response.body().getRuntime());
                setCast();
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void setFavourite(final Integer movieId, final String posterPath, final String movieTitle, final Double voteAverage, final FavMovie mFavMovie) {
        if (DatabaseHelper.isFavMovie(MovieDetailsActivity.this, movieId, userId)) {
            binding.movieDetailsFavouriteBtn.setTag(Constants.TAG_FAV);
            binding.movieDetailsFavouriteBtn.setImageResource(R.drawable.ic_favourite_filled);
            binding.movieDetailsFavouriteBtn.setColorFilter(Color.argb(1, 236, 116, 85));
        } else {
            binding.movieDetailsFavouriteBtn.setTag(Constants.TAG_NOT_FAV);
            binding.movieDetailsFavouriteBtn.setImageResource(R.drawable.ic_favourite_outlined);
        }

        binding.movieDetailsFavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                class SaveMovie extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        FavMovie favMovie = new FavMovie(movieId, movieTitle, posterPath, voteAverage, userId);
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
                                .deleteMovieById(movieId, userId);

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
            binding.movieDetailsGenreSeperator.setVisibility(View.VISIBLE);
        }
        binding.movieDetailsDuration.setText(detailsString);
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

    private void StreamMovie() {
        Intent iStreamMovie = new Intent(MovieDetailsActivity.this, StreamMovieActivity.class);
        iStreamMovie.putExtra("movie_id", imdbId);
        startActivity(iStreamMovie);
    }
}
