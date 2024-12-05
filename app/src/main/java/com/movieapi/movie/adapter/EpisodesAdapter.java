package com.movieapi.movie.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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
import com.movieapi.movie.activity.SeriesStreamActivity;
import com.movieapi.movie.model.series.EpisodeBrief;
import com.movieapi.movie.utils.Constants;

import java.util.List;

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.EpisodesHolder> {

    Context context;
    private String series_id;

    private List<EpisodeBrief> episodeBriefs;

    public EpisodesAdapter(Context context, List<EpisodeBrief> episodeBriefs) {
        this.context = context;
        this.episodeBriefs = episodeBriefs;
    }

    @NonNull
    @Override
    public EpisodesAdapter.EpisodesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new EpisodesAdapter.EpisodesHolder(LayoutInflater.from(context).inflate(R.layout.item_episode, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EpisodesAdapter.EpisodesHolder holder, int position) {
        Glide.with(context)
                .load(Constants.IMAGE_LOADING_BASE_URL_780 + episodeBriefs.get(position).getStillPath())
                .centerCrop()
                .placeholder(R.drawable.poster_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.episode_imageView);

        holder.episode_name.setText(episodeBriefs.get(position).getName());

        holder.episode_number.setText("S" + episodeBriefs.get(position).getSeasonNumber().toString() + "E" + episodeBriefs.get(position).getEpisodeNumber().toString());
    }

    @Override
    public int getItemCount() {
        return episodeBriefs.size();
    }

    public class EpisodesHolder extends RecyclerView.ViewHolder {


        private CardView episode_cardView;
        private ImageView episode_imageView;
        private TextView episode_name;
        private TextView episode_number;
        public EpisodesHolder(@NonNull View itemView) {
            super(itemView);

            episode_cardView = itemView.findViewById(R.id.eps_cardView);
            episode_imageView = itemView.findViewById(R.id.eps_image);
            episode_name = itemView.findViewById(R.id.eps_name);
            episode_number = itemView.findViewById(R.id.eps_episode);

            episode_cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent seriesStream = new Intent(context, SeriesStreamActivity.class);
                    seriesStream.putExtra("series_id", series_id);
                    seriesStream.putExtra("season_number", episodeBriefs.get(getAdapterPosition()).getSeasonNumber().toString());
                    seriesStream.putExtra("episode_number", episodeBriefs.get(getAdapterPosition()).getEpisodeNumber().toString());
                    context.startActivity(seriesStream);
                }
            });

            episode_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            episode_name.setSelected(true);
        }
    }
}
