package com.movieapi.movie.adapter.movies;

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
import com.movieapi.movie.activity.movies.MovieDetailsActivity;
import com.movieapi.movie.model.movie.MovieBrief;
import com.movieapi.movie.utils.Constants;

import java.util.List;

public class MovieBriefSmallAdapter extends RecyclerView.Adapter<MovieBriefSmallAdapter.MovieViewHolder> {
    List<MovieBrief> movieBriefs;
    Context context;

    public MovieBriefSmallAdapter(List<MovieBrief> movieBriefs, Context context) {
        this.movieBriefs = movieBriefs;
        this.context = context;
    }

    @NonNull
    @Override
    public MovieBriefSmallAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieBriefSmallAdapter.MovieViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieBriefSmallAdapter.MovieViewHolder holder, int position) {
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

            imvShowCard.getLayoutParams().width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.50);
            imvShowCard.getLayoutParams().height = (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.50) / 0.76);

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
