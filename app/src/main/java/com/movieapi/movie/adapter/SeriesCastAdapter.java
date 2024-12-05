package com.movieapi.movie.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.movieapi.movie.R;
import com.movieapi.movie.activity.CastActivity;
import com.movieapi.movie.model.series.SeriesCastBrief;
import com.movieapi.movie.utils.Constants;

import java.util.List;

public class SeriesCastAdapter extends RecyclerView.Adapter<SeriesCastAdapter.TVCastHolder> {

    Context context;
    List<SeriesCastBrief> castBriefs;

    public SeriesCastAdapter(Context context, List<SeriesCastBrief> castBriefs) {
        this.context = context;
        this.castBriefs = castBriefs;
    }

    @NonNull
    @Override
    public SeriesCastAdapter.TVCastHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SeriesCastAdapter.TVCastHolder(LayoutInflater.from(context).inflate(R.layout.cast_single_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SeriesCastAdapter.TVCastHolder holder, int position) {
        Glide.with(context.getApplicationContext())
                .load(Constants.IMAGE_LOADING_BASE_URL_342 + castBriefs.get(position).getProfilePath())
                .centerCrop()
                .placeholder(R.drawable.cast_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.cast_imv);

        if(castBriefs.get(position).getName() != null)
            holder.txt_cast_name.setText(castBriefs.get(position).getName());
        else
            holder.txt_cast_name.setText("");

        if (castBriefs.get(position).getCharacter() != null)
            holder.txt_cast_alias.setText(castBriefs.get(position).getCharacter());
        else
            holder.txt_cast_alias.setText("");
    }

    @Override
    public int getItemCount() {
        return castBriefs.size();
    }

    public class TVCastHolder extends RecyclerView.ViewHolder {

        ImageView cast_imv;
        TextView txt_cast_name, txt_cast_alias;
        LinearLayout castDetails;
        public TVCastHolder(@NonNull View itemView) {
            super(itemView);

            cast_imv = itemView.findViewById(R.id.cast_imv);
            txt_cast_name = itemView.findViewById(R.id.txt_cast_name);
            txt_cast_alias = itemView.findViewById(R.id.txt_cast_alias);
            castDetails = itemView.findViewById(R.id.cast_root_view);

            castDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iCastDetails = new Intent(context, CastActivity.class);
                    iCastDetails.putExtra("person_id", castBriefs.get(getAdapterPosition()).getId());
                    context.startActivity(iCastDetails);
                }
            });
        }
    }
}
