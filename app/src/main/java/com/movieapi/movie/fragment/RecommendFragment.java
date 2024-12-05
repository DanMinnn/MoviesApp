package com.movieapi.movie.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.movieapi.movie.activity.MovieDetailsActivity;
import com.movieapi.movie.adapter.MovieBriefSmallAdapter;
import com.movieapi.movie.databinding.FragmentRecommendBinding;
import com.movieapi.movie.model.movie.MovieBrief;
import com.movieapi.movie.model.movie.SimilarMovieResponse;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendFragment extends Fragment {
    FragmentRecommendBinding binding;
    List<MovieBrief> mSimilarList;
    MovieBriefSmallAdapter mSimilarMovieAdapter;
    Call<SimilarMovieResponse> mSimilarMovieResponse;
    int movieId;
    public RecommendFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecommendBinding.inflate(inflater, container, false);

        MovieDetailsActivity activity = (MovieDetailsActivity) getActivity();
        movieId = activity.getMovieId();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mSimilarList = new ArrayList<>();
        mSimilarMovieAdapter = new MovieBriefSmallAdapter(mSimilarList, getContext());
        binding.movieDetailsRecommend.setAdapter(mSimilarMovieAdapter);
        binding.movieDetailsRecommend.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));

        setSimilarMovie();
    }

    private void setSimilarMovie(){
        ApiInterface apiService = ApiClient.getMovieApi();
        mSimilarMovieResponse = apiService.getSimilarMovie(movieId, Constants.API_KEY, 1);
        mSimilarMovieResponse.enqueue(new Callback<SimilarMovieResponse>() {
            @Override
            public void onResponse(Call<SimilarMovieResponse> call, Response<SimilarMovieResponse> response) {
                if (!response.isSuccessful()){
                    mSimilarMovieResponse = call.clone();
                    mSimilarMovieResponse.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                for(MovieBrief movieBrief : response.body().getResults()){
                    if (movieBrief != null && movieBrief.getTitle() != null && movieBrief.getPosterPath() != null)
                        mSimilarList.add(movieBrief);
                }
                mSimilarMovieAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SimilarMovieResponse> call, Throwable t) {

            }
        });
    }
}
