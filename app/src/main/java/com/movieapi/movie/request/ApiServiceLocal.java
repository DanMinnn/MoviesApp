package com.movieapi.movie.request;

import com.movieapi.movie.model.test.ApiResponse;
import com.movieapi.movie.model.recommend.RatingBody;
import com.movieapi.movie.model.recommend.RecommendationsResponse;
import com.movieapi.movie.model.test.RequestData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiServiceLocal {
    @POST("/submit")
    Call<ApiResponse> sendData(@Body RequestData data);

    @POST("/submit-rating-movie")
    Call<Void> submitRatingMovie(@Body RatingBody ratingBody);

    @POST("/submit-rating-tv-series")
    Call<Void> submitRatingSeries(@Body RatingBody ratingBody);

    @GET("recommendations/{user_id}")
    Call<RecommendationsResponse> getRecommendations(@Path("user_id") String userId);

    @GET("recommendations-series/{user_id}")
    Call<RecommendationsResponse> getRecommendationsSeries(@Path("user_id") String userId);
}
