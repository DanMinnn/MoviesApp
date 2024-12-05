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
import com.movieapi.movie.activity.series.SeriesDetailsActivity;
import com.movieapi.movie.model.series.SeriesBrief;
import com.movieapi.movie.utils.Constants;

import java.util.List;

public class MainSeriesAdapter extends RecyclerView.Adapter<MainSeriesAdapter.MainTVHolder> {

    Context context;
    List<SeriesBrief> seriesBriefList;

    public MainSeriesAdapter(Context context, List<SeriesBrief> seriesBriefList) {
        this.context = context;
        this.seriesBriefList = seriesBriefList;
    }

    @NonNull
    @Override
    public MainSeriesAdapter.MainTVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MainSeriesAdapter.MainTVHolder(LayoutInflater.from(context).inflate(R.layout.item_popular_top_rated, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MainSeriesAdapter.MainTVHolder holder, int position) {
        Glide.with(context.getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_1280 + seriesBriefList.get(position).getPosterPath())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imvShowCard);

        if (seriesBriefList.get(position).getVoteAverage() != 0)
            holder.txtVoteAverage.setText(String.format("%.1f", seriesBriefList.get(position).getVoteAverage()));
    }

    @Override
    public int getItemCount() {
        return seriesBriefList.size();
    }

    public class MainTVHolder extends RecyclerView.ViewHolder {

        CardView cardViewShow;
        ImageView imvShowCard;
        TextView txtVoteAverage;
        public MainTVHolder(@NonNull View itemView) {
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
                    intent.putExtra("series_id", seriesBriefList.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
