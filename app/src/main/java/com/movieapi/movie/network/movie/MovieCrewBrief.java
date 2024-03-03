package com.movieapi.movie.network.movie;

import com.google.gson.annotations.SerializedName;

public class MovieCrewBrief {
    @SerializedName("adult")
    private Boolean adult;

    @SerializedName("gender")
    private Integer gender;

    @SerializedName("id")
    private Integer id;

    @SerializedName("known_for_department")
    private String known_for_department;

    @SerializedName("name")
    private String name;

    @SerializedName("original_name")
    private String original_name;

    @SerializedName("popularity")
    private Double popularity;

    @SerializedName("profile_path")
    private String profile_path;

    @SerializedName("credit_id")
    private String credit_id;

    @SerializedName("department")
    private String department;

    @SerializedName("job")
    private String job;

    public MovieCrewBrief(Boolean adult, Integer gender, Integer id, String known_for_department, String name, String original_name, Double popularity, String profile_path, String credit_id, String department, String job) {
        this.adult = adult;
        this.gender = gender;
        this.id = id;
        this.known_for_department = known_for_department;
        this.name = name;
        this.original_name = original_name;
        this.popularity = popularity;
        this.profile_path = profile_path;
        this.credit_id = credit_id;
        this.department = department;
        this.job = job;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getOriginal_name() {
        return original_name;
    }

    public void setOriginal_name(String original_name) {
        this.original_name = original_name;
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

    public String getCredit_id() {
        return credit_id;
    }

    public void setCredit_id(String credit_id) {
        this.credit_id = credit_id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
