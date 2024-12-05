package com.movieapi.movie.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.movieapi.movie.activity.SeriesDetailsActivity;
import com.movieapi.movie.adapter.EpisodesAdapter;
import com.movieapi.movie.databinding.FragmentEpisodesBinding;
import com.movieapi.movie.model.series.EpisodeBrief;
import com.movieapi.movie.model.series.SeasonDetailsResponse;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentEpisodes extends Fragment {
    FragmentEpisodesBinding binding;
    EpisodesAdapter episodesAdapter;
    List<EpisodeBrief> episodeBriefList;
    Call<SeasonDetailsResponse> seasonDetailsResponseCall;
    int seriesId;

    private Integer number_of_seasons = 0;

    public FragmentEpisodes(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentEpisodesBinding.inflate(inflater, container, false);

        SeriesDetailsActivity seriesDetailsActivity = (SeriesDetailsActivity) getActivity();
        seriesId = seriesDetailsActivity.getSeriesId();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        episodeBriefList = new ArrayList<>();
        episodesAdapter = new EpisodesAdapter(getContext(), episodeBriefList);
        binding.seriesDetailsEpisodes.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        binding.seriesDetailsEpisodes.setAdapter(episodesAdapter);

        setEpisodes(4);
    }



    private void setEpisodes(Integer id){
        ArrayList season_number = new ArrayList();
        for(int i=1; i<=number_of_seasons; i++){
            season_number.add("Season " + i);
        }

        ApiInterface apiInterface = ApiClient.getMovieApi();

        for (int seasonIndex = 1; seasonIndex <= season_number.size(); seasonIndex++){
            seasonDetailsResponseCall = apiInterface.getSeasonDetails(seriesId, seasonIndex, Constants.API_KEY);
            seasonDetailsResponseCall.enqueue(new Callback<SeasonDetailsResponse>() {
                @Override
                public void onResponse(Call<SeasonDetailsResponse> call, Response<SeasonDetailsResponse> response) {
                    if(!response.isSuccessful()){
                        seasonDetailsResponseCall.clone();
                        seasonDetailsResponseCall.enqueue(this);
                    }

                    if (response.body() == null) return;

                    for (EpisodeBrief episodeBrief : response.body().getEpisodes()) {
                        if (episodeBrief != null && episodeBrief.getName() != null && episodeBrief.getEpisodeNumber() != null)
                            episodeBriefList.add(episodeBrief);
                    }

                    episodesAdapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<SeasonDetailsResponse> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }
}
