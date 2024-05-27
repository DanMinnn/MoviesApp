package com.movieapi.movie.request;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.movieapi.movie.R;
import com.movieapi.movie.model.movie.MovieBrief;
import com.movieapi.movie.model.movie.NowShowingMoviesResponse;
import com.movieapi.movie.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FetchMoviesWorker extends Worker {
    Call<NowShowingMoviesResponse> nowShowingMoviesResponseCall;
    public FetchMoviesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        createNotificationChannel();
    }
    @NonNull
    @Override
    public Result doWork() {
        ApiInterface apiInterface = ApiClient.getMovieApi();
        nowShowingMoviesResponseCall = apiInterface.getNowShowingMovies(Constants.API_KEY, 1, "US");
        nowShowingMoviesResponseCall.enqueue(new Callback<NowShowingMoviesResponse>() {
            @Override
            public void onResponse(Call<NowShowingMoviesResponse> call, Response<NowShowingMoviesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    NowShowingMoviesResponse movieResponse = response.body();
                    for (MovieBrief movie : movieResponse.getResults()) {
                        sendNotification(movie.getTitle(), movie.getOverview());
                    }
                } else {
                    Log.e("Error", "Response not successful: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<NowShowingMoviesResponse> call, Throwable t) {
                Log.e("Error", "API call failed", t);
            }
        });

        // return Result.retry() để thông báo cho WorkManager rằng công việc không hoàn thành và cần thử lại sau.
        return Result.retry();
    }

    @SuppressLint("NotificationPermission")
    private void sendNotification(String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), Constants.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_movie)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Movies Channel";
            String description = "Channel for new movie notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Constants.CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

