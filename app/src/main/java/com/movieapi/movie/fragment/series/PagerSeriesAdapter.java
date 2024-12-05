package com.movieapi.movie.fragment.series;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.movieapi.movie.fragment.movies.CommentsMoviesFragment;
import com.movieapi.movie.fragment.movies.RecommendMoviesFragment;
import com.movieapi.movie.fragment.movies.TrailerMoviesFragment;

public class PagerSeriesAdapter extends FragmentPagerAdapter {

    Context context;

    public PagerSeriesAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new TrailerSeriesFragment();
            case 1:
                return new FragmentEpisodes();
            case 2:
                return new RecommendSeriesFragment();
            case 3:
                return new CommentsSeriesFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Trailers";
            case 1:
                return "Episodes";
            case 2:
                return "More Like This";
            case 3:
                return "Comments";
        }
        return null;
    }
}
