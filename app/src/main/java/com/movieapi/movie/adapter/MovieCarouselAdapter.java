package com.movieapi.movie.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.movieapi.movie.R;
import com.movieapi.movie.network.movie.MovieBrief;
import com.movieapi.movie.utils.Constants;

import java.util.List;

public class MovieCarouselAdapter extends RecyclerView.Adapter<MovieCarouselAdapter.MovieViewHolder> {
    List<MovieBrief> movieBriefs;
    Context context;

    public MovieCarouselAdapter(List<MovieBrief> movieBriefs, Context context) {
        this.movieBriefs = movieBriefs;
        this.context = context;
    }


    @NonNull
    @Override
    public MovieCarouselAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieViewHolder(LayoutInflater.from(context).inflate(R.layout.carousel_single_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCarouselAdapter.MovieViewHolder holder, int position) {
        Glide.with(context.getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_780 + movieBriefs.get(position).getBackdropPath())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.movie_imageView);

        if (movieBriefs.get(position).getTitle() != null)
            holder.mTitle.setText(movieBriefs.get(position).getTitle());
        else
            holder.mTitle.setText("");

        if(movieBriefs.get(position).getPopularity() != null)
            holder.mRating.setText(String.format("%.1f", movieBriefs.get(position).getVoteAverage()));
        else
            holder.mRating.setText("");
    }

    @Override
    public int getItemCount() {
        return movieBriefs.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView movie_imageView;
        TextView mTitle, mRating;
        CardView mCardView;
        FloatingActionButton play_btn;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            movie_imageView = itemView.findViewById(R.id.carousel_imageView);
            mTitle = itemView.findViewById(R.id.carousel_title);
            mRating = itemView.findViewById(R.id.carousel_rating);
            mCardView = itemView.findViewById(R.id.carousel_main_card);
            play_btn = itemView.findViewById(R.id.carousel_play_btn);

            mTitle.getLayoutParams().width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.7);

            mCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
