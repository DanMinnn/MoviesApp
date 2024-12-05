package com.movieapi.movie.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
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

public class SeriesCarouselAdapter extends RecyclerView.Adapter<SeriesCarouselAdapter.SeriesViewHolder>{

    Context context;
    List<SeriesBrief> seriesBriefs;

    public SeriesCarouselAdapter(Context context, List<SeriesBrief> seriesBriefs) {
        this.context = context;
        this.seriesBriefs = seriesBriefs;
    }

    @NonNull
    @Override
    public SeriesCarouselAdapter.SeriesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SeriesCarouselAdapter.SeriesViewHolder(LayoutInflater.from(context).inflate(R.layout.carousel_single_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesCarouselAdapter.SeriesViewHolder holder, int position) {
        Glide.with(context.getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_780 + seriesBriefs.get(position).getPosterPath())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.series_imageView);

        if (seriesBriefs.get(position).getName() != null)
            holder.series_title.setText(seriesBriefs.get(position).getName());
        else
            holder.series_title.setText("");
    }

    @Override
    public int getItemCount() {
        return seriesBriefs.size();
    }

    public class SeriesViewHolder extends RecyclerView.ViewHolder {

        ImageView series_imageView;
        TextView series_title, tvRating;
        CardView tvCardView;
        AppCompatButton play_btn;

        public SeriesViewHolder(@NonNull View itemView) {
            super(itemView);

            series_imageView = itemView.findViewById(R.id.carousel_imageView);
            series_title = itemView.findViewById(R.id.carousel_title);
            tvRating = itemView.findViewById(R.id.carousel_rating);
            tvCardView = itemView.findViewById(R.id.carousel_main_card);
            play_btn = itemView.findViewById(R.id.carousel_play_btn);

            series_title.getLayoutParams().width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.7);

            tvCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iSeriesDetails = new Intent(context, SeriesDetailsActivity.class);
                    iSeriesDetails.putExtra("series_id", seriesBriefs.get(getAdapterPosition()).getId());
                    context.startActivity(iSeriesDetails);
                }
            });

            play_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iSeriesStream = new Intent(context, SeriesDetailsActivity.class);
                    iSeriesStream.putExtra("series_id", seriesBriefs.get(getAdapterPosition()).getId());
                    context.startActivity(iSeriesStream);
                }
            });
        }
    }
}
