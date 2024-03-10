package com.movieapi.movie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.movieapi.movie.R;
import com.movieapi.movie.utils.NestedRecViewModel;

import java.util.List;

public class MovieNestedRecViewAdapter extends RecyclerView.Adapter<MovieNestedRecViewAdapter.NestedHolder> {
    Context context;
    List<NestedRecViewModel> mMovieGenre;

    public MovieNestedRecViewAdapter(Context context, List<NestedRecViewModel> mMovieGenre) {
        this.context = context;
        this.mMovieGenre = mMovieGenre;
    }

    @NonNull
    @Override
    public MovieNestedRecViewAdapter.NestedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MovieNestedRecViewAdapter.NestedHolder(LayoutInflater.from(context).inflate(R.layout.nested_genre_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MovieNestedRecViewAdapter.NestedHolder holder, int position) {
        switch (mMovieGenre.get(position).getmGenreId()){
            case 28:
                holder.nested_genre_heading.setText("Action");
                holder.nested_view_all.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        }
    }

    @Override
    public int getItemCount() {
        return mMovieGenre.size();
    }

    public class NestedHolder extends RecyclerView.ViewHolder {
        LinearLayout ln_heading_layout;
        TextView nested_genre_heading, nested_view_all;
        RecyclerView nested_recView;

        public NestedHolder(@NonNull View itemView) {
            super(itemView);

            ln_heading_layout = itemView.findViewById(R.id.ln_heading_layout);
            nested_genre_heading = itemView.findViewById(R.id.nested_heading);
            nested_view_all = itemView.findViewById(R.id.nested_view_all);
            nested_recView = itemView.findViewById(R.id.nested_recView);

        }
    }
}
