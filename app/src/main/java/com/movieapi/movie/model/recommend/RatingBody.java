package com.movieapi.movie.model.recommend;

public class RatingBody {

    private String user_id;
    private int item_id;
    private float rating;

    public RatingBody(String user_id, int movie_id, float rating) {
        this.user_id = user_id;
        this.item_id = movie_id;
        this.rating = rating;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
