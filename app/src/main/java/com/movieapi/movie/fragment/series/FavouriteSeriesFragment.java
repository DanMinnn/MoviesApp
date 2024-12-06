package com.movieapi.movie.fragment.series;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;

import com.movieapi.movie.adapter.series.FavSeriesAdapter;
import com.movieapi.movie.database.series.FavSeries;
import com.movieapi.movie.database.series.SeriesDatabase;
import com.movieapi.movie.databinding.FragmentFavouriteSeriesBinding;

import java.util.List;


public class FavouriteSeriesFragment extends Fragment {
    FragmentFavouriteSeriesBinding binding;
    SharedPreferences prefUser;
    private String userId;

    public FavouriteSeriesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFavouriteSeriesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefUser = getActivity().getApplicationContext().getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
        userId = prefUser.getString("idUser", "");

        final LiveData<List<FavSeries>> favSeries = SeriesDatabase.getInstance(getContext())
                .seriesDao()
                .getAllFavMovies(userId);

        favSeries.observe(requireActivity(), new Observer<List<FavSeries>>() {
            @Override
            public void onChanged(List<FavSeries> favSeries) {
                FavSeriesAdapter favSeriesAdapter = new FavSeriesAdapter(getContext(), favSeries);
                binding.favSeriesRecView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                binding.favSeriesRecView.setAdapter(favSeriesAdapter);

                if (favSeries.isEmpty())
                    binding.lnFavSeries.setVisibility(View.VISIBLE);
            }
        });
    }
}
