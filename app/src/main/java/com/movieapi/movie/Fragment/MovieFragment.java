package com.movieapi.movie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.movieapi.movie.R;

import java.util.List;

public class MovieFragment extends Fragment {
    ProgressBar mProgressBar;
    TextView view_popular, view_top_rated;
    NestedScrollView fragment_movie_scrollView;
    RecyclerView carousel_recView, popular_recView, top_rated_recView;


    public MovieFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = view.findViewById(R.id.movie_progressBar);
        view_popular = view.findViewById(R.id.view_popular);
        view_top_rated = view_popular.findViewById(R.id.view_top_rated);

        fragment_movie_scrollView = view.findViewById(R.id.fragment_movie_scrollView);

        carousel_recView = view.findViewById(R.id.carousel_recView);
        popular_recView = view.findViewById(R.id.popular_recView);
        top_rated_recView = view.findViewById(R.id.top_rated_recView);


    }
}
