package com.movieapi.movie.database.series;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.movieapi.movie.database.movies.FavMovie;

import java.util.List;

@Dao
public interface SeriesDao {

    @androidx.room.Query("SELECT * FROM favSeries WHERE user_id = :userId")
    LiveData<List<FavSeries>> getAllFavMovies(String userId);

    @androidx.room.Query("SELECT * FROM favSeries WHERE series_id = :id AND user_id = :userId")
    FavSeries getSeriesById(int id, String userId);

    @androidx.room.Query("DELETE FROM favSeries WHERE series_id = :id AND user_id = :userId")
    void deleteSeriesById(int id, String userId);

    @Insert(onConflict = REPLACE)
    void insertMovie(FavSeries favSeries);

    @Delete
    void deleteMovie(FavSeries favSeries);
}
