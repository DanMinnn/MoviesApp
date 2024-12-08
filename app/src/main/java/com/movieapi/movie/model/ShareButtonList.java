package com.movieapi.movie.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ShareButtonList extends ViewModel {

    private MutableLiveData<List<ButtonItem>> buttonList = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<ButtonItem>> getButtonList() {
        return buttonList;
    }

    public void setButtonList(List<ButtonItem> list) {
        buttonList.setValue(list);
    }

    public void addButton(ButtonItem buttonItem) {
        List<ButtonItem> currentList = buttonList.getValue();
        if (currentList != null) {
            currentList.add(buttonItem);
            buttonList.setValue(currentList);
        }
    }

    public void clearButtonList() {
        buttonList.setValue(new ArrayList<>());
    }
}
