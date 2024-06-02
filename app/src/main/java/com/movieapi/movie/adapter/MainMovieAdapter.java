package com.movieapi.movie.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.movieapi.movie.activity.MovieDetailsActivity;
import com.movieapi.movie.model.movie.MovieBrief;
import com.movieapi.movie.utils.Constants;

import java.util.List;

public class MainMovieAdapter extends RecyclerView.Adapter<MainMovieAdapter.MovieViewHolder>{
    List<MovieBrief> movieBriefs;
    Context context;

    public MainMovieAdapter(List<MovieBrief> movieBriefs, Context context) {
        this.movieBriefs = movieBriefs;
        this.context = context;
    }

    @NonNull
    @Override
    public MainMovieAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainMovieAdapter.MovieViewHolder(LayoutInflater.from(context).inflate(R.layout.item_popular_top_rated, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainMovieAdapter.MovieViewHolder holder, int position) {
        Glide.with(context.getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_1280 + movieBriefs.get(position).getPosterPath())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imvShowCard);

        if (movieBriefs.get(position).getVoteAverage() != 0)
            holder.txtVoteAverage.setText(String.format("%.1f", movieBriefs.get(position).getVoteAverage()));
    }

    @Override
    public int getItemCount() {
        return movieBriefs.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewShow;
        ImageView imvShowCard;
        TextView txtVoteAverage;
        public MovieViewHolder(@NonNull View itemView) {
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
                    intent.putExtra("movie_id", movieBriefs.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
