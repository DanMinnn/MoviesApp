package com.movieapi.movie.model;

import androidx.annotation.Nullable;

public class ButtonItem {
    private String buttonText;
    private boolean isSelected;

    public ButtonItem(String buttonText) {
        this.buttonText = buttonText;
        this.isSelected = false;
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
