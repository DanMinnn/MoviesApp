package com.movieapi.movie.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.movieapi.movie.fragment.movies.FavouritesMoviesFragment;
import com.movieapi.movie.fragment.series.FavouriteSeriesFragment;

public class FavouritesPagerAdapter extends FragmentPagerAdapter {
    Context context;

    public FavouritesPagerAdapter(@NonNull FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FavouritesMoviesFragment();
            case 1:
                return new FavouriteSeriesFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Movies";
            case 1:
                return "Series";
        }
        return null;
    }
}
