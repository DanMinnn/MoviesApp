package com.movieapi.movie.activity.series;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.movieapi.movie.activity.movies.StreamMovieActivity;
import com.movieapi.movie.databinding.ActivityStreamMovieBinding;
import com.movieapi.movie.utils.AdBlocker;
import com.movieapi.movie.utils.Constants;

public class SeriesStreamActivity extends AppCompatActivity {
    ActivityStreamMovieBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AdBlocker.init(this);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityStreamMovieBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.webView.setWebViewClient(new AdBlockingWebViewClient());
        binding.webView.setWebChromeClient(new ChromeClient());
        binding.webView.getSettings().setJavaScriptEnabled(false);
        binding.webView.getSettings().setDomStorageEnabled(false);
        binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        Intent receivedIntent = getIntent();
        String seriesId = receivedIntent.getStringExtra("series_id");
        String url = Constants.MOVIE_STREAM_URL + seriesId;
        Log.d("Movie URL", url);
        binding.webView.loadUrl(url);

        binding.backBtnWebview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private class AdBlockingWebViewClient extends WebViewClient {
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            String url = request.getUrl().toString();
            if (AdBlocker.isAd(url)) {
                Log.d("AdBlocker", "Blocked URL: " + url);
                return AdBlocker.createEmptyResource();
            }
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            if (AdBlocker.isAd(url)) {
                Log.d("AdBlocker", "Blocked URL: " + url);
                return AdBlocker.createEmptyResource();
            }
            return super.shouldInterceptRequest(view, url);
        }
    }

    private class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private FrameLayout mFullscreenContainer;
        private int mOriginOrientation;
        private int mOriginSystemUiVisibility;

        ChromeClient() {
        }

        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null)
                return null;
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginSystemUiVisibility);
            setRequestedOrientation(this.mOriginOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallBack) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallBack;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (binding.webView != null && binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
