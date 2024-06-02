package com.movieapi.movie.utils;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Browser extends WebViewClient {
    private Map<String, Boolean> loadedUrls = new HashMap<>();

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("market://") || url.startsWith("intent://")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            } catch (ActivityNotFoundException e) {
                Log.e("Browser", "Google Play Store not installed", e);
            }
        } else {
            view.loadUrl(url);
            return true;
        }
        return false;
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        String url = request.getUrl().toString();
        boolean ad;
        if (!loadedUrls.containsKey(url)) {
            ad = AdBlocker.isAd(url);
            loadedUrls.put(url, ad);
        } else {
            ad = loadedUrls.get(url);
        }
        if (ad) {
            Log.d("Browser", "Ad blocked: " + url);
        }
        return ad ? AdBlocker.createEmptyResource() : super.shouldInterceptRequest(view, request);
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        Log.e("Browser", "Error loading page: " + error.getDescription());
        // Handle specific errors here if needed
        if (error.getErrorCode() == WebViewClient.ERROR_HOST_LOOKUP) {
            // DNS lookup failure, handle it
        }
    }
}

