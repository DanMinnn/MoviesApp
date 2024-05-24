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
import com.movieapi.movie.activity.YoutubeActivity;
import com.movieapi.movie.model.videos.Trailer;
import com.movieapi.movie.utils.Constants;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerHolder> {
    Context context;
    List<Trailer> trailerList;

    public TrailerAdapter(Context context, List<Trailer> trailerList) {
        this.context = context;
        this.trailerList = trailerList;
    }

    @NonNull
    @Override
    public TrailerAdapter.TrailerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TrailerHolder(LayoutInflater.from(context).inflate(R.layout.item_video_trailer, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerAdapter.TrailerHolder holder, int position) {
        Glide.with(context.getApplicationContext())
                .load(Constants.YOUTUBE_THUMBNAIL_BASE_URL + trailerList.get(position).getKey() + Constants.YOUTUBE_THUMBNAIL_IMAGE_QUALITY)
                .centerCrop()
                .placeholder(R.drawable.trailer_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.trailer_imv);

        if (trailerList.get(position).getName() != null)
            holder.trailer_textView.setText(trailerList.get(position).getName());
        else
            holder.trailer_textView.setText("");
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public class TrailerHolder extends RecyclerView.ViewHolder {
        CardView trailer_cardView;
        ImageView trailer_imv;
        TextView trailer_textView;

        public TrailerHolder(@NonNull View itemView) {
            super(itemView);
            trailer_cardView = itemView.findViewById(R.id.trailer_cardview);
            trailer_imv = itemView.findViewById(R.id.trailer_imv);
            trailer_textView = itemView.findViewById(R.id.trailer_textView);

            trailer_cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iYoutube = new Intent(context, YoutubeActivity.class);
                    iYoutube.putExtra("youtube_id", trailerList.get(getAdapterPosition()).getKey());
                    context.startActivity(iYoutube);
                }
            });
        }
    }
}
