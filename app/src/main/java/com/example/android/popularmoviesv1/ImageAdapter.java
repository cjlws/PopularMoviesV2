package com.example.android.popularmoviesv1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    private List<Movie> mMovies;
    ViewHolder viewHolder;


    public ImageAdapter(Context c, List<Movie> movies) {
        mContext = c;
        mMovies = movies;
    }


    public int getCount() {
        return mMovies.size();

    }

    @Override
    public Movie getItem(int position) {
        return mMovies.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null) {
            view = layoutInflater.inflate(R.layout.movie_cell, parent, false);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        }

        final Movie movie = getItem(position);
        viewHolder = (ViewHolder) view.getTag();

        String posterUrl = movie.getPosterUrl();

        Picasso.with(mContext).load(posterUrl)
                .placeholder(R.drawable.download_placeholder)
                .error(R.drawable.missing)
                .into(viewHolder.imageView);

        viewHolder.titleView.setText(movie.getTitle());

        return view;
    }

    public void updateValues(List<Movie> newMovies) {
        mMovies = newMovies;
        notifyDataSetChanged();
    }


    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView titleView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.grid_view_cell_imageView);
            titleView = (TextView) view.findViewById(R.id.grid_view_cell_textView);
        }
    }
}
