package com.movieapi.movie.controller.interfaces;

import com.movieapi.movie.model.ButtonItem;

import java.util.List;

public interface ButtonListener {
    void onButtonChoices(List<ButtonItem> buttonItemList);
}
