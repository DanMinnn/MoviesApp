package com.movieapi.movie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.movieapi.movie.R;

public class SearchFragment extends Fragment {
    EditText edSearchView;
    FloatingActionButton fabSearch;
    TextView searchRecent;
    RecyclerView recentSearchRecView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edSearchView = view.findViewById(R.id.searchView_ed);
        fabSearch = view.findViewById(R.id.fab_search);
        searchRecent = view.findViewById(R.id.search_recent_textView);
        recentSearchRecView = view.findViewById(R.id.search_recView_recent);


    }
}
