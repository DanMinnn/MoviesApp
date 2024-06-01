package com.movieapi.movie.activity;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.movieapi.movie.R;
import com.movieapi.movie.databinding.ActivityNotificationBinding;
import com.movieapi.movie.request.FetchMoviesWorker;
import com.movieapi.movie.utils.Constants;

public class NotificationActivity extends AppCompatActivity {
    ActivityNotificationBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarNotic);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setSwitchStates(binding.lnNotification);

        addEvents();
    }

    private void addEvents(){
        binding.toolbarBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.switchGeneral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean switchState = binding.switchGeneral.isChecked();

                SharedPreferences.Editor editor = getSharedPreferences("SwitchNotic", MODE_PRIVATE).edit();
                editor.putBoolean("SwitchState_" + binding.switchGeneral.getTag(), binding.switchGeneral.isChecked());
                editor.commit();

                if (switchState){
                    WorkRequest workRequest = new OneTimeWorkRequest.Builder(FetchMoviesWorker.class).build();
                    WorkManager.getInstance(NotificationActivity.this).enqueue(workRequest);
                }else {
                    WorkManager.getInstance(NotificationActivity.this).cancelAllWork();
                }
            }
        });

        binding.switchArrival.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("SwitchNotic", MODE_PRIVATE).edit();
                editor.putBoolean("SwitchState_" + binding.switchArrival.getTag(), binding.switchArrival.isChecked());
                editor.commit();
            }
        });

        binding.switchService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("SwitchNotic", MODE_PRIVATE).edit();
                editor.putBoolean("SwitchState_" + binding.switchService.getTag(), binding.switchService.isChecked());
                editor.commit();

            }
        });

        binding.switchRelease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean switchState = binding.switchRelease.isChecked();

                SharedPreferences.Editor editor = getSharedPreferences("SwitchNotic", MODE_PRIVATE).edit();
                editor.putBoolean("SwitchState_" + binding.switchRelease.getTag(), binding.switchRelease.isChecked());
                editor.commit();

                if (switchState){
                    WorkRequest workRequest = new OneTimeWorkRequest.Builder(FetchMoviesWorker.class).build();
                    WorkManager.getInstance(NotificationActivity.this).enqueue(workRequest);
                }else {
                    WorkManager.getInstance(NotificationActivity.this).cancelAllWork();
                }
            }
        });

        binding.switchUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("SwitchNotic", MODE_PRIVATE).edit();
                editor.putBoolean("SwitchState_" + binding.switchUpdates.getTag(), binding.switchUpdates.isChecked());
                editor.commit();

            }
        });


    }

    private void setSwitchStates(ViewGroup lnNotification) {
        SharedPreferences prefs = getSharedPreferences("SwitchNotic", MODE_PRIVATE);

        for (int i = 0; i < lnNotification.getChildCount(); i++) {
            View child = lnNotification.getChildAt(i);

            if (child instanceof SwitchCompat) {
                SwitchCompat switchCompat = (SwitchCompat) child;
                String tag = (String) switchCompat.getTag();
                Boolean switchState = prefs.getBoolean("SwitchState_" + tag, false);
                switchCompat.setChecked(switchState);
            } else if (child instanceof ViewGroup) {
                setSwitchStates((ViewGroup) child);
            }
        }
    }

}