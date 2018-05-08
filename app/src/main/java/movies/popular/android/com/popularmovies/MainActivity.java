package movies.popular.android.com.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
import movies.popular.android.com.popularmovies.Util.Utils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static movies.popular.android.com.popularmovies.Util.Utils.API_KEY;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {
    private static final String EXTRA_MOVIE_QUERY_URL = "query_url";
    private GridView gridView;
    static int current_page = 1;
    public static List<Movie> pop_movies = new ArrayList<>();
    public static List<Movie> high_ranked_movies = new ArrayList<>();
    final static String POPULAR = "popular";
    final static String TOP_RATED = "top_rated";
    public static String sorting = POPULAR;
    private ProgressBar loadingIndicator;
    private TextView errorMessage;

    private static final int MOVIE_LOADER_ID =  22;


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


        gridView = (GridView) findViewById(R.id.gridview);
        //appears when loading first page from API, to let the user that action is on.
        loadingIndicator = (ProgressBar) findViewById(R.id.loadingIndicator);

        Parcelable state = gridView.onSaveInstanceState();
        gridView.setNumColumns(2);

        getSupportLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);

        errorMessage = (TextView) findViewById(R.id.error_message);

        getFirstPage(sorting);

        // I have looked this method from stackoverflow
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen == totalItemCount) {
                    requestForMovies(sorting);
                    //gridView.setSelection(gridView.getLastVisiblePosition());
                }
            }
        });

        gridView.onRestoreInstanceState(state);

    }


    //shows error message when there is no internet connection
    private void showErrorMessage() {

        gridView.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);

    }

    // shows data when there is no error
    private void showData()
    {

        errorMessage.setVisibility(View.INVISIBLE);
        gridView.setVisibility(View.VISIBLE);

    }


    //this method gets the first page from API
    private void getFirstPage(String sort) {
        if (internet_connection())
        {
            high_ranked_movies.clear();
            pop_movies.clear();
            current_page = 1;

            String http = "https://api.themoviedb.org/3/movie/" + sort +
                    "?page=1&language=en-US&api_key=" + API_KEY;

            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_MOVIE_QUERY_URL, http);

            LoaderManager manager = getSupportLoaderManager();
            Loader<List<Movie>> loader = manager.getLoader(MOVIE_LOADER_ID);
            if(loader == null){
                manager.initLoader(MOVIE_LOADER_ID, bundle, this);
            }else{
                manager.restartLoader(MOVIE_LOADER_ID, bundle, this);
            }

                    /*if (sorting == POPULAR)
                        new LoadingFirstPage(pop_movies).execute(url);
                    else if (sorting == TOP_RATED)
                        new LoadingFirstPage(high_ranked_movies).execute(url);
        */


        }
        else{
            //create a snackbar telling the user there is no internet connection and issuing a chance to reconnect
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
            getFirstPage(sorting);
            return  true;

        }else if (id == R.id.main_rating)
        {
            sorting = TOP_RATED;
            current_page = 1;
            getFirstPage(sorting);
            return  true;
        }
        else if (id== R.id.refresh)
        {
            current_page = 1;
            getFirstPage(sorting);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // this method is used to load more posters from API when the gridView scrolls to the end of the current movie list
    private void requestForMovies(String sortType) {
        if (internet_connection())
        {
            current_page += 1;

            String http = "https://api.themoviedb.org/3/movie/" + sortType + "?page=" + Integer.toString(current_page) +
                    "&language=en-US&api_key=" + API_KEY;

            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_MOVIE_QUERY_URL, http);

            LoaderManager manager = getSupportLoaderManager();
            Loader<List<Movie>> loader = manager.getLoader(MOVIE_LOADER_ID);
            if(loader == null){
                manager.initLoader(MOVIE_LOADER_ID, bundle, this);
                //gridView.setSelection(gridView.getCount()-1);
            }else{
                manager.restartLoader(MOVIE_LOADER_ID, bundle, this);
                //gridView.setSelection(gridView.getCount()-1);
            }
        }
        else{
            showErrorMessage();
        }


    }


    @NonNull
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable final Bundle args) {

        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> movieList = null;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null)
                    return;
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

                String stringUrl = args.getString(EXTRA_MOVIE_QUERY_URL);

                if (stringUrl==null || stringUrl.equals(""))
                    return null;

                URL url = null;
                try {
                    url = new URL(stringUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


                OkHttpClient client = new OkHttpClient();

                MediaType mediaType = MediaType.parse("application/octet-stream");
                RequestBody body = RequestBody.create(mediaType, "{}");
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();

                Response response = null;
                JSONObject json = null;
                JSONArray movies = null;

                try {
                    response = client.newCall(request).execute();
                    json = new JSONObject(response.body().string());
                    movies = json.getJSONArray("results");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                for (int i = 0; i < movies.length(); i++) {

                    Movie movie = new Movie();
                    JSONObject movieJson = null;
                    try {
                        movieJson = movies.getJSONObject(i);
                        movie.setPoster_path(movieJson.getString(Utils.MOVIE_POSTER));
                        movie.setBackdrop(movieJson.getString(Utils.BACKGROUND_PATH));
                        movie.setOriginal_title(movieJson.getString(Utils.ORIGINAL_TITLE));
                        movie.setOverview(movieJson.getString(Utils.OVERVIEW));
                        movie.setRelease_date(movieJson.getString(Utils.RELEASE_DATE));
                        movie.setVote_average(movieJson.getDouble(Utils.VOTE_AVER));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    movieList.add(movie);

                }
                return movieList;
            }
        };

    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, final List<Movie> movieList) {

        loadingIndicator.setVisibility(View.INVISIBLE);

        if (movieList == null)
            showErrorMessage();
        else
            showData();

        List<Movie> movies = null;

        if (sorting.equals(POPULAR)){
            movies = pop_movies;
        }else
            movies = high_ranked_movies;

        movies.addAll(movieList);



        gridView.setAdapter(new GridMoviesAdapter(getApplicationContext(), movies ));


        final List<Movie> finalMovies = movies;
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, MovieDetails.class);
                intent.putExtra(Utils.VOTE_AVER, finalMovies.get(position).getVote_average());
                intent.putExtra(Utils.ORIGINAL_TITLE, finalMovies.get(position).getOriginal_title());
                intent.putExtra(Utils.RELEASE_DATE, finalMovies.get(position).getRelease_date());
                intent.putExtra(Utils.MOVIE_POSTER, finalMovies.get(position).getPoster_path());
                intent.putExtra(Utils.OVERVIEW, finalMovies.get(position).getOverview());
                intent.putExtra(Utils.BACKGROUND_PATH, finalMovies.get(position).getBackdrop());
                startActivity(intent);

            }
        });
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {

    }

}
