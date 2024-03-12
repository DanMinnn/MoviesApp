package com.movieapi.movie.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.movieapi.movie.R;
import com.movieapi.movie.activity.SearchResultActivity;
import com.movieapi.movie.database.search.RecentSearch;
import com.movieapi.movie.database.search.SearchDatabase;

import java.util.List;

public class RecentSearchAdapter extends RecyclerView.Adapter<RecentSearchAdapter.SearchHolder> {
    Context context;
    List<RecentSearch> recentSearches;

    public RecentSearchAdapter(Context context, List<RecentSearch> recentSearches) {
        this.context = context;
        this.recentSearches = recentSearches;
    }

    @NonNull
    @Override
    public RecentSearchAdapter.SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecentSearchAdapter.SearchHolder(LayoutInflater.from(context).inflate(R.layout.recent_search_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHolder holder, @SuppressLint("RecyclerView") int position) {
        if (recentSearches.get(position).getSearch_name() != null)
            holder.recentSearchName.setText(recentSearches.get(position).getSearch_name());

        holder.recentSearchDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteSearch deleteSearch = new DeleteSearch();
                deleteSearch.execute(recentSearches.get(position).getSearch_name());
            }
        });
    }


    @Override
    public int getItemCount() {
        return recentSearches.size();
    }

    public class SearchHolder extends RecyclerView.ViewHolder {
        TextView recentSearchName;
        ImageButton recentSearchDelete;
        ConstraintLayout searchParent;

        public SearchHolder(@NonNull View itemView) {
            super(itemView);

            recentSearchDelete = itemView.findViewById(R.id.recent_search_delete);
            recentSearchName = itemView.findViewById(R.id.name_recent_search);
            searchParent = itemView.findViewById(R.id.search_parent);

            searchParent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SearchResultActivity.class);
                    intent.putExtra("query", recentSearches.get(getAdapterPosition()).getSearch_name());
                    context.startActivity(intent);
                }
            });

        }
    }

    class DeleteSearch extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            SearchDatabase.getInstance(context)
                    .searchDao()
                    .deleteSearchesByName(strings[0]);

            return null;
        }
    }
}
