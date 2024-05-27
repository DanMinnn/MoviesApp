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
import com.movieapi.movie.database.movies.FavMovie;
import com.movieapi.movie.utils.Constants;

import java.util.List;

public class FavMoviesAdapter extends RecyclerView.Adapter<FavMoviesAdapter.FavHolder> {
    Context context;
    List<FavMovie> mMovies;

    public FavMoviesAdapter(Context context, List<FavMovie> mMovies) {
        this.context = context;
        this.mMovies = mMovies;
    }

    @NonNull
    @Override
    public FavMoviesAdapter.FavHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavMoviesAdapter.FavHolder(LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavMoviesAdapter.FavHolder holder, int position) {
        Glide.with(context.getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_1280 + mMovies.get(position).getPoster_path())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.movie_imageView);

        if (mMovies.get(position).getVoteAverage() != 0)
            holder.txtVoteAverage.setText(String.format("%.1f", mMovies.get(position).getVoteAverage()));
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    public class FavHolder extends RecyclerView.ViewHolder {
        CardView movie_CardView;
        ImageView movie_imageView;
        TextView txtVoteAverage;

        public FavHolder(@NonNull View itemView) {
            super(itemView);

            movie_CardView = itemView.findViewById(R.id.card_view_search);
            movie_imageView = itemView.findViewById(R.id.image_view_poster_search);
            txtVoteAverage = itemView.findViewById(R.id.txtVoteAverage);

            movie_imageView.getLayoutParams().width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.50);
            movie_imageView.getLayoutParams().height = (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.50) / 0.70);

            movie_CardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra("movie_id", mMovies.get(getAdapterPosition()).getMovie_id());
                    context.startActivity(intent);
                }
            });
        }
    }
}
