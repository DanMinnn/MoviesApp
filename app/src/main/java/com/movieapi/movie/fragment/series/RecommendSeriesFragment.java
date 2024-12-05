package com.movieapi.movie.fragment.series;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.movieapi.movie.activity.movies.MovieDetailsActivity;
import com.movieapi.movie.activity.series.SeriesDetailsActivity;
import com.movieapi.movie.adapter.movies.MovieBriefSmallAdapter;
import com.movieapi.movie.adapter.series.SeriesBriefSmallAdapter;
import com.movieapi.movie.databinding.FragmentRecommendBinding;
import com.movieapi.movie.model.movie.MovieBrief;
import com.movieapi.movie.model.movie.SimilarMovieResponse;
import com.movieapi.movie.model.series.SeriesBrief;
import com.movieapi.movie.model.series.SimilarSeriesResponse;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendSeriesFragment extends Fragment {
    FragmentRecommendBinding binding;
    List<SeriesBrief> seriesBriefList;
    SeriesBriefSmallAdapter seriesBriefSmallAdapter;
    Call<SimilarSeriesResponse> similarSeriesResponseCall;
    int seriesId;

    public RecommendSeriesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRecommendBinding.inflate(inflater, container, false);

        SeriesDetailsActivity activity = (SeriesDetailsActivity) getActivity();
        seriesId = activity.getSeriesId();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        seriesBriefList = new ArrayList<>();
        seriesBriefSmallAdapter = new SeriesBriefSmallAdapter(getContext(), seriesBriefList);
        binding.movieDetailsRecommend.setAdapter(seriesBriefSmallAdapter);
        binding.movieDetailsRecommend.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.HORIZONTAL, false));

        setSimilarMovie();
    }

    private void setSimilarMovie(){
        ApiInterface apiService = ApiClient.getMovieApi();
        similarSeriesResponseCall = apiService.getSimilarSeries(seriesId, Constants.API_KEY, 1);
        similarSeriesResponseCall.enqueue(new Callback<SimilarSeriesResponse>() {
            @Override
            public void onResponse(Call<SimilarSeriesResponse> call, Response<SimilarSeriesResponse> response) {
                if (!response.isSuccessful()){
                    similarSeriesResponseCall = call.clone();
                    similarSeriesResponseCall.enqueue(this);
                    return;
                }

                if (response.body() == null) return;
                if (response.body().getResults() == null) return;

                for(SeriesBrief seriesBrief : response.body().getResults()){
                    if (seriesBrief != null && seriesBrief.getName() != null && seriesBrief.getPosterPath() != null)
                        seriesBriefList.add(seriesBrief);
                }
                seriesBriefSmallAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<SimilarSeriesResponse> call, Throwable t) {

            }
        });
    }
}
