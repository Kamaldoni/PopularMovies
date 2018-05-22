package movies.popular.android.com.popularmovies;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_POSTER;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_RELEASE_DATE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_TIMESTAMP;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_TITLE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.CONTENT_URI;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.TABLE_NAME;
import static movies.popular.android.com.popularmovies.MainActivity.POPULAR;
import static movies.popular.android.com.popularmovies.MainActivity.TOP_RATED;
/*This activity is created to hold the favorite movies of the user
* It uses SQLite Database and ContentProvider to manage the data
* I decided to create a new activity in order to make MainActivity less complex and readable
* All activities have the same menu, but operate differently since they are different activities
  * Very similar to the MainActivity but this activity can be seen even
  * there is no internet connection
  * It uses the same GridAdapter to manage recyclerView data*/
public class FavouriteMoviesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>, GridMoviesAdapter.GridItemViewListener{

    private RecyclerView recyclerView;
    private List<Movie> movies;
    private GridMoviesAdapter adapter;
    final static String ITEM_POSITION = "position";
    private int mScrollPosition;
    private static final int CURSOR_LOADER_ID = 33;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_movies);

        recyclerView = findViewById(R.id.recyclerViewId);
        adapter = new GridMoviesAdapter(this, new ArrayList<Movie>(), this);

        recyclerView.setAdapter(adapter);
        if(savedInstanceState!= null){

            mScrollPosition = savedInstanceState.getInt(ITEM_POSITION);

        }

        GridLayoutManager manager = new GridLayoutManager(this, numberOfColumns());

        recyclerView.setLayoutManager(manager);
        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null, this);

    }
    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 600;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2; //to keep the grid aspect
        return nColumns;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ITEM_POSITION, ((GridLayoutManager)recyclerView
                .getLayoutManager()).findFirstVisibleItemPosition());
    }

    private List<Movie> fromCursorToList(Cursor cursor){
        List<Movie> movies = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++){

            Movie movie = new Movie();

            movie.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            movie.setPoster_path(cursor.getString(cursor.getColumnIndex(COLUMN_POSTER)));
            movie.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            movie.setOverview(cursor.getString(cursor.getColumnIndex(COLUMN_OVERVIEW)));
            movie.setVote_average(cursor.getDouble(cursor.getColumnIndex(COLUMN_AVERAGE_VOTE)));
            movie.setRelease_date(cursor.getString(cursor.getColumnIndex(COLUMN_RELEASE_DATE)));
            Log.d("poster "  + Integer.toString(i) + ":", movie.getPoster_path()  );
            movies.add(movie);

            cursor.moveToNext();
        }

        return movies;
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor cursor = null;
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }



            @Nullable
            @Override
            public Cursor loadInBackground() {

                cursor = getContentResolver().query(CONTENT_URI,
                        null,
                        null,
                        null,
                        COLUMN_TIMESTAMP);

                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        movies = fromCursorToList(data);

        ((GridMoviesAdapter)recyclerView.getAdapter()).swapData(movies);

        if(mScrollPosition!= 0 ){

            recyclerView.scrollToPosition(mScrollPosition);
            mScrollPosition = 0;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onClickItemListener(int clickedItemIndex) {
        Intent intent = new Intent(FavouriteMoviesActivity.this, MovieDetails.class);
        intent.putExtra("movie", movies.get(clickedItemIndex));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_popular)
        {
            MainActivity.current_page = 1;
            MainActivity.sorting = POPULAR;
            MainActivity.pop_movies.clear();
            Intent intent = new Intent(FavouriteMoviesActivity.this, MainActivity.class);
            startActivity(intent);
            return  true;

        }else if (id == R.id.main_rating)
        {
            MainActivity.current_page = 1;
            MainActivity.sorting = TOP_RATED;
            MainActivity.pop_movies.clear();
            Intent intent = new Intent(FavouriteMoviesActivity.this, MainActivity.class);
            startActivity(intent);
            return  true;
        }else if(id == R.id.fav_menu){

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
