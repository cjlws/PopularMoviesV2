package com.example.android.popularmoviesv1;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;

import com.example.android.popularmoviesv1.data.FavouriteMovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieDetailFragment extends Fragment {

    private Movie mMovie;
    private static final String TAG = "MovieDetailsFragment";
    TextView reviewsTitle;
    TextView trailersTitle;
    LinearLayout trailersHolderLinearLayout;
    LinearLayout reviewsHolderLinearLayout;
    Button favouritesButton;
    static final String DETAIL_URI = "URI";
    View view;
    private Trailer mTrailerToShare;
    private ShareActionProvider mShareActionProvider;

    public MovieDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mMovie != null) {
            inflater.inflate(R.menu.menu_movie_detail, menu);

            MenuItem action_share = menu.findItem(R.id.action_share);
            mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(action_share);
            if (mTrailerToShare != null) {
                mShareActionProvider.setShareIntent(shareMenuTrailerIntent());
            }
        }
    }

    private Intent shareMenuTrailerIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getString(R.string.share_stub) + " " + mMovie.getTitle() +
                        " " + getString(R.string.share_trailer) + " " +
                        mTrailerToShare.getName() +
                        " " + getString(R.string.share_url) +
                        mTrailerToShare.getKey());
        return shareIntent;
    }

    private void populateDetails(View view, Movie movie) {
        ((TextView) view.findViewById(R.id.movie_detail_title)).setText(movie.getTitle());
        ((TextView) view.findViewById(R.id.movie_detail_year)).setText(movie.getReleaseYearAndMonth());
        ((TextView) view.findViewById(R.id.movie_detail_user_rating)).setText(formatMovieScore(movie.getUserRating()));
        ((TextView) view.findViewById(R.id.movie_detail_synopsis)).setText(movie.getSynopsis());
        ImageView poster = (ImageView) view.findViewById(R.id.movie_detail_poster);
        Picasso.with(getActivity())
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.download_placeholder)
                .error(R.drawable.missing)
                .into(poster);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mMovie = arguments.getParcelable(DETAIL_URI);
        } else {
            Log.d("MDF", "No arguments bundle available");
        }


        if (mMovie != null) {
            view = inflater.inflate(R.layout.fragment_movie_detail, container, false);

            reviewsTitle = (TextView) view.findViewById(R.id.reviews_title_bar_textView);
            trailersTitle = (TextView) view.findViewById(R.id.trailers_title_bar_textView);
            reviewsHolderLinearLayout = (LinearLayout) view.findViewById(R.id.reviews_holder_linear_layout);
            trailersHolderLinearLayout = (LinearLayout) view.findViewById(R.id.tailers_holder_linear_layout);
            favouritesButton = (Button) view.findViewById(R.id.favouritesButton);

            populateDetails(view, mMovie);
            gatherTrailers(Integer.toString(mMovie.getId()));
            gatherReviews(Integer.toString(mMovie.getId()));
            setFavouritesButton(mMovie.getId());
        } else {
            view = inflater.inflate(R.layout.empty_movie_container, container, false);
        }

        return view;
    }

    private void setFavouritesButton(int checkMovieId) {
        if (isMovieAFavourite(checkMovieId)) {
            favouritesButton.setText(getString(R.string.remove_from_favourite_button_text));
            favouritesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeMovieFromFavourites(mMovie.getId());
                }
            });
        } else {
            favouritesButton.setText(getString(R.string.add_favourite_button_text));
            favouritesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addMovieToFavourites(mMovie.getId());
                }
            });
        }
    }

    private void gatherReviews(String id) {
        new DownloadMovieReviews().execute(id);
    }

    private void gatherTrailers(String id) {
        new DownloadMovieTrailers().execute(id);
    }


    private void populateReviews(List<Review> reviews) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        for (Review review : reviews) {
            View reviewView = inflater.inflate(R.layout.movie_review, reviewsHolderLinearLayout, false);

            TextView authorTextView = (TextView) reviewView.findViewById(R.id.review_author_textview);
            TextView contentTextView = (TextView) reviewView.findViewById(R.id.review_content_textview);

            authorTextView.setText(review.getAuthor());
            contentTextView.setText(review.getContent());

            reviewsHolderLinearLayout.addView(reviewView);
        }

        if (reviews.isEmpty()) {
            reviewsTitle.setVisibility(View.INVISIBLE);
        } else {
            int numberOfReviews = reviews.size();
            if (numberOfReviews > 1) {
                reviewsTitle.append(" (" + String.valueOf(numberOfReviews) + ")");
            }
            reviewsTitle.setVisibility(View.VISIBLE);
        }
    }

    private void populateTrailers(List<Trailer> trailers) {
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        for (final Trailer trailer : trailers) {
            View trailerView = inflater.inflate(R.layout.movie_trailer, trailersHolderLinearLayout, false);
            ((TextView) trailerView.findViewById(R.id.movie_trailer_title)).setText(trailer.getName());

            trailerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lauchTrailerIntent(trailer.getKey());
                }
            });

            trailersHolderLinearLayout.addView(trailerView);
        }
        if (trailers.isEmpty()) {
            Log.d(TAG, "No Trailers to Display");
            trailersTitle.setVisibility(View.INVISIBLE);
        } else {
            mTrailerToShare = trailers.get(0);
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(shareMenuTrailerIntent());
            }
            int numberOfTrailers = trailers.size();
            if (numberOfTrailers > 1) {
                trailersTitle.append(" (" + String.valueOf(numberOfTrailers) + ")");
            }
            trailersTitle.setVisibility(View.VISIBLE);
        }
    }

    private void lauchTrailerIntent(String youtubeKey) {
        String youtubeBaseUrl = "http://www.youtube.com/watch?v=";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(youtubeBaseUrl + youtubeKey));
        startActivity(intent);

    }

    public class DownloadMovieTrailers extends AsyncTask<String, Void, List<Trailer>> {

        private List<Trailer> getTrailerDataFromJSON(String trailerJSONStr)
                throws JSONException {

            final String RESULTS_kEY = "results";

            JSONObject trailersJSON = new JSONObject(trailerJSONStr);
            JSONArray trailersResults = trailersJSON.getJSONArray(RESULTS_kEY);

            List<Trailer> trailers = new ArrayList<>();

            for (int i = 0; i < trailersResults.length(); i++) {
                JSONObject trailer = trailersResults.getJSONObject(i);

                if (trailer.getString("site").contentEquals("YouTube")) {
                    Trailer trailerObject = new Trailer(trailer);
                    trailers.add(trailerObject);
                }
            }
            return trailers;
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {
            super.onPostExecute(trailers);

            if (trailers != null) {
                populateTrailers(trailers);
            }
        }

        @Override
        protected List<Trailer> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String trailersJSONStr = null;

            try {
                String baseUrl = getString(R.string.trailers_url_pre) + params[0] + getString(R.string.trailers_url_post);
                String apiKey = getString(R.string.api_key);

                Uri uri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter("api_key", apiKey).build();

                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                trailersJSONStr = buffer.toString();

            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getTrailerDataFromJSON(trailersJSONStr);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadMovieReviews extends AsyncTask<String, Void, List<Review>> {


        private List<Review> getReviewDataaFromJSON(String reviewJSONStr)
                throws JSONException {

            final String RESULTS_kEY = "results";

            JSONObject reviewsJSON = new JSONObject(reviewJSONStr);
            JSONArray reviewsResults = reviewsJSON.getJSONArray(RESULTS_kEY);

            List<Review> reviews = new ArrayList<>();

            for (int i = 0; i < reviewsResults.length(); i++) {
                JSONObject review = reviewsResults.getJSONObject(i);
                reviews.add(new Review(review));
            }
            return reviews;
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            super.onPostExecute(reviews);
            if (reviews != null) {
                populateReviews(reviews);
            }
        }

        @Override
        protected List<Review> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String reviewJSONStr = null;

            try {
                String baseUrl = getString(R.string.reviews_url_pre) + params[0] + getString(R.string.reviews_url_post);
                String apiKey = getString(R.string.api_key);

                Uri uri = Uri.parse(baseUrl).buildUpon()
                        .appendQueryParameter("api_key", apiKey).build();

                URL url = new URL(uri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }
                reviewJSONStr = buffer.toString();

            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getReviewDataaFromJSON(reviewJSONStr);
            } catch (JSONException e) {
                Log.e(TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }
    }

    private boolean isMovieAFavourite(int idToCheck) {

        Cursor cursor = getActivity().getContentResolver().query(
                FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI,
                null,
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{Integer.toString(idToCheck)},
                null
        );

        int numRows;
        if (cursor == null) {
            numRows = 0;
        } else {
            numRows = cursor.getCount();
        }
        cursor.close();

        if (numRows > 0) {
            Log.i(TAG, "Movie is already a favourite");
            return true;
        } else {
            Log.d(TAG, "Movie is not yet a favourite");
            return false;
        }
    }

    private void addMovieToFavourites(int movieId) {

        ContentValues contentValues = new ContentValues();

        contentValues.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_DATE, mMovie.getReleaseDate());
        contentValues.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID, mMovie.getId());
        contentValues.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_RATING, mMovie.getUserRating());
        contentValues.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_SYNOPSIS, mMovie.getSynopsis());
        contentValues.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_THUMBNAIL, mMovie.getPosterUrl());
        contentValues.put(FavouriteMovieContract.FavouriteMovieEntry.COLUMN_TITLE, mMovie.getTitle());

        Uri uri = getActivity().getContentResolver().insert(FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI,
                contentValues);

        setFavouritesButton(mMovie.getId());
    }

    private void removeMovieFromFavourites(int movieIdToDelete) {

        Log.d(TAG, "Request to remove movie " + movieIdToDelete + " from favourites");

        int numRowsDeleted = getActivity().getContentResolver().delete(
                FavouriteMovieContract.FavouriteMovieEntry.CONTENT_URI,
                FavouriteMovieContract.FavouriteMovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{Integer.toString(movieIdToDelete)}
        );

        Log.d(TAG, "Total of " + numRowsDeleted + "deleted");
        setFavouritesButton(mMovie.getId());

    }

    private String formatMovieScore(double userRating) {
        Log.d("FM", "Original score was " + String.valueOf(userRating));
        double remainder = userRating % 1;
        if (remainder >= 0.1) {
            return String.format("%.1f", userRating) + "/10";
        }
        return String.format("%.0f", userRating) + "/10";
    }
}
