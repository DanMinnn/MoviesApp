package com.movieapi.movie.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.movieapi.movie.R;
import com.movieapi.movie.adapter.FavouritesPagerAdapter;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

public class FavouritesFragment extends Fragment {
    SmartTabLayout mSmartTabLayout;
    ViewPager mViewPager;


    public FavouritesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favourites, container, false);

        //mSmartTabLayout = view.findViewById(R.id.tab_view_pager_fav);
        mViewPager = view.findViewById(R.id.view_pager_fav);
        mViewPager.setAdapter(new FavouritesPagerAdapter(getChildFragmentManager(), getContext()));
        //mSmartTabLayout.setViewPager(mViewPager);

        return view;
    }

}
