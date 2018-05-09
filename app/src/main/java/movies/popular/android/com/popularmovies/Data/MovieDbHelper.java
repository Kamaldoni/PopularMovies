package movies.popular.android.com.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_AVERAGE_VOTE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_ID;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_OVERVIEW;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_POSTER;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_RELEASE_DATE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_TIMESTAMP;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_TITLE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.TABLE_NAME;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movieDb";

    private static int DATABASE_VERSION  = 2 ;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT NOT NULL, " +
                COLUMN_POSTER + " TEXT NOT NULL, " +
                COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                COLUMN_AVERAGE_VOTE + " REAL NOT NULL, " +
                COLUMN_RELEASE_DATE + " TEXT NOT NULL," +
                COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIME" +");" ;

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {



        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME );

        onCreate(db);

    }

}
