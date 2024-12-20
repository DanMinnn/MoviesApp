package com.movieapi.movie.fragment.movies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.movieapi.movie.R;
import com.movieapi.movie.adapter.movies.FavMoviesAdapter;
import com.movieapi.movie.database.movies.FavMovie;
import com.movieapi.movie.database.movies.MovieDatabase;

import java.util.List;

public class FavouritesMoviesFragment extends Fragment {
    RecyclerView favMoviesRecView;
    LinearLayout lnFavMovies;
    SharedPreferences prefUser;
    private String userId;

    public FavouritesMoviesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fav_movies_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        favMoviesRecView = view.findViewById(R.id.fav_movies_recView);
        lnFavMovies = view.findViewById(R.id.ln_fav_movies);

        prefUser = getActivity().getApplicationContext().getSharedPreferences("sessionUser", Context.MODE_PRIVATE);
        userId = prefUser.getString("idUser", "");

        final LiveData<List<FavMovie>> mFavMovies = MovieDatabase.getInstance(getContext())
                .movieDao()
                .getAllFavMovies(userId);

        mFavMovies.observe(requireActivity(), new Observer<List<FavMovie>>() {
            @Override
            public void onChanged(List<FavMovie> favMovies) {
                FavMoviesAdapter mFavMoviesAdapter = new FavMoviesAdapter(getContext(), favMovies);
                favMoviesRecView.setAdapter(mFavMoviesAdapter);
                favMoviesRecView.setLayoutManager(new GridLayoutManager(getContext(), 2));

                if (favMovies.isEmpty())
                    lnFavMovies.setVisibility(View.VISIBLE);
            }
        });
    }
}
