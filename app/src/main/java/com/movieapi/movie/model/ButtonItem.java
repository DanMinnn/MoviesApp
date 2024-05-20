package com.movieapi.movie.model;

import androidx.annotation.Nullable;

import java.io.Serializable;

public class ButtonItem implements Serializable {
    private String buttonText;
    private boolean isSelected;
    private int idGenre;
    private String mediaType;
    private String time, region;

    public ButtonItem(String buttonText) {
        this.buttonText = buttonText;
        this.isSelected = false;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getIdGenre() {
        return idGenre;
    }

    public void setIdGenre(int idGenre) {
        this.idGenre = idGenre;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getButtonText() {
        return buttonText;
    }

    public void setButtonText(String buttonText) {
        this.buttonText = buttonText;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ButtonItem that = (ButtonItem) o;

        return buttonText != null ? buttonText.equals(that.buttonText) : that.buttonText == null;
    }

    @Override
    public int hashCode() {
        return buttonText != null ? buttonText.hashCode() : 0;
    }
}
