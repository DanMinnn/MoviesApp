package com.movieapi.movie.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.movieapi.movie.R;
import com.movieapi.movie.activity.SearchResultActivity;
import com.movieapi.movie.adapter.FilterAdapter;
import com.movieapi.movie.adapter.RecentSearchAdapter;
import com.movieapi.movie.controller.interfaces.ButtonListener;
import com.movieapi.movie.database.DatabaseHelper;
import com.movieapi.movie.database.search.RecentSearch;
import com.movieapi.movie.database.search.SearchDatabase;
import com.movieapi.movie.model.ButtonItem;
import com.movieapi.movie.model.ShareModel;
import com.movieapi.movie.utils.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class SearchFragment extends Fragment{
    EditText edSearchView;
    FloatingActionButton fabSearch;
    TextView searchRecent;
    RecyclerView recentSearchRecView, filterRecView;

    FilterAdapter adapter;
    private List<ButtonItem> buttonItemList;
    private ShareModel viewModel;
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
        filterRecView = view.findViewById(R.id.filter_recView);

        buttonItemList = new ArrayList<>();

        viewModel = new ViewModelProvider(requireActivity()).get(ShareModel.class);

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
                iSearchResult.putExtra("filterFilm", (Serializable) buttonItemList);
                startActivity(iSearchResult);
            }
        });

        fabSearch.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                eventSortFilter();
                return false;
            }
        });
    }

    private void eventSortFilter(){
        Dialog dialog = new Dialog(getContext());
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.dialog_sort_filter);

        // event handling
        findViewById(dialog);

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialog.show();
    }

    private void findViewById(Dialog dialog){
        LinearLayout lnCate, lnRegions, lnGenre, lnTime,lnSort;
        Button btnReset, btnApply;

        lnCate = dialog.findViewById(R.id.lnCategories);
        for (int i = 0; i < lnCate.getChildCount(); i++){
            View child = lnCate.getChildAt(i);
            if (child instanceof Button){
                Button button = (Button) child;
                setToggleOnTouchListener(button);
            }
        }

        lnRegions = dialog.findViewById(R.id.lnRegions);
        for (int i = 0; i < lnRegions.getChildCount(); i++){
            View child = lnRegions.getChildAt(i);
            if (child instanceof Button){
                Button button = (Button) child;
                setToggleOnTouchListener(button);
            }
        }

        lnGenre = dialog.findViewById(R.id.lnGenre);
        for (int i = 0; i < lnGenre.getChildCount(); i++){
            View child = lnGenre.getChildAt(i);
            if (child instanceof Button){
                Button button = (Button) child;
                setToggleOnTouchListener(button);
            }
        }

        lnTime = dialog.findViewById(R.id.lnTime);
        for (int i = 0; i < lnTime.getChildCount(); i++){
            View child = lnTime.getChildAt(i);
            if (child instanceof Button){
                Button button = (Button) child;
                setToggleOnTouchListener(button);
            }
        }

        lnSort = dialog.findViewById(R.id.lnSort);
        for (int i = 0; i < lnSort.getChildCount(); i++){
            View child = lnSort.getChildAt(i);
            if (child instanceof Button){
                Button button = (Button) child;
                setToggleOnTouchListener(button);
            }
        }

        btnReset = dialog.findViewById(R.id.btnReset);
        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout lnCate, lnRegions, lnGenre, lnTime,lnSort;

                lnCate = dialog.findViewById(R.id.lnCategories);
                for (int i = 0; i < lnCate.getChildCount(); i++){
                    View child = lnCate.getChildAt(i);
                    if (child instanceof Button){
                        Button button = (Button) child;
                        button.setPressed(false);
                    }
                }

                lnRegions = dialog.findViewById(R.id.lnRegions);
                for (int i = 0; i < lnRegions.getChildCount(); i++){
                    View child = lnRegions.getChildAt(i);
                    if (child instanceof Button){
                        Button button = (Button) child;
                        button.setPressed(false);
                    }
                }

                lnGenre = dialog.findViewById(R.id.lnGenre);
                for (int i = 0; i < lnGenre.getChildCount(); i++){
                    View child = lnGenre.getChildAt(i);
                    if (child instanceof Button){
                        Button button = (Button) child;
                        button.setPressed(false);
                    }
                }

                lnTime = dialog.findViewById(R.id.lnTime);
                for (int i = 0; i < lnTime.getChildCount(); i++){
                    View child = lnTime.getChildAt(i);
                    if (child instanceof Button){
                        Button button = (Button) child;
                        button.setPressed(false);
                    }
                }

                lnSort = dialog.findViewById(R.id.lnSort);
                for (int i = 0; i < lnSort.getChildCount(); i++){
                    View child = lnSort.getChildAt(i);
                    if (child instanceof Button){
                        Button button = (Button) child;
                        button.setPressed(false);
                    }
                }

            }
        });

        btnApply = dialog.findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter = new FilterAdapter(getContext(), buttonItemList);
                filterRecView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
                filterRecView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                filterRecView.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setToggleOnTouchListener(Button button){
        button.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    boolean isPress = !button.isPressed();
                    button.setPressed(isPress);

                    ButtonItem buttonItem = new ButtonItem(button.getText().toString());
                    String buttonText = button.getText().toString();

                    if(buttonItemList.contains(buttonItem)){
                        int index = buttonItemList.indexOf(buttonItem);
                        buttonItem = buttonItemList.get(index);
                        buttonItem.setSelected(!buttonItem.isSelected());
                    }else {
                        buttonItem.setSelected(true);

                        //set categoriesId
                        setCateId(buttonText, buttonItem);
                        //set genresId
                        setIdGenre(buttonText, buttonItem);
                        //set time
                        setTime(buttonText, buttonItem);
                        //set region
                        setRegion(buttonText, buttonItem);

                        buttonItemList.add(buttonItem);
                    }

                    button.setPressed(buttonItem.isSelected());
                }
                return true;
            }
        });
    }

    private void removeButtonFromList(String buttonText){
        for (Iterator<ButtonItem> iterator = buttonItemList.iterator(); iterator.hasNext(); ) {
            ButtonItem buttonItem = iterator.next();
            if (buttonItem.getButtonText().equals(buttonText)) {
                iterator.remove();
            }
        }
    }

    private void setIdGenre(String genres, ButtonItem buttonItem){
        switch (genres){
            case "Action":
                buttonItem.setIdGenre(Constants.ACTION_MOVIES_TYPE);
                break;
            case "Comedy":
                buttonItem.setIdGenre(Constants.COMEDY_MOVIES_TYPE);
                break;
            case "Romance":
                buttonItem.setIdGenre(Constants.ROMANCE_MOVIES_TYPE);
                break;
            case "Aventure":
                buttonItem.setIdGenre(Constants.ADVENTURE_MOVIES_TYPE);
                break;
            case "Animation":
                buttonItem.setIdGenre(Constants.ANIMATION_MOVIES_TYPE);
                break;
            case "Crime":
                buttonItem.setIdGenre(Constants.CRIME_MOVIES_TYPE);
                break;
            case "Documentary":
                buttonItem.setIdGenre(Constants.DOCUMENTARY_MOVIES_TYPE);
                break;
            case "Drama":
                buttonItem.setIdGenre(Constants.DRAMA_MOVIES_TYPE);
                break;
            case "Horror":
                buttonItem.setIdGenre(Constants.HORROR_MOVIES_TYPE);
                break;
            case "Scientific":
                buttonItem.setIdGenre(Constants.SCIFI_MOVIES_TYPE);
                break;
            case "Thriller":
                buttonItem.setIdGenre(Constants.THRILLER_MOVIES_TYPE);
                break;
            case "War":
                buttonItem.setIdGenre(Constants.WAR_MOVIES_TYPE);
                break;
            case "Western":
                buttonItem.setIdGenre(Constants.WESTERN_MOVIES_TYPE);
                break;
            default:
                break;
        }
    }

    private void setCateId(String cate, ButtonItem buttonItem){
        switch (cate){
            case "Movie":
                buttonItem.setMediaType("movie");
                break;
            case "TV Shows":
                buttonItem.setMediaType("tv");
                break;
            case "K-Drama":
                buttonItem.setMediaType("k-drama");
                break;
            case "Anime":
                buttonItem.setMediaType("anime");
                break;
            default:
                break;
        }
    }

    private void setTime(String time, ButtonItem buttonItem){
        switch (time){
            case "All Periods":
                buttonItem.setTime("All Periods");
                break;
            case "2024":
                buttonItem.setTime("2024");
                break;
            case "2023":
                buttonItem.setTime("2023");
                break;
            case "2022":
                buttonItem.setTime("2022");
                break;
            case "2021":
                buttonItem.setTime("2021");
                break;
            case "2020":
                buttonItem.setTime("2020");
                break;
            case "2019":
                buttonItem.setTime("2019");
                break;
            case "2018":
                buttonItem.setTime("2018");
                break;
            default:
                break;
        }
    }

    private void setRegion(String region, ButtonItem buttonItem){
        switch (region){
            case "All Regions":
                buttonItem.setRegion("All Regions");
                break;
            case "US":
                buttonItem.setRegion("US");
                break;
            case "South Korea":
                buttonItem.setRegion("KR");
                break;
            case "Hong Kong":
                buttonItem.setRegion("HK");
                break;
            case "Japan":
                buttonItem.setRegion("JP");
                break;
            case "France":
                buttonItem.setRegion("FR");
                break;
            default:
                break;
        }
    }
}
