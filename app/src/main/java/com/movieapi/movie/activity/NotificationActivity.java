package com.movieapi.movie.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.movieapi.movie.R;
import com.movieapi.movie.databinding.ActivityNotificationBinding;
import com.movieapi.movie.request.FetchMoviesWorker;

public class NotificationActivity extends AppCompatActivity {
    ActivityNotificationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarNotic);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        addEvents();
    }

    private void addEvents(){
        binding.toolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.switchRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkRequest workRequest = new OneTimeWorkRequest.Builder(FetchMoviesWorker.class).build();
                WorkManager.getInstance(NotificationActivity.this).enqueue(workRequest);
            }
        });
    }
}