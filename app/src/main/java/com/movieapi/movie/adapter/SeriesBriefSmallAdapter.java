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
import com.movieapi.movie.activity.SeriesDetailsActivity;
import com.movieapi.movie.model.series.SeriesBrief;
import com.movieapi.movie.utils.Constants;

import java.util.List;

public class SeriesBriefSmallAdapter extends RecyclerView.Adapter<SeriesBriefSmallAdapter.TVShowViewHolder>{

    Context context;
    List<SeriesBrief> seriesBriefs;

    public SeriesBriefSmallAdapter(Context context, List<SeriesBrief> seriesBriefs) {
        this.context = context;
        this.seriesBriefs = seriesBriefs;
    }

    @NonNull
    @Override
    public SeriesBriefSmallAdapter.TVShowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SeriesBriefSmallAdapter.TVShowViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesBriefSmallAdapter.TVShowViewHolder holder, int position) {
        Glide.with(context.getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_1280 + seriesBriefs.get(position).getPosterPath())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imvShowCard);

        if (seriesBriefs.get(position).getVoteAverage() != 0)
            holder.txtVoteAverage.setText(String.format("%.1f", seriesBriefs.get(position).getVoteAverage()));
    }

    @Override
    public int getItemCount() {
        return seriesBriefs.size();
    }

    public class TVShowViewHolder extends RecyclerView.ViewHolder {

        CardView cardViewShow;
        ImageView imvShowCard;
        TextView txtVoteAverage;
        public TVShowViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewShow = itemView.findViewById(R.id.card_view_search);
            imvShowCard = itemView.findViewById(R.id.image_view_poster_search);
            txtVoteAverage = itemView.findViewById(R.id.txtVoteAverage);

            imvShowCard.getLayoutParams().width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.50);
            imvShowCard.getLayoutParams().height = (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.50) / 0.76);

            cardViewShow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SeriesDetailsActivity.class);
                    intent.putExtra("series_id", seriesBriefs.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
