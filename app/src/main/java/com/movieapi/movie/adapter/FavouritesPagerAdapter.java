package com.movieapi.movie.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.movieapi.movie.Fragment.FavouritesFragment;
import com.movieapi.movie.Fragment.FavouritesMoviesFragment;
import com.movieapi.movie.Fragment.TrailerFragment;

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
        }
        return null;
    }

    @Override
    public int getCount() {
        return 1;
    }

    /*public CharSequence getPageTitle(int position){
        switch (position){
            case 0:
                return "Movies";
        }
        return null;
    }*/
}
