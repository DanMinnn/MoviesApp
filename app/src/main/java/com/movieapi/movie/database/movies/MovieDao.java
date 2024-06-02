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
    @androidx.room.Query("SELECT * FROM favMovies WHERE user_id = :userId")
    LiveData<List<FavMovie>> getAllFavMovies(String userId);

    @androidx.room.Query("SELECT * FROM favMovies WHERE movie_id = :id AND user_id = :userId")
    FavMovie getMovieById(int id, String userId);

    @androidx.room.Query("DELETE FROM favMovies WHERE movie_id = :id AND user_id = :userId")
    void deleteMovieById(int id, String userId);

    @Insert(onConflict = REPLACE)
    void insertMovie(FavMovie favMovie);

    @Delete
    void deleteMovie(FavMovie favMovie);
}
