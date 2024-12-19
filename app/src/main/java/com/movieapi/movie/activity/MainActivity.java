package com.movieapi.movie.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

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

        binding.chatBox.setOnClickListener(v -> {
            ChatPanelDialog chatPanelDialog = new ChatPanelDialog(this);
            chatPanelDialog.setCancelable(true);

            /*// Đặt kích thước cụ thể
            Window window = chatPanelDialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Background trong suốt
                window.setLayout(600, 800); // Kích thước cố định: width = 600px, height = 800px
                window.setGravity(Gravity.END | Gravity.BOTTOM); // Hiển thị ở góc dưới bên phải

                WindowManager.LayoutParams params = window.getAttributes();
                params.x = 50; // Khoảng cách từ mép phải
                params.y = 50; // Khoảng cách từ mép dưới
                window.setAttributes(params);

                window.setWindowAnimations(R.style.DialogAnimation); // Hiệu ứng animation
            }*/

            chatPanelDialog.show();
        });
    }

}