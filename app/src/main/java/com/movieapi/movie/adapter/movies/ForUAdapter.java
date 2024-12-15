package com.movieapi.movie.adapter.movies;

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
import com.movieapi.movie.model.Recommendation;
import com.movieapi.movie.model.movie.Movie;
import com.movieapi.movie.model.movie.MovieBrief;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForUAdapter extends RecyclerView.Adapter<ForUAdapter.ForUHolder> {

    private List<Recommendation> recommendations;
    private Context context;
    private ApiInterface apiInterface;

    public ForUAdapter(List<Recommendation> recommendations, Context context) {
        this.recommendations = recommendations;
        this.context = context;
        this.apiInterface = ApiClient.getMovieApi();
    }

    @NonNull
    @Override
    public ForUAdapter.ForUHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ForUAdapter.ForUHolder(LayoutInflater.from(context).inflate(R.layout.item_popular_top_rated, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ForUAdapter.ForUHolder holder, int position) {
        Recommendation recommendation = recommendations.get(position);

        if (recommendation.getMovie_id() == 0) {
            Log.e("Error", "Invalid movie ID for recommendation at position " + position);
            return;
        }

        apiInterface.getMovieDetails(recommendation.getMovie_id(), Constants.API_KEY).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                if (response.isSuccessful() && response.body() != null){
                    Movie movie = response.body();

                    Glide.with(context.getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_1280 + movie.getPoster_path())
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(holder.imvShowCard);

                    if (movie.getVote_average() != 0)
                        holder.txtVoteAverage.setText(String.format("%.1f", movie.getVote_average()));
                }else
                    Log.e("Error", "Failed to fetch movie details for ID: " + recommendation.getMovie_id());
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
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
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra("movie_id", recommendations.get(getAdapterPosition()).getMovie_id());
                    context.startActivity(intent);
                }
            });
        }
    }
}
