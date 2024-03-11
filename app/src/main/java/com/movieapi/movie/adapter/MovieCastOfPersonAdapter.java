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
import com.movieapi.movie.network.movie.MovieCastOfPerson;
import com.movieapi.movie.utils.Constants;

import java.util.List;

public class MovieCastOfPersonAdapter extends RecyclerView.Adapter<MovieCastOfPersonAdapter.ViewHolder> {
    Context context;
    List<MovieCastOfPerson> movieCastOfPersonList;

    public MovieCastOfPersonAdapter(Context context, List<MovieCastOfPerson> movieCastOfPersonList) {
        this.context = context;
        this.movieCastOfPersonList = movieCastOfPersonList;
    }

    @NonNull
    @Override
    public MovieCastOfPersonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.cast_movie_single_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieCastOfPersonAdapter.ViewHolder holder, int position) {
        Glide.with(context.getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_342 + movieCastOfPersonList.get(position).getPosterPath())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.movieCastImv);

        if (movieCastOfPersonList.get(position).getTitle() != null)
            holder.movieCastTitle.setText(movieCastOfPersonList.get(position).getTitle());
        else
            holder.movieCastTitle.setText("");

        if (movieCastOfPersonList.get(position).getCharacter() != null)
            holder.movieCastCharacter.setText("as " + movieCastOfPersonList.get(position).getCharacter());
        else
            holder.movieCastCharacter.setText("");
    }

    @Override
    public int getItemCount() {
        return movieCastOfPersonList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewShowCast;
        ImageView movieCastImv;
        TextView movieCastTitle, movieCastCharacter;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewShowCast = itemView.findViewById(R.id.cast_view_showCastOfMovie);
            movieCastImv = itemView.findViewById(R.id.movie_cast_imv);
            movieCastTitle = itemView.findViewById(R.id.movie_cast_title);
            movieCastCharacter = itemView.findViewById(R.id.movie_cast_character);

            movieCastImv.getLayoutParams().width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.31);
            movieCastImv.getLayoutParams().height = (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.31) / 0.66);

            cardViewShowCast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra("movie_id", movieCastOfPersonList.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
