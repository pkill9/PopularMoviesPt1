package com.example.bcalabri.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.bcalabri.popularmovies.adapters.MovieListAdapter;
import com.example.bcalabri.popularmovies.adapters.MovieListAdapter.MovieListAdapterOnClickHandler;
import com.example.bcalabri.popularmovies.objects.SimpleMovie;
import com.example.bcalabri.popularmovies.utilities.JSONUtils;
import com.example.bcalabri.popularmovies.utilities.NetworkUtils;
import com.example.bcalabri.popularmovies.utilities.NetworkUtils.QueryType;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieListAdapterOnClickHandler, AdapterCallback {
    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager layoutManager;
    private FetchMoviesTask task;
    private MovieListAdapter adapter;
    private ProgressBar loadingSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.PosterViewer);
        layoutManager = new GridLayoutManager(MainActivity.this, 3);
        adapter = new MovieListAdapter(this, this);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(adapter);
        loadingSpinner = (ProgressBar) findViewById(R.id.progressBar);

        fetchHighestRated();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_highest_rated) {
            fetchHighestRated();
        } else if (id == R.id.action_popular){
            fetchPopular();
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchHighestRated() {
        task = new FetchMoviesTask();
        Log.v(TAG, "Fetching Highest Rated");
        task.execute(QueryType.HIGHEST_RATED);
        getSupportActionBar().setTitle(R.string.highest_rated);
    }

    private void fetchPopular() {
        task = new FetchMoviesTask();
        Log.v(TAG, "Fetching Popular");
        task.execute(QueryType.POPULAR);
        getSupportActionBar().setTitle(R.string.popular);
    }

    @Override
    public void onClick(SimpleMovie movie) {
        Context context = this;
        Log.v(TAG, "Clicked " + movie.getTitle());
        Class destinationClass = MovieDetailActivity.class;

        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT,  movie.getId().toString());

        startActivity(intentToStartDetailActivity);
    }

    @Override
    public void call() {
        mRecyclerView.setVisibility(View.VISIBLE);
        loadingSpinner.setVisibility(View.GONE);
    }

    public class FetchMoviesTask extends AsyncTask<QueryType, Object, List<SimpleMovie>> {
        @Override
        protected void onPreExecute() {
            mRecyclerView.setVisibility(View.INVISIBLE);
            loadingSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<SimpleMovie> doInBackground(QueryType... params) {
            List<SimpleMovie> moviesFromJson = new ArrayList<>();
            try {
                String responseFromHttpUrl = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildQueryUrl(params[0]));
                moviesFromJson = JSONUtils.getMoviesFromResponse(responseFromHttpUrl);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return moviesFromJson;
        }

        @Override
        protected void onPostExecute(List<SimpleMovie> movies) {
            adapter.setMovieList(movies);
            Log.v(TAG, "Set new adapter values");
        }
    }
}
