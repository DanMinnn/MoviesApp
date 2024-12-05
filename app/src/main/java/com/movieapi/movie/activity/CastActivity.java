package com.movieapi.movie.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.movieapi.movie.adapter.MovieCastOfPersonAdapter;
import com.movieapi.movie.databinding.ActivityCastBinding;
import com.movieapi.movie.model.cast.Person;
import com.movieapi.movie.model.movie.MovieCastOfPerson;
import com.movieapi.movie.model.movie.MovieCastsOfPersonResponse;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CastActivity extends AppCompatActivity {
    ActivityCastBinding binding;
    int personId;

    List<MovieCastOfPerson> mCastOfPersonList;

    MovieCastOfPersonAdapter movieCastOfPersonAdapter;

    Call<Person> mPersonDetailsCall;

    Call<MovieCastsOfPersonResponse> mCastsOfPersonResponsesCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCastBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent receivedIntent = getIntent();
        personId = receivedIntent.getIntExtra("person_id", -1);

        if (personId == -1) finish();

        mCastOfPersonList = new ArrayList<>();
        movieCastOfPersonAdapter = new MovieCastOfPersonAdapter(CastActivity.this, mCastOfPersonList);
        binding.castMovieRecView.setAdapter(movieCastOfPersonAdapter);
        binding.castMovieRecView.setLayoutManager(new LinearLayoutManager(CastActivity.this, LinearLayoutManager.HORIZONTAL, false));

        loadActivity();
        binding.castBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadActivity(){
        ApiInterface apiService = ApiClient.getMovieApi();
        mPersonDetailsCall = apiService.getPersonDetails(personId, Constants.API_KEY);
        mPersonDetailsCall.enqueue(new Callback<Person>() {
            @Override
            public void onResponse(Call<Person> call, Response<Person> response) {
                if (!response.isSuccessful()){
                    mPersonDetailsCall = call.clone();
                    mPersonDetailsCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;

                Glide.with(getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_1280 + response.body().getProfile_path())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                binding.castProgressBar.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                binding.castProgressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(binding.castImageView);

                if(response.body().getName() != null)
                    binding.castName.setText(response.body().getName());
                else
                    binding.castName.setText("");

                if (response.body().getPlace_of_birth() != null && !response.body().getPlace_of_birth().trim().isEmpty())
                    binding.castBirthplace.setText(response.body().getPlace_of_birth());

                if (response.body().getBiography() != null && !response.body().getBiography().trim().isEmpty()){
                    binding.castBioHeading.setVisibility(View.VISIBLE);
                    binding.castBio.setText(response.body().getBiography());

                    if (binding.castBio.getLineCount() == 7)
                        binding.castReadmore.setVisibility(View.VISIBLE);
                    else
                        binding.castReadmore.setVisibility(View.GONE);

                    binding.castReadmore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (binding.castReadmore.getText() == "Read more"){
                                binding.castBio.setMaxLines(Integer.MAX_VALUE);
                                binding.castReadmore.setText("Read less");
                            }else {
                                binding.castBio.setMaxLines(7);
                                binding.castReadmore.setText("Read more");
                            }
                        }
                    });
                }
                setAge(response.body().getDateOfBirth());
                setMovieCast(response.body().getId());
            }

            @Override
            public void onFailure(Call<Person> call, Throwable t) {

            }
        });
    }

    private void setAge(String dateOfBirthString) {
        if (dateOfBirthString != null && !dateOfBirthString.trim().isEmpty()){
            SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sf2 = new SimpleDateFormat("yyyy");
            Date releaseDate = null;
            try {
                releaseDate = sf1.parse(dateOfBirthString);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            binding.castAge.setText((Calendar.getInstance().get(Calendar.YEAR) - Integer.parseInt(sf2.format(releaseDate))) + "");
        }
    }

    private void setMovieCast(Integer personId){
        ApiInterface apiService = ApiClient.getMovieApi();
        mCastsOfPersonResponsesCall = apiService.getMovieCastOfPerson(personId, Constants.API_KEY);
        mCastsOfPersonResponsesCall.enqueue(new Callback<MovieCastsOfPersonResponse>() {
            @SuppressLint("SuspiciousIndentation")
            @Override
            public void onResponse(Call<MovieCastsOfPersonResponse> call, Response<MovieCastsOfPersonResponse> response) {
                if (!response.isSuccessful()){
                    mCastsOfPersonResponsesCall = call.clone();
                    mCastsOfPersonResponsesCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getCasts() == null) return;

                for (MovieCastOfPerson movieCastOfPerson : response.body().getCasts()){
                    if (movieCastOfPerson != null && movieCastOfPerson.getPosterPath() != null)
                        binding.castMovieHeading.setVisibility(View.VISIBLE);
                        mCastOfPersonList.add(movieCastOfPerson);
                }

                movieCastOfPersonAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<MovieCastsOfPersonResponse> call, Throwable t) {

            }
        });

    }
}
