package com.movieapi.movie.adapter.series;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.movieapi.movie.R;
import com.movieapi.movie.activity.movies.MovieDetailsActivity;
import com.movieapi.movie.activity.series.SeriesDetailsActivity;
import com.movieapi.movie.model.movie.Movie;
import com.movieapi.movie.model.recommend.Recommendation;
import com.movieapi.movie.model.series.Series;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecommendSeriesAdapter extends RecyclerView.Adapter<RecommendSeriesAdapter.ForUHolder> {

    private List<Recommendation> recommendations;
    private Context context;
    private ApiInterface apiInterface;

    public RecommendSeriesAdapter(List<Recommendation> recommendations, Context context) {
        this.recommendations = recommendations;
        this.context = context;
        this.apiInterface = ApiClient.getMovieApi();
    }

    @NonNull
    @Override
    public RecommendSeriesAdapter.ForUHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecommendSeriesAdapter.ForUHolder(LayoutInflater.from(context).inflate(R.layout.item_popular_top_rated, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendSeriesAdapter.ForUHolder holder, int position) {
        Recommendation recommendation = recommendations.get(position);

        if (recommendation.getItem_id() == 0) {
            Log.e("Error", "Invalid movie ID for recommendation at position " + position);
            return;
        }

        apiInterface.getSeries(recommendation.getItem_id(), Constants.API_KEY).enqueue(new Callback<Series>() {
            @Override
            public void onResponse(Call<Series> call, Response<Series> response) {
                if (response.isSuccessful() && response.body() != null){
                    Series series = response.body();

                    Glide.with(context.getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_1280 + series.getBackdropPath())
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.imvShowCard);

                    if (series.getVoteAverage() != 0)
                        holder.txtVoteAverage.setText(String.format("%.1f", series.getVoteAverage()));
                }else
                    Log.e("Error", "Failed to fetch movie details for ID: " + recommendation.getItem_id());
            }

            @Override
            public void onFailure(Call<Series> call, Throwable t) {
                Log.e("Error", "Network request failed in adapter: " + t.getMessage());
            }
        });
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }

    public class ForUHolder extends RecyclerView.ViewHolder {

        CardView cardViewShow;
        ImageView imvShowCard;
        TextView txtVoteAverage;

        public ForUHolder(@NonNull View itemView) {
            super(itemView);

            cardViewShow = itemView.findViewById(R.id.card_view_search);
            imvShowCard = itemView.findViewById(R.id.image_view_poster_search);
            txtVoteAverage = itemView.findViewById(R.id.txtVoteAverage);

            imvShowCard.getLayoutParams().width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.38);
            imvShowCard.getLayoutParams().height = (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.38) / 0.70);

            cardViewShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SeriesDetailsActivity.class);
                    intent.putExtra("series_id", recommendations.get(getAdapterPosition()).getItem_id());
                    context.startActivity(intent);
                }
            });
        }
    }
}
