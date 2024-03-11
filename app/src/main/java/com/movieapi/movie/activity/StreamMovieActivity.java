package com.movieapi.movie.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.movieapi.movie.databinding.ActivityStreamMovieBinding;
import com.movieapi.movie.utils.AdBlocker;
import com.movieapi.movie.utils.Browser;
import com.movieapi.movie.utils.Constants;

public class StreamMovieActivity extends AppCompatActivity {
    ActivityStreamMovieBinding binding;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AdBlocker.init(this);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding = ActivityStreamMovieBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.webView.setWebViewClient(new Browser());
        binding.webView.setWebChromeClient(new ChromeClient());
        binding.webView.getSettings().setJavaScriptEnabled(true);

        Intent recievedIntent = getIntent();
        String movieId = recievedIntent.getStringExtra("movie_id");

        binding.webView.loadUrl(Constants.MOVIE_STREAM_URL + movieId);

        binding.backBtnWebview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private class ChromeClient extends WebChromeClient{
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private FrameLayout mFullscreenContainer;
        private int mOriginOrientation;
        private int mOriginSystemUiVisibility;

        ChromeClient() {
        }

        public Bitmap getDefaultVideoPoster(){
            if (mCustomView == null)
                return null;
            return BitmapFactory.decodeResource(getApplicationContext().getResources(), 2130837573);
        }

        public void onHideCustomView(){
            ((FrameLayout)getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginSystemUiVisibility);
            setRequestedOrientation(this.mOriginOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallBack){
            if (this.mCustomView != null){
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallBack;
            ((FrameLayout)getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    @Override
    public void onBackPressed() {
        if(binding.webView != null && binding.webView.canGoBack()) {
            binding.webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
