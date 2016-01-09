package com.example.android.popularmoviesv1.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmoviesv1.data.FavouriteMovieContract.FavouriteMovieEntry;
public class FavouriteMovieDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "favourite_movies.db";

    public FavouriteMovieDBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_FAVOURITE_MOVIE_TABLE = "CREATE TABLE " + FavouriteMovieEntry.TABLE_NAME + " (" +
                FavouriteMovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavouriteMovieEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                FavouriteMovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                FavouriteMovieEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                FavouriteMovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                FavouriteMovieEntry.COLUMN_THUMBNAIL + " TEXT NOT NULL, " +
                FavouriteMovieEntry.COLUMN_TITLE + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_FAVOURITE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavouriteMovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
