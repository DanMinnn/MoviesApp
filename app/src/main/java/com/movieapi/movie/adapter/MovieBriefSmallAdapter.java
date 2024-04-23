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
        return new MovieBriefSmallAdapter.MovieViewHolder(LayoutInflater.from(context).inflate(R.layout.small_single_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieBriefSmallAdapter.MovieViewHolder holder, int position) {
        Glide.with(context.getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_342 + movieBriefs.get(position).getPosterPath())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imvShowCard);

        if (movieBriefs.get(position).getTitle() != null)
            holder.txtTitleShowCard.setText(movieBriefs.get(position).getTitle());
        else
            holder.txtTitleShowCard.setText("");
    }

    @Override
    public int getItemCount() {
        return movieBriefs.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewShow;
        ImageView imvShowCard;
        TextView txtTitleShowCard;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewShow = itemView.findViewById(R.id.card_view_show_card);
            imvShowCard = itemView.findViewById(R.id.imv_show_card);
            txtTitleShowCard = itemView.findViewById(R.id.txt_title_show_card);

            imvShowCard.getLayoutParams().width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.31);
            imvShowCard.getLayoutParams().height = (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.31) / 0.66);

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
