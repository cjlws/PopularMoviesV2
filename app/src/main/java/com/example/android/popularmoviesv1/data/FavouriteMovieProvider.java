package com.example.android.popularmoviesv1.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.net.Uri;

public class FavouriteMovieProvider extends ContentProvider {

    private static final UriMatcher uriMatcher = buildUriMatcher();
    private FavouriteMovieDBHelper favouriteMovieDBHelper;

    static final int MOVIE = 1;

    //  private static final SQLiteQueryBuilder movieQueryBuilder;

//    static {
//        movieQueryBuilder = new SQLiteQueryBuilder();
//    }

    @Override
    public boolean onCreate() {
        favouriteMovieDBHelper = new FavouriteMovieDBHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavouriteMovieContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, FavouriteMovieContract.PATH_MOVIE, MOVIE);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                return FavouriteMovieContract.FavouriteMovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Uri not found: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                cursor = favouriteMovieDBHelper.getReadableDatabase().query(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME,
                        projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Invalid Uri - " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        SQLiteDatabase database = favouriteMovieDBHelper.getWritableDatabase();
        Uri uriToReturn;

        switch (uriMatcher.match(uri)) {
            case MOVIE:
                long _id = database.insert(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME, null, contentValues);
                if (_id > 0) {
                    uriToReturn = FavouriteMovieContract.FavouriteMovieEntry.buildFavouriteMovieUri(_id);
                } else {
                    throw new SQLiteException("Insert Failed with uri " + uri);
                }
                break;

            default:
                throw new UnsupportedOperationException("URI unknown - " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return uriToReturn;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int rowsDeleted;
        SQLiteDatabase database = favouriteMovieDBHelper.getWritableDatabase();
        if (null == selection) selection = "1";
        switch (uriMatcher.match(uri)) {
            case MOVIE:
                rowsDeleted = database.delete(FavouriteMovieContract.FavouriteMovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Uri unknown: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Update is never used
        return 0;
    }

}
