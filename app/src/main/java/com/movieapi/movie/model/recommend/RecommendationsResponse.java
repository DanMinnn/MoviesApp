package com.movieapi.movie.model.recommend;

import java.util.List;

public class RecommendationsResponse {

    private String user_id;
    private List<Recommendation> recommendations;

    public RecommendationsResponse(String user_id, List<Recommendation> recommendations) {
        this.user_id = user_id;
        this.recommendations = recommendations;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<Recommendation> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        this.recommendations = recommendations;
    }
}
