package com.movieapi.movie.database;

import android.content.Context;

import com.movieapi.movie.database.movies.FavMovie;
import com.movieapi.movie.database.movies.MovieDatabase;

public class DatabaseHelper {
    public static boolean isFavMovie(Context context, Integer movieId){
        if (movieId == null) return false;
        FavMovie f = MovieDatabase.getInstance(context).movieDao().getMovieById(movieId);
        return f != null;
    }

    public static boolean isFavSeries(){
        return true;
    }
}
