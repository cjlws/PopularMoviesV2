package com.example.android.popularmoviesv1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(MovieDetailFragment.DETAIL_URI,
                    getIntent().getParcelableExtra(MovieDetailFragment.DETAIL_URI));
            MovieDetailFragment fragment = new MovieDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_movie_detail_container, fragment)
                    .commit();
        }
    }
}
