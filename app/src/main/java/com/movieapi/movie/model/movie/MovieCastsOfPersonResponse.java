package com.movieapi.movie.model.movie;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MovieCastsOfPersonResponse {
    @SerializedName("id")
    private Integer id;
    @SerializedName("cast")
    private List<MovieCastOfPerson> casts;
    //crew missing

    public MovieCastsOfPersonResponse(Integer id, List<MovieCastOfPerson> casts) {
        this.id = id;
        this.casts = casts;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<MovieCastOfPerson> getCasts() {
        return casts;
    }

    public void setCasts(List<MovieCastOfPerson> casts) {
        this.casts = casts;
    }
}
