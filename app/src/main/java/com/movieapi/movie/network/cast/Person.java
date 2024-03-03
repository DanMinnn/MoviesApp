package com.movieapi.movie.network.cast;

import com.google.gson.annotations.SerializedName;

public class Person {
    @SerializedName("adult")
    private Boolean adult;
    @SerializedName("also_known_as")
    private String also_known_as;
    @SerializedName("biography")
    private String biography;
    @SerializedName("birthday")
    private String dateOfBirth;
    @SerializedName("deathday")
    private String dateOfDeadth;
    @SerializedName("gender")
    private int gender;
    @SerializedName("homepage")
    private String homepage;
    @SerializedName("id")
    private int id;
    @SerializedName("imdb_id")
    private String imdb_id;
    @SerializedName("known_for_department")
    private String known_for_department;
    @SerializedName("name")
    private String name;
    @SerializedName("place_of_birth")
    private String place_of_birth;
    @SerializedName("popularity")
    private Double popularity;
    @SerializedName("profile_path")
    private String profile_path;

    public Person(Boolean adult, String also_known_as, String biography, String dateOfBirth, String dateOfDeadth, int gender, String homepage, int id, String imdb_id, String known_for_department, String name, String place_of_birth, Double popularity, String profile_path) {
        this.adult = adult;
        this.also_known_as = also_known_as;
        this.biography = biography;
        this.dateOfBirth = dateOfBirth;
        this.dateOfDeadth = dateOfDeadth;
        this.gender = gender;
        this.homepage = homepage;
        this.id = id;
        this.imdb_id = imdb_id;
        this.known_for_department = known_for_department;
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

    public String getAlso_known_as() {
        return also_known_as;
    }

    public void setAlso_known_as(String also_known_as) {
        this.also_known_as = also_known_as;
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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
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

    public String getKnown_for_department() {
        return known_for_department;
    }

    public void setKnown_for_department(String known_for_department) {
        this.known_for_department = known_for_department;
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
