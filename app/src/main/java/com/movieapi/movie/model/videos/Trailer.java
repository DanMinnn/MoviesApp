package com.movieapi.movie.model.videos;

import com.google.gson.annotations.SerializedName;

public class Trailer {
    @SerializedName("name")
    private String name;
    @SerializedName("key")
    private String key;
    @SerializedName("site")
    private String site;
    @SerializedName("size")
    private Integer size;
    @SerializedName("type")
    private String type;
    @SerializedName("id")
    private String id;

    public Trailer(String name, String key, String site, Integer size, String type, String id) {
        this.name = name;
        this.key = key;
        this.site = site;
        this.size = size;
        this.type = type;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
