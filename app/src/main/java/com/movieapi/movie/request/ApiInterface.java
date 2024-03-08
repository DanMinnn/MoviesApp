package com.movieapi.movie.request;

import android.net.TransportInfo;

import com.movieapi.movie.network.movie.Movie;
import com.movieapi.movie.network.movie.MovieCreditsResponse;
import com.movieapi.movie.network.movie.NowShowingMoviesResponse;
import com.movieapi.movie.network.movie.PopularMoviesResponse;
import com.movieapi.movie.network.movie.SimilarMovieResponse;
import com.movieapi.movie.network.videos.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("movie/now_playing")
    Call<NowShowingMoviesResponse> getNowShowingMovies(@Query("api_key") String apiKey, @Query("page")
                                                       Integer page, @Query("region") String region);
    @GET("movie/popular")
    Call<PopularMoviesResponse> getPopularMovies(@Query("api_key") String apiKey, @Query("page") Integer page);

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(@Path("movie_id") Integer movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailerResponse> getTrailerMovie(@Path("id") Integer movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/credits")
    Call<MovieCreditsResponse> getMovieCredits(@Path("id") Integer movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/similar")
    Call<SimilarMovieResponse> getSimilarMovie(@Path("id") Integer movieId, @Query("api_key") String apiKey, @Query("page") Integer page);
}
