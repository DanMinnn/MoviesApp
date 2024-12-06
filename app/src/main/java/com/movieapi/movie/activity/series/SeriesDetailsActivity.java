package com.movieapi.movie.activity.series;

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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.movieapi.movie.R;
import com.movieapi.movie.adapter.series.SeriesCastAdapter;
import com.movieapi.movie.controller.SharedViewModel;
import com.movieapi.movie.database.DatabaseHelper;
import com.movieapi.movie.database.series.FavSeries;
import com.movieapi.movie.database.series.SeriesDatabase;
import com.movieapi.movie.databinding.ActivitySeriesDetailsBinding;
import com.movieapi.movie.fragment.movies.PagerMoviesAdapter;
import com.movieapi.movie.fragment.series.PagerSeriesAdapter;
import com.movieapi.movie.model.series.Genre;
import com.movieapi.movie.model.series.Series;
import com.movieapi.movie.model.series.SeriesCastBrief;
import com.movieapi.movie.model.series.SeriesCreditsResponse;
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

public class SeriesDetailsActivity extends AppCompatActivity {

    ActivitySeriesDetailsBinding binding;
    private int seriesId;
    private String imdbId = "";

    private Integer number_of_seasons = 0;
    List<SeriesCastBrief> seriesCast;
    SeriesCastAdapter seriesCastAdapter;
    Call<Series> seriesDetailsCall;
    Call<SeriesCreditsResponse> seriesCreditsResponseCall;
    SharedPreferences prefUser;
    String userId;
    private int id_episode = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeriesDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent receivedIntent = getIntent();
        seriesId = receivedIntent.getIntExtra("series_id", -1);

        if (seriesId == -1) finish();

        final FavSeries favSeries = (FavSeries) getIntent().getSerializableExtra("name");

        binding.viewPagerSeriesDetails.setAdapter(new PagerSeriesAdapter(getSupportFragmentManager(), SeriesDetailsActivity.this));
        binding.tabViewPagerSeriesDetails.setViewPager(binding.viewPagerSeriesDetails);

        seriesCast = new ArrayList<>();
        seriesCastAdapter = new SeriesCastAdapter(SeriesDetailsActivity.this, seriesCast);
        binding.seriesDetailsCast.setLayoutManager(new LinearLayoutManager(SeriesDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        binding.seriesDetailsCast.setAdapter(seriesCastAdapter);

        //get userId
        prefUser = getSharedPreferences("sessionUser", MODE_PRIVATE);
        userId = prefUser.getString("idUser", "");

        addEvents();

        loadActivity(favSeries);
    }

    private void addEvents() {
        binding.seriesDetailsBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.seriesDetailsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StreamSeries();
            }
        });

        binding.imvShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    public int getSeriesId(){
        return seriesId;
    }
    private void share(){
        Dialog dialog = new Dialog(SeriesDetailsActivity.this);
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

    private void loadActivity(FavSeries favSeries) {
        ApiInterface apiInterface = ApiClient.getMovieApi();
        seriesDetailsCall = apiInterface.getSeriesDetails(seriesId, Constants.API_KEY, "external_ids");
        seriesDetailsCall.enqueue(new Callback<Series>() {
            @SuppressLint("CheckResult")
            @Override
            public void onResponse(Call<Series> call, Response<Series> response) {
                if(!response.isSuccessful()){
                    seriesDetailsCall = call.clone();
                    seriesDetailsCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                imdbId = response.body().getExternalIds().getImdbId();

                binding.seriesDetailsFab.setEnabled(true);

                Glide.with(getApplicationContext())
                        .load(Constants.IMAGE_LOADING_BASE_URL_1280 + response.body().getBackdropPath())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                binding.seriesDetailsProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                binding.seriesDetailsProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(binding.seriesDetailsImv);

                if (response.body().getName() != null)
                    binding.seriesDetailsTitle.setText(response.body().getName());
                else
                    binding.seriesDetailsTitle.setText("");

                if (response.body().getOverview() != null && !response.body().getOverview().trim().isEmpty()){
                    binding.seriesDetailsStorylineHeading.setVisibility(View.VISIBLE);
                    binding.seriesDetailsStorylineContent.setText(response.body().getOverview());

                    if(binding.seriesDetailsStorylineContent.getLineCount() >= 5)
                        binding.contentReadmore.setVisibility(View.VISIBLE);
                    else
                        binding.contentReadmore.setVisibility(View.GONE);

                    binding.contentReadmore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (binding.contentReadmore.getText() == "Read more"){
                                binding.seriesDetailsStorylineContent.setMaxLines(Integer.MAX_VALUE);
                                binding.contentReadmore.setText("Read less");
                            }else {
                                binding.seriesDetailsStorylineContent.setMaxLines(7);
                                binding.contentReadmore.setText("Read more");
                            }
                        }
                    });
                }else
                    binding.seriesDetailsStorylineContent.setText("");

                setFavourite(response.body().getId(), response.body().getPosterPath(), response.body().getName(), response.body().getVoteAverage(), favSeries);
                setYear(response.body().getFirstAirDate());
                setGenres(response.body().getGenres());
                setSeasons(response.body().getNumberOfSeasons());
                setCast();

                id_episode = response.body().getId();
                number_of_seasons = response.body().getNumberOfSeasons();
                handleEpisodeId(id_episode, number_of_seasons);
            }

            @Override
            public void onFailure(Call<Series> call, Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void handleEpisodeId(int id, int number_of_seasons) {
        // Xử lý id_episode sau khi được gán.
        Log.d("handleEpisodeId", "Processed id_episode: " + id);

        SharedViewModel viewModel= new ViewModelProvider(this).get(SharedViewModel.class);
        viewModel.setIdEpisode(id);
        viewModel.setNumberOfSeason(number_of_seasons);
    }


    private void setFavourite(final Integer seriesId, final String posterPath, final String seriesName, final Double vote, final FavSeries favSeries) {
        if (DatabaseHelper.isFavSeries(SeriesDetailsActivity.this, seriesId, userId)) {
            binding.seriesDetailsFavouriteBtn.setTag(Constants.TAG_FAV);
            binding.seriesDetailsFavouriteBtn.setImageResource(R.drawable.ic_favourite_filled);
            binding.seriesDetailsFavouriteBtn.setColorFilter(Color.argb(1, 236, 116, 85));
        } else {
            binding.seriesDetailsFavouriteBtn.setTag(Constants.TAG_NOT_FAV);
            binding.seriesDetailsFavouriteBtn.setImageResource(R.drawable.ic_favourite_outlined);
        }

        binding.seriesDetailsFavouriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                class SaveSeries extends AsyncTask<Void, Void, Void> {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        FavSeries favSeries = new FavSeries(userId, seriesId, posterPath, seriesName, vote);
                        SeriesDatabase.getInstance(getApplicationContext())
                                .seriesDao()
                                .insertMovie(favSeries);

                        return null;
                    }
                }

                class DeleteSeries extends AsyncTask<Void, Void, Void>{

                    @Override
                    protected Void doInBackground(Void... voids) {
                        SeriesDatabase.getInstance(getApplicationContext())
                                .seriesDao()
                                .deleteSeriesById(seriesId, userId);

                        return null;
                    }
                }

                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                if ((int) binding.seriesDetailsFavouriteBtn.getTag() == Constants.TAG_FAV) {
                    binding.seriesDetailsFavouriteBtn.setTag(Constants.TAG_NOT_FAV);
                    binding.seriesDetailsFavouriteBtn.setImageResource(R.drawable.ic_favourite_outlined);
                    DeleteSeries deleteSeries = new DeleteSeries();
                    deleteSeries.execute();
                } else {
                    binding.seriesDetailsFavouriteBtn.setTag(Constants.TAG_FAV);
                    binding.seriesDetailsFavouriteBtn.setImageResource(R.drawable.ic_favourite_filled);
                    binding.seriesDetailsFavouriteBtn.setColorFilter(Color.argb(1, 236, 116, 85));
                    SaveSeries saveSeries = new SaveSeries();
                    saveSeries.execute();
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
                binding.seriesDetailsYear.setText(sdf2.format(releaseDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            binding.seriesDetailsYear.setText("");
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
        String year = binding.seriesDetailsYear.getText().toString();
        if(!year.equals("") && !genres.equals("")){
            binding.seriesDetailsYearSeperator.setVisibility(View.VISIBLE);
        }
        binding.seriesDetailsGenre.setText(genres);
    }

    private void setSeasons(Integer numberOfSeasons){
        if (numberOfSeasons == 1){
            binding.seriesDetailsDuration.setText(numberOfSeasons.toString() + " season");
        } else {
            binding.seriesDetailsDuration.setText(numberOfSeasons.toString() + " seasons");
        }
        number_of_seasons = numberOfSeasons;
        binding.seriesDetailsGenreSeperator.setVisibility(View.VISIBLE);
    }

    private void setCast(){
        ApiInterface apiService = ApiClient.getMovieApi();
        seriesCreditsResponseCall = apiService.getSeriesCredits(seriesId, Constants.API_KEY);
        seriesCreditsResponseCall.enqueue(new Callback<SeriesCreditsResponse>() {
            @Override
            public void onResponse(Call<SeriesCreditsResponse> call, Response<SeriesCreditsResponse> response) {
                if(!response.isSuccessful()){
                    seriesCreditsResponseCall = call.clone();
                    seriesCreditsResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getCasts() == null) return;

                for (SeriesCastBrief castBrief : response.body().getCasts()){
                    if (castBrief != null && castBrief.getName() != null)
                        seriesCast.add(castBrief);
                }

                if (!seriesCast.isEmpty())
                    binding.seriesDetailsCastHeading.setVisibility(View.VISIBLE);

                seriesCastAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SeriesCreditsResponse> call, Throwable t) {

            }
        });
    }

    private void StreamSeries() {
        Intent iStreamMovie = new Intent(SeriesDetailsActivity.this, SeriesStreamActivity.class);
        iStreamMovie.putExtra("series_id", imdbId);
        startActivity(iStreamMovie);
    }
}
