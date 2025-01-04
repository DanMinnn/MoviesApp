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
    private static final String[] ALLOWED_URL_CONTAINS = {
            "vidsrc",
            "cdnjs.cloudflare.com",
            "gomo.to",
            "s10.histats.com",
            "capaciousdrewreligion.com",
            "ajax.googleapis.com",
            "image.tmdb.org",
            "s4.histats.com",
            "recordedthereby.com",
            "e.dtscout.com",
            "unseenreport.com",
            "gstatic.com",
            "tags.crwdcntrl.net",
            "get.s-onetag.com",
            "t.dtscout.com",
            "pixel.onaudience.com",
            "data-beacons.s-onetag.com",
            "googletagmanager",
            "pogothere",
            "cloudfront",
            "knowledconsideunden",
            "getrunkhomuto",
            "connect-metrics-collector",
            "techtonicwave4",
            "ap.lijit.com",
            "um.simpli.fi",
            ".link/content",
            "favicon",
            "proftrafficcounter",
            "onetag-geo.s-onetag",
            ".org/content",
            "approveofchi",
            "2embed",
            "jsdelivr",
            "certificatestainfranz",
            "www.google-analytics.com",
            "p.dtsan.net",
            "embedwish.com",
            "t.dtscdn.com",
            "rvlzhfoat7kb.cdn-centaurus.com"
    };

    public static void init(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
            }
        }).start();
    }

    public static boolean isAd(String url) {
        if (TextUtils.isEmpty(url)) return true;

        Uri uri = Uri.parse(url);
        String host = uri.getHost();
        if (host == null) return true;

        if (url.contains(".m3u8") || url.contains(".ts") || url.contains(".dtsan")) {
            return false;
        }

        // Kiểm tra host trong danh sách được phép
        for (String allowedUrl : ALLOWED_URL_CONTAINS) {
            if (host.contains(allowedUrl)) {
                return false; // URL hợp lệ
            }
        }
        return true; // Không hợp lệ => Quảng cáo
    }

    private static boolean isAllowedUrl(String url) throws MalformedURLException {
        for (String allowedUrl : ALLOWED_URL_CONTAINS) {
            if (url.contains(allowedUrl)) {
                return true;
            }
        }
        return false;
    }

    public static WebResourceResponse createEmptyResource() {
        return new WebResourceResponse("text/plain", "utf-8", new ByteArrayInputStream("".getBytes()));
    }
}

