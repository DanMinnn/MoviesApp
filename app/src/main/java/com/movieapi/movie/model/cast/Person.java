package com.movieapi.movie.model.cast;

import com.google.gson.annotations.SerializedName;

public class Person {
    @SerializedName("adult")
    private Boolean adult;
    @SerializedName("biography")
    private String biography;
    @SerializedName("birthday")
    private String dateOfBirth;
    @SerializedName("deathday")
    private String dateOfDeadth;
    @SerializedName("gender")
    private Integer gender;
    @SerializedName("id")
    private int id;
    @SerializedName("imdb_id")
    private String imdb_id;
    @SerializedName("name")
    private String name;
    @SerializedName("place_of_birth")
    private String place_of_birth;
    @SerializedName("popularity")
    private Double popularity;
    @SerializedName("profile_path")
    private String profile_path;

    public Person(Boolean adult, String biography, String dateOfBirth, String dateOfDeadth, Integer gender, int id, String imdb_id, String name, String place_of_birth, Double popularity, String profile_path) {
        this.adult = adult;
        this.biography = biography;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeadth = dateOfDeadth;
        this.gender = gender;
        this.id = id;
        this.imdb_id = imdb_id;
        this.name = name;
        this.place_of_birth = place_of_birth;
        this.popularity = popularity;
        this.profile_path = profile_path;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getDateOfDeadth() {
        return dateOfDeadth;
    }

    public void setDateOfDeadth(String dateOfDeadth) {
        this.dateOfDeadth = dateOfDeadth;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_of_birth() {
        return place_of_birth;
    }

    public void setPlace_of_birth(String place_of_birth) {
        this.place_of_birth = place_of_birth;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public String getProfile_path() {
        return profile_path;
    }

    public void setProfile_path(String profile_path) {
        this.profile_path = profile_path;
    }
}
