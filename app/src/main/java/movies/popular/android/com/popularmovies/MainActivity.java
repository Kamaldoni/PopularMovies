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
    //holds the List of movies to be linked to recyclerViewAdapter
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

    static boolean loading = true;

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




        if(savedInstanceState!=null ){

            ((GridMoviesAdapter)recyclerView.getAdapter()).swapData(pop_movies);

            getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

            mScrollPosition = savedInstanceState.getInt(ITEM_POSITION);
            Log.d("position", Integer.toString(mScrollPosition));

            setRecyclerViewOnScrollListener((GridLayoutManager)recyclerView.getLayoutManager());

        }else{

            if(!internet_connection()){
                showErrorMessage();
            }else{

                setRecyclerViewOnScrollListener((GridLayoutManager)recyclerView.getLayoutManager());




                getFirstPage();

                Log.d("page onCreate", Integer.toString(current_page));
                Log.d("movies onCreate", Integer.toString(pop_movies.size()));
            }
        }


    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ITEM_POSITION,
                ((GridLayoutManager)recyclerView.getLayoutManager()).
                        findFirstVisibleItemPosition());

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState!= null){

            mScrollPosition = savedInstanceState.getInt(ITEM_POSITION);
        }
    }

    //sets onScrollListener to recyclerView given the manager
    private void setRecyclerViewOnScrollListener(final GridLayoutManager manager){


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
                int lastItemPosition = manager.findLastVisibleItemPosition();
                int totalItemCount = manager.getItemCount();

                if (loading){
                    if(dy > 0){
                        if(lastItemPosition + 3 >= totalItemCount ){
                            requestForMovies();

                        }
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

        DisplayMetrics dm = getResources().getDisplayMetrics();
        //gridLayout shows 3 columns in landscape mode
        //and 2 columns in portrait mode

        GridLayoutManager manager = new GridLayoutManager(this, numberOfColumns());


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        List<Movie> movies = new ArrayList<>();

        movies.addAll(pop_movies);

        GridMoviesAdapter adapter = new GridMoviesAdapter( this, movies,this);
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

    // shows data when there is no error
    private void showData()
    {

        errorMessage.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);

    }


    //this method gets the first page from API
    private void getFirstPage() {
        if (internet_connection())
        {
            pop_movies.clear();
            current_page = 1;

            // if loader is
            LoaderManager manager = getSupportLoaderManager();
            Loader<List<Movie>> loader = manager.getLoader(MOVIE_LOADER_ID);
            if(loader == null){
                manager.initLoader(MOVIE_LOADER_ID, null, this);
            }else{
                manager.restartLoader(MOVIE_LOADER_ID, null, this);
            }
        }
        else{
            //shows an error message telling the user there is no internet connection and issuing a chance to reconnect
            showErrorMessage();

        }
    }

    private void requestForMovies() {
        if (internet_connection())
        {
            current_page ++;
            LoaderManager manager = getSupportLoaderManager();
            Loader<List<Movie>> loader = manager.getLoader(MOVIE_LOADER_ID);
            if(loader == null){
                manager.initLoader(MOVIE_LOADER_ID, null, this);

            }else{
                manager.restartLoader(MOVIE_LOADER_ID, null, this);
            }
        }
        else{
            showErrorMessage();
        }


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
            current_page = 1;
            sorting = POPULAR;
            getFirstPage();
            return  true;

        }else if (id == R.id.main_rating)
        {
            sorting = TOP_RATED;
            current_page = 1;
            getFirstPage();
            return  true;
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
                loading = false;
                if(movieList != null)
                    deliverResult(movieList);
                else{
                    loadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }

            }

            @Override
            public void deliverResult(@Nullable List<Movie> data) {
                movieList = data;
                super.deliverResult(data);
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

        if (movieList == null){
            showErrorMessage();
        }else{
            showData();

            pop_movies.addAll(movieList);
            ((GridMoviesAdapter)recyclerView.getAdapter()).swapData(pop_movies);

        }
        if(mScrollPosition != 0){
            recyclerView.scrollToPosition(mScrollPosition);
            Log.d("position", Integer.toString(mScrollPosition));
        }

        loading = true;

        }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {

    }
    // when the movie thumbnail is clicked navigate to MovieDetailsActivity
    @Override
    public void onClickItemListener(int clickedItemIndex) {
        Intent intent = new Intent(MainActivity.this, MovieDetails.class);
        intent.putExtra("movie", pop_movies.get(clickedItemIndex));
        startActivity(intent);
    }
}
