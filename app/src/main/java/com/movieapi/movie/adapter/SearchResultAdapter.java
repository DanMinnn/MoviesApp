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
import com.movieapi.movie.network.search.SearchResult;
import com.movieapi.movie.utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {
    Context context;
    List<SearchResult> searchResults;

    public SearchResultAdapter(Context context, List<SearchResult> searchResults) {
        this.context = context;
        this.searchResults = searchResults;
    }

    @NonNull
    @Override
    public SearchResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultAdapter.ViewHolder holder, int position) {
        Glide.with(context.getApplicationContext()).load(Constants.IMAGE_LOADING_BASE_URL_342 + searchResults.get(position).getPosterPath())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.posterImv);

        if (searchResults.get(position).getName() != null && !searchResults.get(position).getName().trim().isEmpty())
            holder.name.setText(searchResults.get(position).getName());
        else
            holder.name.setText("");

        if (searchResults.get(position).getMediaType() != null && searchResults.get(position).getMediaType().equals("movie"))
            holder.mediaType.setText("Movies");
        else if (searchResults.get(position).getMediaType() != null && searchResults.get(position).getMediaType().equals("person")) {
            holder.mediaType.setText("Person");
        }
        else
            holder.mediaType.setText("");

        if (searchResults.get(position).getOverview() != null && !searchResults.get(position).getOverview().trim().isEmpty())
            holder.overview.setText(searchResults.get(position).getOverview());
        else
            holder.overview.setText("");

        if (searchResults.get(position).getReleaseDate() != null && !searchResults.get(position).getReleaseDate().trim().isEmpty()){
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");

            try {
                Date releaseDate = sdf1.parse(searchResults.get(position).getReleaseDate());
                holder.year.setText(sdf2.format(releaseDate));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }else
            holder.year.setText("");
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardViewResutlSearch;
        ImageView posterImv;
        TextView name, mediaType, overview, year;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardViewResutlSearch = itemView.findViewById(R.id.card_view_search);
            posterImv = itemView.findViewById(R.id.image_view_poster_search);
            name = itemView.findViewById(R.id.text_view_name_search);
            mediaType = itemView.findViewById(R.id.text_view_media_type_search);
            overview = itemView.findViewById(R.id.text_view_overview_search);
            year = itemView.findViewById(R.id.text_view_year_search);

            posterImv.getLayoutParams().width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.31);
            posterImv.getLayoutParams().height = (int) ((context.getResources().getDisplayMetrics().widthPixels * 0.31) / 0.66);


            cardViewResutlSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent iMovieDetails = new Intent(context, MovieDetailsActivity.class);
                    iMovieDetails.putExtra("movie_id", searchResults.get(getAdapterPosition()).getId());
                    context.startActivity(iMovieDetails);
                }
            });
        }
    }
}
