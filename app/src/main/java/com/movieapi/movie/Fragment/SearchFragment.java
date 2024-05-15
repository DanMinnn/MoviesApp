package com.movieapi.movie.Fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.movieapi.movie.R;
import com.movieapi.movie.activity.SearchResultActivity;
import com.movieapi.movie.adapter.RecentSearchAdapter;
import com.movieapi.movie.database.DatabaseHelper;
import com.movieapi.movie.database.search.RecentSearch;
import com.movieapi.movie.database.search.SearchDatabase;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {
    EditText edSearchView;
    FloatingActionButton fabSearch;
    TextView searchRecent;
    RecyclerView recentSearchRecView;

    String query;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edSearchView = view.findViewById(R.id.searchView);
        fabSearch = view.findViewById(R.id.sort_filter_fab);
        searchRecent = view.findViewById(R.id.search_recents_textView);
        recentSearchRecView = view.findViewById(R.id.search_recView_recents);

        LiveData<List<RecentSearch>> recentSearches = SearchDatabase.getInstance(requireContext())
                .searchDao()
                .getAllRecentSearch();

        recentSearches.observe(requireActivity(), new Observer<List<RecentSearch>>() {
            @Override
            public void onChanged(List<RecentSearch> recentSearches) {
                Collections.reverse(recentSearches);
                RecentSearchAdapter recentSearchAdapter = new RecentSearchAdapter(getActivity(), recentSearches);
                recentSearchRecView.setAdapter(recentSearchAdapter);
                recentSearchRecView.setLayoutManager(new LinearLayoutManager(getContext()));

                if (recentSearches.isEmpty())
                    searchRecent.setVisibility(View.INVISIBLE);
                else
                    searchRecent.setVisibility(View.VISIBLE);
            }
        });

        fabSearch.setOnClickListener(new View.OnClickListener() {

            class SaveSearch extends AsyncTask<Void, Void, Void>{
                @Override
                protected Void doInBackground(Void... voids) {
                    query = edSearchView.getText().toString().trim().toLowerCase();
                    if (!DatabaseHelper.isRecentSearch(requireContext(), query)){
                        RecentSearch recentSearch = new RecentSearch(query);
                        SearchDatabase.getInstance(requireContext())
                                .searchDao()
                                .insertSearch(recentSearch);
                    }
                    return null;
                }
            }

            @Override
            public void onClick(View v) {
                query = edSearchView.getText().toString().trim().toLowerCase();
                SaveSearch saveSearch = new SaveSearch();
                saveSearch.execute();

                Intent iSearchResult = new Intent(getActivity(), SearchResultActivity.class);
                iSearchResult.putExtra("query", query);
                startActivity(iSearchResult);
            }
        });

        fabSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setCanceledOnTouchOutside(false);
                dialog.setContentView(R.layout.dialog_sort_filter);

                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                dialog.setCanceledOnTouchOutside(true);
                dialog.getWindow().setGravity(Gravity.BOTTOM);
                dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                dialog.show();
                return false;
            }
        });
    }
}
