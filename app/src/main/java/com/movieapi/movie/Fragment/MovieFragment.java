package com.movieapi.movie.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.movieapi.movie.R;
import com.movieapi.movie.adapter.MovieBriefSmallAdapter;
import com.movieapi.movie.adapter.MovieCarouselAdapter;
import com.movieapi.movie.network.movie.MovieBrief;
import com.movieapi.movie.network.movie.NowShowingMoviesResponse;
import com.movieapi.movie.network.movie.PopularMoviesResponse;
import com.movieapi.movie.request.ApiClient;
import com.movieapi.movie.request.ApiInterface;
import com.movieapi.movie.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MovieFragment extends Fragment {
    ProgressBar mProgressBar;
    TextView view_popular, view_top_rated;
    NestedScrollView fragment_movie_scrollView;
    LinearLayout popular_heading;

    // carousel
    LinearLayoutManager carouselLayoutManager;
    RecyclerView carousel_recView;
    List<MovieBrief> mNowShowingMovie;
    MovieCarouselAdapter movieCarouselAdapter;

    // popular
    RecyclerView popular_recView;
    List<MovieBrief> mPopularList;
    MovieBriefSmallAdapter mPopularAdapter;

    private boolean mNowShowingMoviesLoaded;
    private boolean mPopularMoviesLoaded;

    Call<NowShowingMoviesResponse> nowShowingMoviesResponseCall;
    Call<PopularMoviesResponse> popularMoviesResponseCall;

    Timer timer;
    TimerTask timerTask;
    int position;

    public MovieFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = view.findViewById(R.id.movie_progressBar);
        view_popular = view.findViewById(R.id.view_popular);
        view_top_rated = view_popular.findViewById(R.id.view_top_rated);

        fragment_movie_scrollView = view.findViewById(R.id.fragment_movie_scrollView);

        popular_heading = view.findViewById(R.id.popular_heading);

        carousel_recView = view.findViewById(R.id.carousel_recView);
        popular_recView = view.findViewById(R.id.popular_recView);

        mNowShowingMovie = new ArrayList<>();
        mPopularList = new ArrayList<>();

        // carousel
        movieCarouselAdapter = new MovieCarouselAdapter(mNowShowingMovie, getContext());
        carouselLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        carousel_recView.setLayoutManager(carouselLayoutManager);
        carousel_recView.setAdapter(movieCarouselAdapter);

        // popular
        mPopularAdapter = new MovieBriefSmallAdapter(mPopularList, getContext());
        popular_recView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popular_recView.setAdapter(mPopularAdapter);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(carousel_recView);
        carousel_recView.smoothScrollBy(5, 0);

        carousel_recView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == 1)
                    stopAutoScrollCarousel();
                else if (newState == 0){
                    position = carouselLayoutManager.findFirstCompletelyVisibleItemPosition();
                    runAutoScrollingCarousel();
                }
            }
        });

        initViews();
    }

    private void stopAutoScrollCarousel(){
        if (timer != null && timerTask != null){
            timerTask.cancel();
            timer.cancel();
            timer = null;
            timerTask = null;
            position = carouselLayoutManager.findFirstCompletelyVisibleItemPosition();
        }
    }

    private void runAutoScrollingCarousel(){
        if(timer == null && timerTask == null){
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (position == mNowShowingMovie.size() - 1){
                        carousel_recView.post(new Runnable() {
                            @Override
                            public void run() {
                                position = 0;
                                carousel_recView.smoothScrollToPosition(position);
                                carousel_recView.smoothScrollBy(5, 0);
                            }
                        });
                    }else {
                        position++;
                        carousel_recView.smoothScrollToPosition(position);
                    }
                }
            };
            timer.schedule(timerTask, 4000, 4000);
        }
    }

    private void initViews(){
        loadNowShowingMovies();
        loadPopularMovie();
    }

    private void loadNowShowingMovies(){
        ApiInterface apiInterface = ApiClient.getMovieApi();
        nowShowingMoviesResponseCall = apiInterface.getNowShowingMovies(Constants.API_KEY, 1, "US");
        nowShowingMoviesResponseCall.enqueue(new Callback<NowShowingMoviesResponse>() {
            @Override
            public void onResponse(Call<NowShowingMoviesResponse> call, Response<NowShowingMoviesResponse> response) {
                if(!response.isSuccessful()){
                    nowShowingMoviesResponseCall = call.clone();
                    nowShowingMoviesResponseCall.enqueue(this);
                    return;
                }

                if(response.body() == null) return;
                if (response.body().getResults() == null) return;

                for (MovieBrief movieBrief : response.body().getResults()){
                    if (movieBrief != null && movieBrief.getBackdropPath() != null)
                        mNowShowingMovie.add(movieBrief);
                }
                movieCarouselAdapter.notifyDataSetChanged();
                mNowShowingMoviesLoaded = true;
                checkAllDataLoad();
             }
            @Override
            public void onFailure(Call<NowShowingMoviesResponse> call, Throwable t) {

            }
        });
    }

    private void loadPopularMovie(){
        ApiInterface apiInterface = ApiClient.getMovieApi();
        popularMoviesResponseCall = apiInterface.getPopularMovies(Constants.API_KEY, 1);
        popularMoviesResponseCall.enqueue(new Callback<PopularMoviesResponse>() {
            @Override
            public void onResponse(Call<PopularMoviesResponse> call, Response<PopularMoviesResponse> response) {
                if(!response.isSuccessful()){
                    popularMoviesResponseCall = call.clone();
                    popularMoviesResponseCall.enqueue(this);
                    return;
                }

                if(response.body() == null) return;
                if (response.body().getResults() == null) return;

                for (MovieBrief movieBrief : response.body().getResults()){
                    if (movieBrief != null && movieBrief.getBackdropPath() != null)
                        mPopularList.add(movieBrief);
                }
                mPopularAdapter.notifyDataSetChanged();
                mPopularMoviesLoaded = true;
                checkAllDataLoad();
            }

            @Override
            public void onFailure(Call<PopularMoviesResponse> call, Throwable t) {

            }
        });
    }

    private void checkAllDataLoad() {
        if(mNowShowingMoviesLoaded && mPopularMoviesLoaded){
            mProgressBar.setVisibility(View.GONE);
            carousel_recView.setVisibility(View.VISIBLE);
            popular_heading.setVisibility(View.VISIBLE);
            popular_recView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopAutoScrollCarousel();
    }

    @Override
    public void onResume() {
        super.onResume();
        runAutoScrollingCarousel();
    }
}
