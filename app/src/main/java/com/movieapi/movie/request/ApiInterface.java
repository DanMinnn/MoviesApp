package com.movieapi.movie.request;

import com.movieapi.movie.model.ApiResponse;
import com.movieapi.movie.model.RatingBody;
import com.movieapi.movie.model.RequestData;
import com.movieapi.movie.model.SessionResponse;
import com.movieapi.movie.model.TokenBody;
import com.movieapi.movie.model.TokenResponse;
import com.movieapi.movie.model.cast.Person;
import com.movieapi.movie.model.movie.GenreMoviesResponse;
import com.movieapi.movie.model.movie.Movie;
import com.movieapi.movie.model.movie.MovieCastsOfPersonResponse;
import com.movieapi.movie.model.movie.MovieCreditsResponse;
import com.movieapi.movie.model.movie.NowShowingMoviesResponse;
import com.movieapi.movie.model.movie.PopularMoviesResponse;
import com.movieapi.movie.model.movie.SimilarMovieResponse;
import com.movieapi.movie.model.movie.TopRatedMoviesResponse;
import com.movieapi.movie.model.series.AiringTodaySeriesResponse;
import com.movieapi.movie.model.series.OnTheAirSeriesResponse;
import com.movieapi.movie.model.series.PopularSeriesResponse;
import com.movieapi.movie.model.series.SeasonDetailsResponse;
import com.movieapi.movie.model.series.Series;
import com.movieapi.movie.model.series.SeriesCastsOfPersonResponse;
import com.movieapi.movie.model.series.SeriesCreditsResponse;
import com.movieapi.movie.model.series.SimilarSeriesResponse;
import com.movieapi.movie.model.series.TopRatedSeriesResponse;
import com.movieapi.movie.model.videos.TrailerResponse;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("movie/now_playing")
    Call<NowShowingMoviesResponse> getNowShowingMovies(@Query("api_key") String apiKey, @Query("page") Integer page, @Query("region") String region);

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
    Call<GenreMoviesResponse> getMoviesByGenre(@Query("api_key") String apiKey, @Query("with_original_language") String region, @Query("primary_release_year") Integer year, @Query("with_genres") List<Integer> genreNumber, @Query("page") Integer page);

    //Cast
    @GET("person/{person_id}")
    Call<Person> getPersonDetails(@Path("person_id") Integer personId, @Query("api_key") String apiKey);

    @GET("person/{person_id}/movie_credits")
    Call<MovieCastsOfPersonResponse> getMovieCastOfPerson(@Path("person_id") Integer personId, @Query("api_key") String apiKey);

    @GET("person/{id}/tv_credits")
    Call<SeriesCastsOfPersonResponse> getTVCastsOfPerson(@Path("id") Integer personId, @Query("api_key") String apiKey);

    // Series
    @GET("tv/airing_today")
    Call<AiringTodaySeriesResponse> getAiringTodaySeries(@Query("api_key") String apiKey, @Query("page") Integer page);

    @GET("tv/on_the_air")
    Call<OnTheAirSeriesResponse> getOnTheAirSeries(@Query("api_key") String apiKey, @Query("page") Integer page);

    @GET("tv/popular")
    Call<PopularSeriesResponse> getPopularSeries(@Query("api_key") String apiKey, @Query("page") Integer page);

    @GET("tv/top_rated")
    Call<TopRatedSeriesResponse> getTopRatedSeries(@Query("api_key") String apiKey, @Query("page") Integer page);

    @GET("tv/{id}")
    Call<Series> getSeriesDetails(@Path("id") Integer tvShowId, @Query("api_key") String apiKey, @Query("append_to_response") String append_to_response);

    @GET("tv/{id}/videos")
    Call<TrailerResponse> getSeriesVideos(@Path("id") Integer movieId, @Query("api_key") String apiKey);

    @GET("tv/{id}/credits")
    Call<SeriesCreditsResponse> getSeriesCredits(@Path("id") Integer movieId, @Query("api_key") String apiKey);

    @GET("tv/{id}/similar")
    Call<SimilarSeriesResponse> getSimilarSeries(@Path("id") Integer movieId, @Query("api_key") String apiKey, @Query("page") Integer page);

    @GET("tv/{id}/season/{season}")
    Call<SeasonDetailsResponse> getSeasonDetails(@Path("id") Integer id, @Path("season") Integer season_number, @Query("api_key") String apiKey);

    @POST("movie/{movie_id}/rating")
    Call<Void> addRating(@Path("movie_id") int movieId,
                         @Query("api_key") String apiKey,
                         @Query("session_id") String sessionId,
                         @Body RequestBody rating);

    @GET("authentication/token/new")
    Call<TokenResponse> getRequestToken(@Query("api_key") String apiKey);

    @POST("authentication/session/new")
    Call<SessionResponse> createSession(@Query("api_key") String apiKey, @Body RequestBody body);


}
