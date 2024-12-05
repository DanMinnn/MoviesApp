package com.movieapi.movie.controller;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<Integer> idEpisode = new MutableLiveData<>();
    private final MutableLiveData<Integer> numberOfSeason = new MutableLiveData<>();

    public void setIdEpisode(int id){
        idEpisode.setValue(id);
    }

    public LiveData<Integer> getIdEpisode(){
        return idEpisode;
    }

    public void setNumberOfSeason(int number){
        numberOfSeason.setValue(number);
    }

    public LiveData<Integer> getNumberOfSeason(){
        return numberOfSeason;
    }
}
