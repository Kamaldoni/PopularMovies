package movies.popular.android.com.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import movies.popular.android.com.popularmovies.Data.MovieContract;
import movies.popular.android.com.popularmovies.Modul.Movie;
import movies.popular.android.com.popularmovies.Util.NetworkUtils;
import movies.popular.android.com.popularmovies.Util.ParsingJsonUtils;
import movies.popular.android.com.popularmovies.Util.TrailersRecyclerViewAdapter;
import movies.popular.android.com.popularmovies.Util.Utils;

import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_AVERAGE_VOTE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_ID;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_OVERVIEW;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_POSTER;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_RELEASE_DATE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_TITLE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.CONTENT_URI;
import static movies.popular.android.com.popularmovies.MainActivity.POPULAR;
import static movies.popular.android.com.popularmovies.MainActivity.TOP_RATED;


// an activity which is executed when a poster is selected, and details are shown

public class MovieDetails extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<List<String>>, TrailersRecyclerViewAdapter.onButtonClickListener{

    private TextView overview;
    private TextView release_date;
    private TextView aver_vote;
    private ImageView poster;
    private TextView originalTitle;
    private Button movieModeButton;
    private TextView reviews;
    private TextView movieDuration;

    private Movie movie;
    private static final int REVIEW_LOADER_ID = 44;
    private YouTubePlayer youTubePlayer;
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private List<String> videoKeys = new ArrayList<>();
    private RecyclerView trailers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        ActionBar act = getSupportActionBar();
        if(act!= null){
            act.setDisplayHomeAsUpEnabled(true);
        }

        initializeViews();

        bindViews();

        checkTheStarButton();

        initializeYoutubePlayer();

        if(internet_connection()) {

            getSupportLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);

        }

    }

    boolean internet_connection(){
        //Check if connected to internet, output accordingly
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }

    private void initializeViews(){
        //initializing views in movie details
        overview = (TextView) findViewById(R.id.overview);
        release_date = (TextView) findViewById(R.id.releaseDate);
        aver_vote = (TextView) findViewById(R.id.averageRating);
        originalTitle = (TextView) findViewById(R.id.original_title);
        reviews = (TextView) findViewById(R.id.reviewsId);
        movieModeButton = (Button) findViewById(R.id.movie_mode_button);
        poster = (ImageView) findViewById(R.id.poster);
        movieDuration = (TextView)findViewById(R.id.durationId);
        trailers = (RecyclerView) findViewById(R.id.trailers_recyclerViewId);
    }

    //binds views to the appropriate data
    private void bindViews(){

        //get movie that was passed in MainActivity and passing data into MovieDetails
        movie =  getIntent().getParcelableExtra("movie");
        String pstr = movie.getPoster_path();
        String releaseDate = movie.getRelease_date();
        String rating = String.valueOf(movie.getVote_average()) + "/10";

        overview.setText(movie.getOverview());
        release_date.setText(releaseDate);
        aver_vote.setText(rating);
        originalTitle.setText(movie.getTitle());
        reviews.setText("Reviews\n\n");

        handlingImages(pstr);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        trailers.setLayoutManager(manager);
        trailers.addItemDecoration(new
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    // modifies the star button according to current status in database
    private void checkTheStarButton(){
        if( getContentResolver().query(CONTENT_URI, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(movie.getId())},
                null,
                null).getCount() > 0){

            movieModeButton.setBackground(getResources().getDrawable(android.R.drawable.btn_star_big_on));

        }else{
            movieModeButton.setBackground(getResources().getDrawable(android.R.drawable.btn_star_big_off));

        }

    }

    //handles image taken from api by using Picasso
    private void handlingImages(String posterString) {

        final String url1 = Utils.BASE_URL + "w342/" + posterString;
        Picasso.get().load(url1).into(poster);
    }

    // inserts or deletes a movie when the star button is clicked
    public void addToFavouriteMovies(View view) {

        int num = getContentResolver().query(CONTENT_URI, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(movie.getId())},
                null,
                null).getCount() ;
        if( num == 0)
        {
            // if movie is not in db then insert it with all the data it has
            view.setBackground(getResources().getDrawable(android.R.drawable.btn_star_big_on));

            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ID, movie.getId());
            cv.put(COLUMN_TITLE, movie.getTitle());
            cv.put(COLUMN_OVERVIEW, movie.getOverview());
            cv.put(COLUMN_POSTER, movie.getPoster_path());
            cv.put(COLUMN_RELEASE_DATE, movie.getRelease_date());
            cv.put(COLUMN_AVERAGE_VOTE, movie.getVote_average());

            getContentResolver().insert(CONTENT_URI, cv);


        }else{

            //change star background and delete from db

            view.setBackground(getResources().getDrawable(android.R.drawable.btn_star_big_off));

            Uri uri = MovieContract.MovieDbEntry.CONTENT_URI.buildUpon().
                    appendPath(String.valueOf(movie.getId())).build();

            getContentResolver().delete(uri, null, null);

        }


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
            Intent intent = new Intent(MovieDetails.this, MainActivity.class);
            startActivity(intent);
            return  true;

        }else if (id == R.id.main_rating)
        {
            MainActivity.current_page = 1;
            MainActivity.sorting = TOP_RATED;
            MainActivity.pop_movies.clear();
            Intent intent = new Intent(MovieDetails.this, MainActivity.class);
            startActivity(intent);
            return  true;
        }
        else if(id == R.id.fav_menu){

            Intent intent = new Intent(MovieDetails.this, FavouriteMoviesActivity.class);
            startActivity(intent);
            return true;
        }else if(id == android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    // loads the reviews and video keys from API in background
    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        return new android.support.v4.content.AsyncTaskLoader<List<String>>(this) {
            List<String> data = null;
            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }

            @Override
            public List<String> loadInBackground() {
                data = new ArrayList<>();
                URL reviewsUrl = null;
                URL durationUrl = null;

                try {
                    reviewsUrl = NetworkUtils.buildReviewUrl(movie.getId());
                    String res1 = ParsingJsonUtils.getReviewsFromHttp(reviewsUrl);
                    durationUrl = NetworkUtils.buildMovieDetailUrl(movie.getId());
                    String res2 = ParsingJsonUtils.getDurationFromApi(durationUrl);
                    URL videosUrl = NetworkUtils.buildVideosUrl(movie.getId());
                    List<String> res3 = ParsingJsonUtils.getVideosFromHttp(videosUrl);
                    if(res1!=null){
                        data.add(res1);
                    }else
                        data.add("");
                    if(res2!=null){
                        data.add(res2);
                    }else
                        data.add("");

                    if(res3!=null)
                        data.addAll(res3);
                    else
                        data.add("");

                } catch (MalformedURLException e) {
                    e.getStackTrace();
                }
                return data;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<String>> loader, List<String> data) {
        if(data!= null){
            if (data.get(0).equals("")){
                reviews.setText("No Reviews");
            } else{
                reviews.setText(data.get(0));
            }
            movieDuration.setText(data.get(1));
            for (int i = 2; i < data.size(); i++){
                videoKeys.add(data.get(i));
            }
            if(videoKeys != null && videoKeys.size() > 0){
                trailers.setAdapter(
                        new TrailersRecyclerViewAdapter(
                                this, videoKeys.size(), this));
                if(youTubePlayer!=null)
                    youTubePlayer.cueVideo(videoKeys.get(0));
            }


        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<String>> loader) {

    }

    // initializes the youtube player with the default trailer from API
    // some code was taken from stackoverflow
    private void initializeYoutubePlayer() {

        youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.youtubesupportfragment);

        if (youTubePlayerFragment == null)
            return;

        youTubePlayerFragment.initialize(Utils.YOUTUBE_API, new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                                boolean wasRestored) {
                if (!wasRestored) {
                    youTubePlayer = player;

                    //set the player style default
                    youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);

                    //cue the 1st video by default
                    if(videoKeys.size() > 0)
                        youTubePlayer.cueVideo(videoKeys.get(0));
                    //bY73vFGhSVk
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {

                //print or show error if initialization failed
            }
        });
    }
    @Override
    public void clickListener(int pos) {
        youTubePlayer.cueVideo(videoKeys.get(pos));
    }
}