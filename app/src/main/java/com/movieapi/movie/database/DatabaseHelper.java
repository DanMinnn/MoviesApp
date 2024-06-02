package com.movieapi.movie.database;

import android.content.Context;

import com.movieapi.movie.database.movies.FavMovie;
import com.movieapi.movie.database.movies.MovieDatabase;
import com.movieapi.movie.database.search.RecentSearch;
import com.movieapi.movie.database.search.SearchDatabase;

public class DatabaseHelper {
    public static boolean isFavMovie(Context context, Integer movieId, String userId){
        if (movieId == null) return false;
        FavMovie f = MovieDatabase.getInstance(context).movieDao().getMovieById(movieId, userId);
        return f != null;
    }

    public static boolean isRecentSearch(Context context, String name, String userId){
        if (name == null) return false;
        RecentSearch recentSearch = SearchDatabase.getInstance(context).searchDao().getSearchesByName(name, userId);
        return recentSearch != null;
    }
}
