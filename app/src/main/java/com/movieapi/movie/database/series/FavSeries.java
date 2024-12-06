package com.movieapi.movie.database.series;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "favSeries")
public class FavSeries {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @ColumnInfo(name = "user_id")
    private String userId;
    @ColumnInfo(name = "series_id")
    private Integer series_id;
    @ColumnInfo(name = "still_path")
    private String still_path;
    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "voteAverage")
    private Double voteAverage;

    public FavSeries(int id, String userId, Integer series_id, String still_path, String name, double voteAverage) {
        this.id = id;
        this.userId = userId;
        this.series_id = series_id;
        this.still_path = still_path;
        this.name = name;
        this.voteAverage = voteAverage;
    }

    @Ignore
    public FavSeries(String userId, Integer series_id, String still_path, String name, double voteAverage) {
        this.userId = userId;
        this.series_id = series_id;
        this.still_path = still_path;
        this.name = name;
        this.voteAverage = voteAverage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getSeries_id() {
        return series_id;
    }

    public void setSeries_id(Integer series_id) {
        this.series_id = series_id;
    }

    public String getStill_path() {
        return still_path;
    }

    public void setStill_path(String still_path) {
        this.still_path = still_path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }
}
