package com.movieapi.movie.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.navigation.NavigationBarView;
import com.movieapi.movie.fragment.movies.FavouritesFragment;
import com.movieapi.movie.fragment.movies.MovieFragment;
import com.movieapi.movie.fragment.ProfileFragment;
import com.movieapi.movie.fragment.SearchFragment;
import com.movieapi.movie.fragment.series.SeriesFragment;
import com.movieapi.movie.R;
import com.movieapi.movie.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MovieFragment()).commit();
        binding.titleToolbar.setText(R.string.home_nav);

        binding.bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_movie:
                        if(!getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getSimpleName().equals("MovieFragment")){
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MovieFragment()).commit();
                            binding.titleToolbar.setText(R.string.home_nav);
                        }
                        break;

                    case R.id.nav_series:
                        if(!getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getSimpleName().equals("SeriesFragment")){
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SeriesFragment()).commit();
                            binding.titleToolbar.setText(getString(R.string.series_nav));
                        }
                        break;

                    case R.id.nav_search:
                        if(!getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getSimpleName().equals("SearchFragment")){
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
                            binding.titleToolbar.setText(R.string.search_nav);
                        }
                        break;

                    case R.id.nav_favourites:
                        if(!getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getSimpleName().equals("FavouritesFragment")){
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FavouritesFragment()).commit();
                            binding.titleToolbar.setText(R.string.favourites_nav);
                        }
                        break;
                    case R.id.nav_profile:
                        if(!getSupportFragmentManager().findFragmentById(R.id.fragment_container).getClass().getSimpleName().equals("ProfileFragment")){
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                            binding.titleToolbar.setText("Profile");
                        }
                        break;
                }
                return true;
            }
        });
    }
}