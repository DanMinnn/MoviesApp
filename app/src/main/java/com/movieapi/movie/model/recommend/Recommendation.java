package com.movieapi.movie.model.recommend;

public class Recommendation {

    private int movie_id;
    private double predicted_rating;

    public Recommendation(int movie_id, double predicted_rating) {
        this.movie_id = movie_id;
        this.predicted_rating = predicted_rating;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(int movie_id) {
        this.movie_id = movie_id;
    }

    public double getPredicted_rating() {
        return predicted_rating;
    }

    public void setPredicted_rating(double predicted_rating) {
        this.predicted_rating = predicted_rating;
    }
}
