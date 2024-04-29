package com.movieapi.movie.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.movieapi.movie.R;
import com.movieapi.movie.activity.MovieDetailsActivity;
import com.movieapi.movie.adapter.TrailerAdapter;
import com.movieapi.movie.databinding.FragmentTrailerBinding;
import com.movieapi.movie.model.videos.Trailer;
import com.movieapi.movie.model.videos.TrailerResponse;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrailerFragment extends Fragment {
    FragmentTrailerBinding binding;
    TrailerAdapter trailerAdapter;
    List<Trailer> trailerList;
    Call<TrailerResponse> mMovieTrailersCall;
    int movieId;

    public TrailerFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTrailerBinding.inflate(inflater, container, false);

        MovieDetailsActivity activity = (MovieDetailsActivity) getActivity();
        movieId = activity.getMovieId();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        trailerList = new ArrayList<>();
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(binding.movieDetailsTrailer);
        trailerAdapter = new TrailerAdapter(getContext(), trailerList);
        binding.movieDetailsTrailer.setAdapter(trailerAdapter);
        binding.movieDetailsTrailer.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        Log.d("movieId", movieId + "");

        setTrailers();
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

                /*if(!trailerList.isEmpty()) {
                    binding.movieDetailsTrailerHeading.setVisibility(View.VISIBLE);
                }
*/
                trailerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<TrailerResponse> call, Throwable t) {

            }

        });
    }
}
