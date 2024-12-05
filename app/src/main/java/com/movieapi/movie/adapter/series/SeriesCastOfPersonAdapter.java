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
import com.movieapi.movie.model.series.SeriesCastOfPerson;
import com.movieapi.movie.utils.Constants;

import java.util.List;

public class SeriesCastOfPersonAdapter extends RecyclerView.Adapter<SeriesCastOfPersonAdapter.ViewHolder> {

    Context context;
    List<SeriesCastOfPerson> tvCastList;

    public SeriesCastOfPersonAdapter(Context context, List<SeriesCastOfPerson> tvCastList) {
        this.context = context;
        this.tvCastList = tvCastList;
    }

    @NonNull
    @Override
    public SeriesCastOfPersonAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SeriesCastOfPersonAdapter.ViewHolder(LayoutInflater.from(context).inflate(R.layout.cast_movie_single_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesCastOfPersonAdapter.ViewHolder holder, int position) {
        Glide.with(context.getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_342 + tvCastList.get(position).getPosterPath())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.tvCastImv);

        if (tvCastList.get(position).getName() != null)
            holder.tvCastTitle.setText(tvCastList.get(position).getName());
        else
            holder.tvCastTitle.setText("");

        if (tvCastList.get(position).getCharacter() != null)
            holder.tvCastCharacter.setText("as " + tvCastList.get(position).getCharacter());
        else
            holder.tvCastCharacter.setText("");
    }

    @Override
    public int getItemCount() {
        return tvCastList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardViewShowCast;
        ImageView tvCastImv;
        TextView tvCastTitle, tvCastCharacter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewShowCast = itemView.findViewById(R.id.cast_view_showCastOfMovie);
            tvCastImv = itemView.findViewById(R.id.movie_cast_imv);
            tvCastTitle = itemView.findViewById(R.id.movie_cast_title);
            tvCastCharacter = itemView.findViewById(R.id.movie_cast_character);

            tvCastImv.getLayoutParams().width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.31);
            tvCastImv.getLayoutParams().height = (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.31) / 0.66);

            cardViewShowCast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SeriesDetailsActivity.class);
                    intent.putExtra("series_id", tvCastList.get(getAdapterPosition()).getId());
                    context.startActivity(intent);
                }
            });
        }
    }
}
