package com.movieapi.movie.adapter.series;

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
import com.movieapi.movie.activity.series.SeriesDetailsActivity;
import com.movieapi.movie.database.series.FavSeries;
import com.movieapi.movie.utils.Constants;

import java.util.List;

public class FavSeriesAdapter extends RecyclerView.Adapter<FavSeriesAdapter.FavSeriesHolder> {
    Context context;
    List<FavSeries> favSeriesList;

    public FavSeriesAdapter(Context context, List<FavSeries> favSeriesList) {
        this.context = context;
        this.favSeriesList = favSeriesList;
    }

    @NonNull
    @Override
    public FavSeriesAdapter.FavSeriesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavSeriesAdapter.FavSeriesHolder(LayoutInflater.from(context).inflate(R.layout.item_search_result, parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavSeriesAdapter.FavSeriesHolder holder, int position) {
        Glide.with(context.getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_1280 + favSeriesList.get(position).getStill_path())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.movie_imageView);

        if (favSeriesList.get(position).getVoteAverage() != 0)
            holder.txtVoteAverage.setText(String.format("%.1f", favSeriesList.get(position).getVoteAverage()));
    }

    @Override
    public int getItemCount() {
        return favSeriesList.size();
    }

    public class FavSeriesHolder extends RecyclerView.ViewHolder {

        CardView movie_CardView;
        ImageView movie_imageView;
        TextView txtVoteAverage;
        public FavSeriesHolder(@NonNull View itemView) {
            super(itemView);

            movie_CardView = itemView.findViewById(R.id.card_view_search);
            movie_imageView = itemView.findViewById(R.id.image_view_poster_search);
            txtVoteAverage = itemView.findViewById(R.id.txtVoteAverage);

            movie_imageView.getLayoutParams().width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.50);
            movie_imageView.getLayoutParams().height = (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.50) / 0.70);

            movie_CardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SeriesDetailsActivity.class);
                    intent.putExtra("series_id", favSeriesList.get(getAdapterPosition()).getSeries_id());
                    context.startActivity(intent);
                }
            });
        }
    }
}
