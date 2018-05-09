package movies.popular.android.com.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import movies.popular.android.com.popularmovies.Data.MovieContract;
import movies.popular.android.com.popularmovies.Data.MovieDbHelper;
import movies.popular.android.com.popularmovies.Modul.Movie;
import movies.popular.android.com.popularmovies.Util.GridMoviesAdapter;

import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_AVERAGE_VOTE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_ID;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_OVERVIEW;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_RELEASE_DATE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_TIMESTAMP;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_TITLE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.TABLE_NAME;

public class FavouriteMoviesActivity extends AppCompatActivity {

    private GridView grid;
    private SQLiteDatabase db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_movies);

        grid = findViewById(R.id.gridview_fav);
        grid.setNumColumns(GridView.AUTO_FIT);
        grid.setVisibility(View.VISIBLE);

        MovieDbHelper dbHelper = new MovieDbHelper(this);

        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                COLUMN_TIMESTAMP);

        final List<Movie> movies = fromCursorToList(cursor);

        Log.d("number", Integer.toString(cursor.getCount()));

        grid.setAdapter(new GridMoviesAdapter(getApplicationContext(), movies));

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FavouriteMoviesActivity.this, MovieDetails.class);
                intent.putExtra("movie", movies.get(position));
                startActivity(intent);

            }
        });

    }

    private List<Movie> fromCursorToList(Cursor cursor){
        List<Movie> movies = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++){

            Movie movie = new Movie();

            movie.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            movie.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)));
            movie.setVote_average(cursor.getDouble(cursor.getColumnIndex(COLUMN_AVERAGE_VOTE)));
            movie.setRelease_date(cursor.getString(cursor.getColumnIndex(COLUMN_RELEASE_DATE)));

            movies.add(movie);

            cursor.moveToNext();
        }

        return movies;
    }

}
