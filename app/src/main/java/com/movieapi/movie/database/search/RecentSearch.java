package com.movieapi.movie.database.search;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "recentSearches")
public class RecentSearch   {
    @PrimaryKey(autoGenerate = true)
    private int searchId;

    @ColumnInfo(name = "search_name")
    private String search_name;

    public RecentSearch(int searchId, String search_name) {
        this.searchId = searchId;
        this.search_name = search_name;
    }

    @Ignore
    public RecentSearch(String search_name) {
        this.search_name = search_name;
    }

    public int getSearchId() {
        return searchId;
    }

    public void setSearchId(int searchId) {
        this.searchId = searchId;
    }

    public String getSearch_name() {
        return search_name;
    }

    public void setSearch_name(String search_name) {
        this.search_name = search_name;
    }
}
