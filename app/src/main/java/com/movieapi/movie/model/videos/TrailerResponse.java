package com.movieapi.movie.model.videos;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TrailerResponse {
    @SerializedName("id")
    private Integer id;
    @SerializedName("results")
    private List<Trailer> trailers;

    public TrailerResponse(Integer id, List<Trailer> trailers) {
        this.id = id;
        this.trailers = trailers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
