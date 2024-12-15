package com.movieapi.movie.request;

import com.movieapi.movie.model.ApiResponse;
import com.movieapi.movie.model.RatingBody;
import com.movieapi.movie.model.RecommendationsResponse;
import com.movieapi.movie.model.RequestData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiServiceLocal {
    @POST("/submit")
    Call<ApiResponse> sendData(@Body RequestData data);

    @POST("/submit-rating")
    Call<Void> submitRating(@Body RatingBody ratingBody);

    @GET("recommendations/{user_id}")
    Call<RecommendationsResponse> getRecommendations(@Path("user_id") String userId);
}
