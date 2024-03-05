package com.movieapi.movie.database.movies;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;

import java.util.List;

import retrofit2.http.Query;

@Dao
public interface MovieDao {
    @androidx.room.Query("SELECT * FROM favMovies")
    LiveData<List<FavMovie>> getAllFavMovies();

    @androidx.room.Query("SELECT * FROM favMovies WHERE movie_id = :id")
    FavMovie getMovieById(int id);

    @androidx.room.Query("DELETE FROM favMovies WHERE movie_id = :id")
    void deleteMovieById(int id);

    @Insert(onConflict = REPLACE)
    void insertMovie(FavMovie favMovie);

    @Delete
    void deleteMovie(FavMovie favMovie);
}
