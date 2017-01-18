package com.example.bcalabri.popularmovies.utilities;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.example.bcalabri.popularmovies.BuildConfig.MOVIE_DB_API;

public final class NetworkUtils {

    final static String API_PARAM = "api_key";
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String MOVIE_DB_URL = "https://api.themoviedb.org/3";
    private static final String MOVIE_DB_IMG_URL = "https://image.tmdb.org/t/p";

    /**
     * Builds the URL
     *
     * @param queryType The location that will be queried for.
     * @return The URL to use to query the movieDB server.
     */
    public static URL buildQueryUrl(QueryType queryType) {
        Uri builtUri = Uri.parse(MOVIE_DB_URL + queryType.getValue()).buildUpon()
                .appendQueryParameter(API_PARAM, MOVIE_DB_API)
                .build();

        return buildUrl(builtUri);
    }

    /**
     * Builds the URL
     *
     * @param size Size of image.
     * @return The URL to use to query the movieDB server.
     */
    public static URL buildImageUrl(String size, String imagePath) {
        Uri builtUri = Uri.parse(MOVIE_DB_IMG_URL + "/" + size + imagePath)
                .buildUpon()
                .build();

        return buildUrl(builtUri);
    }

    /**
     * Builds the URL
     *
     * @param movieID id of movie
     * @return The URL to use to query the movieDB server.
     */
    public static URL buildMovieDetailUrl(String movieID) {
        Uri builtUri = Uri.parse(MOVIE_DB_URL + QueryType.MOVIE.getValue() + movieID).buildUpon()
                .appendQueryParameter(API_PARAM, MOVIE_DB_API)
                .build();

        return buildUrl(builtUri);
    }

    /**
     * Builds the URL
     *
     * @param builtUri The location that will be queried for.
     * @return The URL to use to query the movieDB server.
     */
    public static URL buildUrl(Uri builtUri) {
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public enum QueryType {
        POPULAR("/movie/popular"),
        HIGHEST_RATED("/movie/top_rated"),
        MOVIE("/movie/"),
        NONE("");

        private String value;

        QueryType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum PosterSize {
        w92,
        w154,
        w185,
        w342,
        w500,
        w780,
        original
    }
}