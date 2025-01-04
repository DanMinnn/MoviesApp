package com.movieapi.movie.model.search;

import android.content.AsyncTaskLoader;
import android.content.Context;

import androidx.annotation.Nullable;

import com.movieapi.movie.model.movie.Genre;
import com.movieapi.movie.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchAsyncTaskLoader extends AsyncTaskLoader<SearchResponse>   {
    private Context mContext;
    private String query;
    private String page;

    public SearchAsyncTaskLoader(Context context, String query, String page) {
        super(context);
        this.mContext = context;
        this.query = query;
        this.page = page;
    }


    @Nullable
    @Override
    public SearchResponse loadInBackground() {
        try {
            query = query.trim();
            query = query.replace(" ", "+");

            String urlString = "https://api.themoviedb.org/3/" + "search/multi"
                    + "?"
                    + "api_key=" + Constants.API_KEY
                    + "&"
                    + "query=" + query
                    + "&"
                    + "page=" + page;

            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");

            httpURLConnection.connect();

            if (httpURLConnection.getResponseCode() != 200) return null;

            InputStream inputStream = httpURLConnection.getInputStream();
            Scanner scanner = new Scanner(inputStream);
            String jSonString = "";
            while (scanner.hasNext())
                jSonString += scanner.nextLine();

            //Parse JSON
            JSONObject searchJsonObject = new JSONObject(jSonString);
            SearchResponse searchResponse = new SearchResponse();
            searchResponse.setPage(searchJsonObject.getInt("page"));

            JSONArray resutls = searchJsonObject.getJSONArray("results");

            searchResponse.setTotalPages(searchJsonObject.getInt("total_pages"));

            List<SearchResult> searchResults = new ArrayList<>();

            for (int i = 0; i < resutls.length(); i++){
                JSONObject result = (JSONObject) resutls.get(i);
                SearchResult searchResult = new SearchResult();
                switch (result.getString("media_type")){
                    case "movie":
                        searchResult.setId(result.getInt("id"));
                        searchResult.setPosterPath(result.getString("poster_path"));
                        searchResult.setTitle(result.getString("title"));
                        searchResult.setMediaType("movie");
                        searchResult.setOverview(result.getString("overview"));
                        searchResult.setVoteAverage(result.getDouble("vote_average"));
                        searchResult.setRegions(result.getString("original_language"));
                        searchResult.setPopularity(result.getDouble("popularity"));

                        JSONArray genreIdsMovie = result.getJSONArray("genre_ids");
                        List<Genre> genreMovieList = new ArrayList<>();
                        for (int j = 0; j < genreIdsMovie.length(); j++) {
                            int genreId = genreIdsMovie.getInt(j);

                            Genre genre = new Genre(genreId);
                            genreMovieList.add(genre);
                        }
                        searchResult.setGenreList(genreMovieList);


                        try {
                            searchResult.setReleaseDate(result.getString("release_date"));

                            String date = result.getString("release_date");
                            String year = date.split("-")[0];
                            searchResult.setYear(year);
                        } catch (Exception e){
                            searchResult.setReleaseDate("N/A");
                            e.printStackTrace();
                        }
                        break;

                    case "tv":
                        searchResult.setId(result.getInt("id"));
                        searchResult.setPosterPath(result.getString("poster_path"));
                        searchResult.setTitle(result.getString("name"));
                        searchResult.setMediaType("tv");
                        searchResult.setOverview(result.getString("overview"));
                        searchResult.setVoteAverage(result.getDouble("vote_average"));
                        searchResult.setRegions(result.getString("original_language"));
                        searchResult.setPopularity(result.getDouble("popularity"));

                        JSONArray genreIdsTV = result.getJSONArray("genre_ids");
                        List<Genre> genreTVList = new ArrayList<>();
                        for (int j = 0; j < genreIdsTV.length(); j++) {
                            int genreId = genreIdsTV.getInt(j);

                            Genre genre = new Genre(genreId);
                            genreTVList.add(genre);
                        }
                        searchResult.setGenreList(genreTVList);

                        try {
                            searchResult.setReleaseDate(result.getString("first_air_date"));
                            String date = result.getString("first_air_date");
                            String year = date.split("-")[0];
                            searchResult.setYear(year);
                        } catch (Exception e){
                            searchResult.setReleaseDate("N/A");
                        }
                        break;

                    case "person":
                        searchResult.setId(result.getInt("id"));
                        searchResult.setPosterPath(result.getString("profile_path"));
                        searchResult.setTitle(result.getString("name"));
                        searchResult.setMediaType("person");
                        searchResult.setOverview(null);
                        searchResult.setReleaseDate(null);
                        searchResult.setVoteAverage(0);
                        searchResult.setRegions(null);
                        searchResult.setPopularity(0);
                        break;
                }
                searchResults.add(searchResult);
            }
            searchResponse.setResults(searchResults);

            return searchResponse;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
