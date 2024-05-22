package com.movieapi.movie.model.search;

import com.movieapi.movie.model.movie.Genre;

import java.util.List;

public class SearchResult {
    private Integer id;
    private String posterPath;
    private String name;
    private String mediaType;
    private String overview;
    private String releaseDate;
    private double voteAverage;
    private List<Genre> genreList;
    private String regions;
    private String year;
    private double popularity;

    public SearchResult(){

    }

    public SearchResult(Integer id, String posterPath, String name, String mediaType, String overview, String releaseDate, double voteAverage, List<Genre> genreList) {
        this.id = id;
        this.posterPath = posterPath;
        this.name = name;
        this.mediaType = mediaType;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.genreList = genreList;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public List<Genre> getGenreList() {
        return genreList;
    }

    public void setGenreList(List<Genre> genreList) {
        this.genreList = genreList;
    }

    public String getRegions() {
        return regions;
    }

    public void setRegions(String regions) {
        this.regions = regions;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }
}
