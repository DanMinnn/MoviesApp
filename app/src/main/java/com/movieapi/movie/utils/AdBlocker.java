package com.movieapi.movie.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebResourceResponse;

import androidx.annotation.WorkerThread;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class AdBlocker {
    private static final String AD_HOSTS_FILE = "host.txt";
    private static final Set<String> AD_HOSTS = new HashSet<>();

    public static void init(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loadFromAssets(context);
                } catch (IOException e) {
                    Log.e("AdBlocker", "Error loading ad hosts file", e);
                }
            }
        }).start();
    }

    private static void loadFromAssets(Context context) throws IOException {
        InputStream stream = context.getAssets().open(AD_HOSTS_FILE);
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            AD_HOSTS.add(line.trim());
        }
        bufferedReader.close();
        inputStreamReader.close();
        stream.close();
        Log.d("AdBlocker", "Ad hosts loaded: " + AD_HOSTS.size());
    }

    public static boolean isAd(String url) {
        try {
            String host = getHost(url);
            return isAdHost(host);
        } catch (MalformedURLException e) {
            Log.d("AdBlocker", "Malformed URL: " + e.toString());
            return false;
        }
    }

    private static boolean isAdHost(String host) {
        if (TextUtils.isEmpty(host)) {
            return false;
        }
        for (String adHost : AD_HOSTS) {
            if (host.contains(adHost)) {
                return true;
            }
        }
        return false;
    }

    public static String getHost(String url) throws MalformedURLException {
        return new URL(url).getHost();
    }

    public static WebResourceResponse createEmptyResource() {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
    }
}

