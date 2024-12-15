package com.movieapi.movie.request;

import com.movieapi.movie.utils.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiLocal {

    //private static final String BASE_URL = "http://192.168.x.x:5000"; // Đổi IP thành IP của Flask Server
    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constants.LOCAL_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static ApiServiceLocal apiServiceLocal = retrofit.create(ApiServiceLocal.class);

    public static ApiServiceLocal getApiLocal(){
        return apiServiceLocal;
    }
}
