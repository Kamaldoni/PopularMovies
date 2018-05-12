package movies.popular.android.com.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class MovieContentProvider extends ContentProvider{

    public static UriMatcher matcher = buildUriMatcher();
    private static final int MOVIES = 100;
    private static final int MOVIES_WITH_ID = 101;

    private MovieDbHelper dbHelper;

    public static UriMatcher buildUriMatcher(){

        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES );
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIES_WITH_ID );

        return uriMatcher;
    }

    @Override
    public boolean onCreate() {

         dbHelper = new MovieDbHelper(getContext());

        return true;
    }



    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor rCursor;
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        switch (matcher.match(uri)){

            case MOVIES:
            {
                rCursor = db.query(MovieContract.MovieDbEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException();

        }

        //notifying the change
        rCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return rCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }


    // inserts a single movie when the button is clicked
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Uri retUri;
        switch (matcher.match(uri)){

            case MOVIES:
                long id = db.insert(MovieContract.MovieDbEntry.TABLE_NAME,
                        null,
                        values
                        );
                if(id > 0){
                    retUri = ContentUris.withAppendedId(MovieContract.MovieDbEntry.CONTENT_URI,
                            id);
                }else{
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " +  uri);

        }

        //notifying the change to content resolver
        getContext().getContentResolver().notifyChange(uri, null);

        return retUri;
    }


    // deletes the single movie when the button is clicked
    // returns the number of deleted movies
    //
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int retVal;

        switch (matcher.match(uri)){

            case MOVIES_WITH_ID:
                String id = uri.getPathSegments().get(1);

                retVal = db.delete(MovieContract.MovieDbEntry.TABLE_NAME,
                        MovieContract.MovieDbEntry.COLUMN_ID + "=?",
                        new String[]{id}
                        );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri "  + uri);



        }

        getContext().getContentResolver().notifyChange(uri, null);
        return retVal;
    }


    // this method is not used in this project
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }
}
