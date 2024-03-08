package com.movieapi.movie.utils;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.WorkerThread;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

public class AdBlocker {
    private static final String AD_HOST_FILE = "host.txt";
    private static final Set<String> AD_HOSTS = new HashSet<>();

    public static void init(final Context context){
        new AsyncTask<Void, Void, Void>(){

            @Override
            protected Void doInBackground(Void... params) {
                return null;
            }
        }.execute();
    }

    @WorkerThread
    private static void loadFromAssets(Context context) throws IOException {
        InputStream inputStream = context.getAssets().open(AD_HOST_FILE);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

    }
}
