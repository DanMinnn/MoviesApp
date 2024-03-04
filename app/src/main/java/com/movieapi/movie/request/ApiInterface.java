package com.movieapi.movie.request;

import com.movieapi.movie.network.movie.NowShowingMoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("movie/now_playing")
    Call<NowShowingMoviesResponse> getNowShowingMovies(@Query("api_key") String apiKey, @Query("page")
                                                       Integer page, @Query("region") String region);
}
