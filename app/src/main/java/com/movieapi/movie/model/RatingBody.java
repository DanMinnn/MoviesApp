package com.movieapi.movie.model;

public class RatingBody {

    private String user_id;
    private int movie_id;
    private float rating;

    public RatingBody(String user_id, int movie_id, float rating) {
        this.user_id = user_id;
        this.movie_id = movie_id;
        this.rating = rating;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
