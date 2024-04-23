package com.movieapi.movie.request;

import com.movieapi.movie.model.cast.Person;
import com.movieapi.movie.model.movie.GenreMoviesResponse;
import com.movieapi.movie.model.movie.Movie;
import com.movieapi.movie.model.movie.MovieCastsOfPersonResponse;
import com.movieapi.movie.model.movie.MovieCreditsResponse;
import com.movieapi.movie.model.movie.NowShowingMoviesResponse;
import com.movieapi.movie.model.movie.PopularMoviesResponse;
import com.movieapi.movie.model.movie.SimilarMovieResponse;
import com.movieapi.movie.model.movie.TopRatedMoviesResponse;
import com.movieapi.movie.model.videos.TrailerResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("movie/now_playing")
    Call<NowShowingMoviesResponse> getNowShowingMovies(@Query("api_key") String apiKey, @Query("page")
                                                       Integer page, @Query("region") String region);
    @GET("movie/popular")
    Call<PopularMoviesResponse> getPopularMovies(@Query("api_key") String api_Key, @Query("page") int page);
    @GET("movie/top_rated")
    Call<TopRatedMoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey, @Query("page") Integer page, @Query("region") String region);
    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(@Path("movie_id") Integer movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/videos")
    Call<TrailerResponse> getTrailerMovie(@Path("id") Integer movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/credits")
    Call<MovieCreditsResponse> getMovieCredits(@Path("id") Integer movieId, @Query("api_key") String apiKey);

    @GET("movie/{id}/similar")
    Call<SimilarMovieResponse> getSimilarMovie(@Path("id") Integer movieId, @Query("api_key") String apiKey, @Query("page") Integer page);

    @GET("discover/movie")
    Call<GenreMoviesResponse> getMoviesByGenre(@Query("api_key") String apiKey, @Query("with_genres") Integer genreNumber, @Query("page") Integer page);

    //Cast
    @GET("person/{person_id}")
    Call<Person> getPersonDetails(@Path("person_id") Integer personId, @Query("api_key") String apiKey);

    @GET("person/{person_id}/movie_credits")
    Call<MovieCastsOfPersonResponse> getMovieCastOfPerson(@Path("person_id") Integer personId, @Query("api_key") String apiKey);
}
