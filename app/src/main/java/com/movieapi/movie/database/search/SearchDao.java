package com.movieapi.movie.database.search;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SearchDao {
    @Query("SELECT * FROM recentSearches WHERE user_id = :userId")
    LiveData<List<RecentSearch>> getAllRecentSearch(String userId);

    @Query("SELECT * FROM recentSearches WHERE search_name = :name AND user_id = :userId ")
    RecentSearch getSearchesByName(String name, String userId);

    @Query("DELETE FROM recentSearches WHERE search_name = :name AND user_id = :userId ")
    void deleteSearchesByName(String name, String userId);

    @Insert(onConflict = REPLACE)
    void insertSearch(RecentSearch recentSearch);
}
