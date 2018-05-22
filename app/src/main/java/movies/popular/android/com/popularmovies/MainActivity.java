package movies.popular.android.com.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import movies.popular.android.com.popularmovies.Modul.Movie;
import movies.popular.android.com.popularmovies.Util.GridMoviesAdapter;
import movies.popular.android.com.popularmovies.Util.NetworkUtils;
import movies.popular.android.com.popularmovies.Util.ParsingJsonUtils;
import movies.popular.android.com.popularmovies.Util.Utils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static movies.popular.android.com.popularmovies.Util.Utils.API_KEY;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>>,
GridMoviesAdapter.GridItemViewListener{



    //holds current page in movieList
    public static int current_page = 1;


    public static List<Movie> pop_movies = new ArrayList<>();
    //const to be used when the user changes the types of movies
    final static String POPULAR = "popular";
    final static String TOP_RATED = "top_rated";
    //holds the current sorting type; POPULAR by default
    public static String sorting = POPULAR;
    //will be shown while background task is taking place
    private ProgressBar loadingIndicator;
    //error message that is invisible will be shown when there is no internet connection
    private TextView errorMessage;
    private RecyclerView recyclerView;
    private static final int MOVIE_LOADER_ID =  22;

    public static boolean loading = true;
    private GridMoviesAdapter adapter;
    private final String ITEM_POSITION = "position";
    private int mScrollPosition;
    // it checks whether there is an internet connection, now my app doesn't crash when there is no internet connection.
    // it is been taken from https://stackoverflow.com/questions/37232927/app-crashes-when-no-internet-connection-is-available

    boolean internet_connection(){
        //Check if connected to internet, output accordingly
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initializeViews();

        if(internet_connection()){
            if(savedInstanceState != null){
                mScrollPosition  = savedInstanceState.getInt(ITEM_POSITION);
            }


            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null , this);


            setRecyclerViewOnScrollListener();

        }else{
            showErrorMessage();
        }

    }



    private void setRecyclerViewOnScrollListener(){


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            // will not be implemented

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // last item position
                int visibleItemCount = ((GridLayoutManager)recyclerView.
                        getLayoutManager()).getChildCount();
                int firstVisibleItemPosition = ((GridLayoutManager)recyclerView.
                        getLayoutManager()).findFirstVisibleItemPosition();
                int totalItemCount = ((GridLayoutManager)recyclerView.
                        getLayoutManager()).getItemCount();

                Log.d("itemPosition", Integer.toString(visibleItemCount+ firstVisibleItemPosition));


                if (loading && visibleItemCount + firstVisibleItemPosition >= totalItemCount){
                    if(dy > 0){
                        loading = false;
                        current_page++;
                        getSupportLoaderManager()
                                .restartLoader(MOVIE_LOADER_ID, null, MainActivity.this);
                    }

                }
            }
        });

    }
    //initializes all views from this activity

    private void initializeViews() {

        loadingIndicator = findViewById(R.id.loadingIndicator);
        errorMessage = findViewById(R.id.error_message);
        recyclerView = findViewById(R.id.recyclerViewId);

        GridLayoutManager manager = new GridLayoutManager(this, numberOfColumns());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);

        List<Movie> movies = new ArrayList<>();

        adapter = new GridMoviesAdapter( this, movies,this);

        recyclerView.setAdapter(adapter);

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

    //shows error message when there is no internet connection
    private void showErrorMessage() {

        recyclerView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        return true;
    }

    // there are three menus, two for sorting and one for refreshing the activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.main_popular)
        {
            if(sorting.equals(POPULAR)){
                return true;
            }else{
                current_page = 1;
                sorting = POPULAR;
                pop_movies.clear();
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
                return  true;
            }


        }else if (id == R.id.main_rating)
        {
            if(sorting.equals(TOP_RATED)){
                return true;
            }else{
                sorting = TOP_RATED;
                current_page = 1;
                pop_movies.clear();
                getSupportLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
                return  true;
            }

        }else if(id == R.id.fav_menu){

            Intent intent= new Intent(this, FavouriteMoviesActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    // this method is used to load more posters from API when the gridView scrolls to the end of the current movie list



    @NonNull
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable final Bundle args) {

        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> movieList = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();

                if(movieList!=null) {
                    deliverResult(new ArrayList<Movie>());
                }else{
                    loading = false;
                    loadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }

            }

            @Override
            public void deliverResult(@Nullable List<Movie> data) {
                super.deliverResult(data);
                movieList = data;
            }

            @Override
            public List<Movie> loadInBackground() {

                movieList = new ArrayList<>();


                URL url = null;
                try {
                    url = NetworkUtils.buildMoviesUrl(sorting, current_page);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                ParsingJsonUtils.getResponseFromHttpUrl(movieList, url);

                return movieList;
            }
        };

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, final List<Movie> movieList) {

        loadingIndicator.setVisibility(View.INVISIBLE);

        if(movieList != null){


            pop_movies.addAll(movieList);

            adapter.swapData(pop_movies);

            if(mScrollPosition != 0){
                recyclerView.scrollToPosition(mScrollPosition);
                mScrollPosition = 0;
            }
            Log.d("moviess", Integer.toString(pop_movies.size()));

            loading = true;

        }else {
            showErrorMessage();
        }

    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ITEM_POSITION, ((GridLayoutManager)recyclerView
                .getLayoutManager()).findFirstVisibleItemPosition());
    }

    // when the movie thumbnail is clicked navigate to MovieDetailsActivity
    @Override
    public void onClickItemListener(int clickedItemIndex) {
        Intent intent = new Intent(MainActivity.this, MovieDetails.class);
        intent.putExtra("movie", pop_movies.get(clickedItemIndex));
        startActivity(intent);
    }



}
