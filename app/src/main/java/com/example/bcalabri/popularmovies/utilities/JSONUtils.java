package com.example.bcalabri.popularmovies.utilities;

/**
 * Created by bcalabri on 12/26/16.
 */

import android.util.Log;

import com.example.bcalabri.popularmovies.objects.DetailedMovie;
import com.example.bcalabri.popularmovies.objects.SimpleMovie;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class JSONUtils {
    private static final String TAG = JSONUtils.class.getSimpleName();
    private static final String ERROR_MESSAGE = "status_message";
    private static final String ERROR_MESSAGE_CODE = "status_code";
    private static final String OWM_LIST = "results";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static List<SimpleMovie> getMoviesFromResponse(String urlResponse)
            throws JSONException, IOException {

        List<SimpleMovie> parsedMovies = new ArrayList<>();

        JSONObject jsonObject = new JSONObject(urlResponse);

        /* Is there an error? */
        if (jsonObject.has(ERROR_MESSAGE)) {
            Log.e(TAG, jsonObject.getString(ERROR_MESSAGE));
        }

        JSONArray movieResults = jsonObject.getJSONArray(OWM_LIST);

        for (int i = 0; i < movieResults.length(); i++) {
            /* Get the JSON object representing the day */
            JSONObject movieResult = movieResults.getJSONObject(i);

            SimpleMovie movie;
            movie = MAPPER.readValue(movieResult.toString(), SimpleMovie.class);
            movie.setPosterPath(NetworkUtils.buildImageUrl(NetworkUtils.PosterSize.w500.name(),movie.getPosterPath()).toString());

            Log.v(TAG, movie.getTitle());
            parsedMovies.add(movie);
        }

        return parsedMovies;
    }

    public static DetailedMovie getMovieDetailFromResponse(String urlResponse) throws JSONException {
        JSONObject resultObject = new JSONObject(urlResponse);

        /* Is there an error? */
        if (resultObject.has(ERROR_MESSAGE)) {
            try {
                Log.e(TAG, resultObject.getString(ERROR_MESSAGE));
            } catch (JSONException e) {
                Log.e(TAG, "Can't fetch JSON error message");
            }
        }

        DetailedMovie movie = new DetailedMovie();
        try {
            movie = MAPPER.readValue(resultObject.toString(), DetailedMovie.class);
            Log.v(TAG, movie.getTitle());
        } catch (IOException e) {
            Log.e(TAG, "Failed to map movie: ");
        }

        return movie;
    }
}
