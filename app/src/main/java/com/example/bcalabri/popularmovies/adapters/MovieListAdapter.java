package com.example.bcalabri.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bcalabri.popularmovies.AdapterCallback;
import com.example.bcalabri.popularmovies.R;
import com.example.bcalabri.popularmovies.objects.SimpleMovie;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListAdapterViewHolder> {
    @SuppressWarnings("unused")
    private static final String TAG = MovieListAdapter.class.getSimpleName();
    private final MovieListAdapterOnClickHandler movieClickHandler;
    private final AdapterCallback adapterCallback;
    private List<SimpleMovie> movieList;

    public MovieListAdapter(MovieListAdapterOnClickHandler clickHandler, AdapterCallback callback) {
        movieClickHandler = clickHandler;
        this.adapterCallback = callback;
    }

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new ForecastAdapterViewHolder that holds the View for each list item
     */
    @Override
    public MovieListAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.movie_grid_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new MovieListAdapterViewHolder(view);
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder.
     *
     * @param holder   The ViewHolder which should be updated to represent the
     *                 contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieListAdapterViewHolder holder, int position) {
        SimpleMovie movie = movieList.get(position);
        Log.v(TAG, "Setting SimpleMovie:" + movie.getTitle());
        Log.v(TAG, "Binding Position:" + position + "out of " + (this.getItemCount() - 1) );

        if (position != this.getItemCount() - 1) {
            holder.setValue(movie);
        } else {
            Log.v(TAG, "last item");
            holder.setValueLast(movie);
        }
    }

    @Override
    public int getItemCount() {
        if (movieList == null) {
            return 0;
        } else {
            return movieList.size();
        }
    }

    public void setMovieList(List<SimpleMovie> movies) {
        movieList = movies;
        notifyDataSetChanged();
    }

    public interface MovieListAdapterOnClickHandler {
        void onClick(SimpleMovie movie);
    }

    // HOLDER \\
    public class MovieListAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView image;
        public final TextView text;

        public MovieListAdapterViewHolder(View view) {
            super(view);
            image = (ImageView) view.findViewById(R.id.poster_img);
            text = (TextView) view.findViewById(R.id.movie_title);
            Log.v(TAG, "Set listener");
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.v(TAG, "Clicked");
            int adapterPosition = getAdapterPosition();
            Log.v(TAG, "Clicked:" + adapterPosition);
            movieClickHandler.onClick(movieList.get(adapterPosition));
        }

        public void setValue(SimpleMovie movie) {
            text.setText(movie.getTitle());
            Picasso.with(itemView.getContext())
                    .load(movie.getPosterPath())
                    .into(image);
        }

        public void setValueLast(SimpleMovie movie) {
            text.setText(movie.getTitle());
            Picasso.with(itemView.getContext())
                    .load(movie.getPosterPath())
                    .into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            adapterCallback.call();
                        }

                        @Override
                        public void onError() {
                            adapterCallback.call();
                        }
                    });
        }
    }
}
