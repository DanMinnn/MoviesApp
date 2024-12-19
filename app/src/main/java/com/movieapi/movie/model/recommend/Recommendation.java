package com.movieapi.movie.model.recommend;

public class Recommendation {

    private int item_id;
    private double predicted_rating;

    public Recommendation(int movie_id, double predicted_rating) {
        this.item_id = movie_id;
        this.predicted_rating = predicted_rating;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public double getPredicted_rating() {
        return predicted_rating;
    }

    public void setPredicted_rating(double predicted_rating) {
        this.predicted_rating = predicted_rating;
    }
}
