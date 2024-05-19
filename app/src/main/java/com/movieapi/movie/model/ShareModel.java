package com.movieapi.movie.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class ShareModel extends ViewModel {
    private final MutableLiveData<List<ButtonItem>> selectedButtons = new MutableLiveData<>();

    public MutableLiveData<List<ButtonItem>> getSelectedButtons() {
        return selectedButtons;
    }

    public void selectButton(List<ButtonItem> buttonItemList) {
        selectedButtons.setValue(buttonItemList);
    }
}
