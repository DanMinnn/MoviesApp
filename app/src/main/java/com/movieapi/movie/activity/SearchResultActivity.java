package com.movieapi.movie.activity;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.movieapi.movie.R;
import com.movieapi.movie.adapter.SearchResultAdapter;
import com.movieapi.movie.databinding.ActivitySearchResultBinding;
import com.movieapi.movie.model.ButtonItem;
import com.movieapi.movie.model.movie.Genre;
import com.movieapi.movie.model.search.SearchResponse;
import com.movieapi.movie.model.search.SearchAsyncTaskLoader;
import com.movieapi.movie.model.search.SearchResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchResultActivity extends AppCompatActivity {
    ActivitySearchResultBinding binding;
    private String query;
    List<SearchResult> searchResultList, filterRes;
    SearchResultAdapter searchResultAdapter;
    List<ButtonItem> buttonItemList;

    private boolean pagesOver = false;
    private int presentPage = 1;
    private boolean loading = true;
    private int previousTotal = 0;
    private int visibleThreshold = 5;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.view_search_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);

        Intent receiveIntent = getIntent();
        query = receiveIntent.getStringExtra("query");
        buttonItemList = (List<ButtonItem>) receiveIntent.getSerializableExtra("filterFilm");

        //Log.d("ButtonList: ", buttonItemList.get(1).getButtonText() + "");

        if (query == null || query.trim().isEmpty()) finish();
        setTitle(query.trim());

        filterRes = new ArrayList<>();
        searchResultList = new ArrayList<>();

        searchResultAdapter = new SearchResultAdapter(SearchResultActivity.this, filterRes);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(SearchResultActivity.this, 2);
        binding.recyclerViewSearch.setLayoutManager(gridLayoutManager);
        binding.recyclerViewSearch.setAdapter(searchResultAdapter);
        //final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SearchResultActivity.this, LinearLayoutManager.VERTICAL, false);


        binding.recyclerViewSearch.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = gridLayoutManager.getChildCount();
                int totalItemCount = gridLayoutManager.getItemCount();
                int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }

                if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    loadSearchResutls();
                    loading = true;
                }
            }
        });
        loadSearchResutls();
    }

    private void loadSearchResutls(){
        if (pagesOver) return;

        getLoaderManager().initLoader(presentPage, null, new LoaderManager.LoaderCallbacks<SearchResponse>() {
            @Override
            public Loader<SearchResponse> onCreateLoader(int id, Bundle args) {
                return new SearchAsyncTaskLoader(SearchResultActivity.this, query, String.valueOf(presentPage));
            }

            @Override
            public void onLoadFinished(Loader<SearchResponse> loader, SearchResponse searchResponse) {
                binding.searchProgressBar.setVisibility(View.GONE);
                if (searchResponse == null) return;
                if (searchResponse.getResults() == null) return;

                for (SearchResult searchResult : searchResponse.getResults()){
                    if (searchResult != null)
                        searchResultList.add(searchResult);
                }

                filterRes = filterResult(searchResultList, buttonItemList);
                searchResultAdapter.updateSearchResults(filterRes);
                searchResultAdapter.notifyDataSetChanged();

                if (filterRes.isEmpty())
                    binding.lnNotFound.setVisibility(View.VISIBLE);

                if (searchResponse.getPage() == searchResponse.getTotalPages())
                    pagesOver = true;
                else
                    presentPage++;
            }

            @Override
            public void onLoaderReset(Loader<SearchResponse> loader) {

            }
        }).forceLoad();
    }

    public List<SearchResult> filterResult(List<SearchResult> allRes, List<ButtonItem> filters){
        return allRes.stream()
                .filter(result -> {
                    boolean matches = true;
                    for (ButtonItem filter : filters){
                        if (filter.isSelected()){
                            matches &= (filter.getIdGenre() == 0 || result.getGenreList() != null && result.getGenreList().contains(new Genre(filter.getIdGenre()))) &&
                                    (filter.getMediaType() == null || filter.getMediaType().equals(result.getMediaType())) &&
                                    (filter.getTime() == null || filter.getTime().equals(result.getReleaseDate())) &&
                                    (filter.getRegion() == null || filter.getRegion().equals(result.getRegions()));
                        }
                    }
                    return matches;
                })
                .collect(Collectors.toList());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
