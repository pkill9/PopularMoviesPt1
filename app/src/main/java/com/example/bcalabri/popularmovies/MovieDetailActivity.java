package com.example.bcalabri.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.bcalabri.popularmovies.objects.DetailedMovie;
import com.example.bcalabri.popularmovies.objects.Genre;
import com.example.bcalabri.popularmovies.utilities.NetworkUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;

import static com.example.bcalabri.popularmovies.utilities.JSONUtils.getMovieDetailFromResponse;

public class MovieDetailActivity extends AppCompatActivity {
    private String TAG = MovieDetailActivity.class.getSimpleName();

    private TextView title;
    private TextView overview;
    private TextView homepage;
    private TextView release;
    private TextView genres;
    private TextView budget;
    private TextView language;
    private TextView length;
    private TextView revenue;

    private TextView ratingCount;
    private RatingBar ratingBar;

    private ImageView poster;
    private ImageView backdrop;

    private ProgressBar loadingSpinner;
    private ScrollView fullView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();

        if (intent == null || !intent.hasExtra(Intent.EXTRA_TEXT) ) {
            finish();
        }

        String movieId = intent.getStringExtra(Intent.EXTRA_TEXT);

        initializeElements();
        FetchMovieTask task = new FetchMovieTask();
        task.execute(movieId);
    }

    private void displayMovieDetails(DetailedMovie movie) {
        getSupportActionBar().setTitle(movie.getTitle());
        title.setText(movie.getTitle());
        overview.setText(movie.getOverview());
        homepage.setText(movie.getHomepage());
        budget.setText("$"+ movie.getBudget().toString());
        length.setText(movie.getRuntime().toString());
        revenue.setText("$" + movie.getRevenue().toString());
        language.setText(movie.getOriginalLanguage());
        release.setText(movie.getReleaseDate());

        StringBuilder genreString = new StringBuilder();

        for (Genre genre : movie.getGenres()) {
            if (genreString.length() != 0) {
                genreString.append(",");
            }
            genreString.append(genre.getName());
        }

        genres.setText(genreString.toString());

        ratingCount.setText(String.format(getString(R.string.votes), movie.getVoteCount()));
        ratingBar.setRating(movie.getVoteAverage().floatValue());

        Picasso.with(this)
                .load(NetworkUtils.buildImageUrl(NetworkUtils.PosterSize.original.name(), movie.getPosterPath()).toString())
                .into(poster, new Callback() {
                    @Override
                    public void onSuccess() {
                        fullView.setVisibility(View.VISIBLE);
                        loadingSpinner.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        fullView.setVisibility(View.VISIBLE);
                        loadingSpinner.setVisibility(View.GONE);
                    }
                });

        Picasso.with(this)
                .load(NetworkUtils.buildImageUrl(NetworkUtils.PosterSize.original.name(), movie.getBackdropPath()).toString())
                .into(backdrop);
    }

    private void initializeElements(){
        title = (TextView) findViewById(R.id.value_title);
        overview = (TextView) findViewById(R.id.value_overview);
        homepage = (TextView) findViewById(R.id.value_homepage);
        genres = (TextView) findViewById(R.id.value_genres);
        budget = (TextView) findViewById(R.id.value_budget);
        language = (TextView) findViewById(R.id.value_language);
        length = (TextView) findViewById(R.id.value_length);
        revenue = (TextView) findViewById(R.id.value_revenue);
        release = (TextView) findViewById(R.id.value_release);

        ratingCount = (TextView) findViewById(R.id.value_numRatings);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        poster = (ImageView) findViewById(R.id.value_poster_image);
        backdrop = (ImageView) findViewById(R.id.backdrop);

        loadingSpinner = (ProgressBar) findViewById(R.id.movie_progress_bar);
        fullView = (ScrollView) findViewById(R.id.movie_detail);

    }

    public class FetchMovieTask extends AsyncTask<String, Object, DetailedMovie> {
        @Override
        protected void onPreExecute() {
            Log.v(TAG, "Loading Spinner");
            loadingSpinner.setVisibility(View.VISIBLE);
            fullView.setVisibility(View.INVISIBLE);
        }

        @Override
        protected DetailedMovie doInBackground(String... params) {
            DetailedMovie movie = null;
            try {
                String response = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildMovieDetailUrl(params[0]));
                movie = getMovieDetailFromResponse(response);
            } catch (IOException | JSONException e) {
                Log.e(TAG, "Error fetching from remote server", e);
                finish();
            }
            return movie;
        }

        @Override
        protected void onPostExecute(DetailedMovie movie) {
            displayMovieDetails(movie);
            Log.v(TAG, "Displaying movie" + movie.getTitle());
        }
    }
}
