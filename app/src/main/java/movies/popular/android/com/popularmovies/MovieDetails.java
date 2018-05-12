package movies.popular.android.com.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import movies.popular.android.com.popularmovies.Data.MovieContract;
import movies.popular.android.com.popularmovies.Data.MovieDbHelper;
import movies.popular.android.com.popularmovies.Modul.Movie;
import movies.popular.android.com.popularmovies.Util.Utils;

import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_AVERAGE_VOTE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_ID;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_OVERVIEW;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_POSTER;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_RELEASE_DATE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.COLUMN_TITLE;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.CONTENT_URI;
import static movies.popular.android.com.popularmovies.Data.MovieContract.MovieDbEntry.TABLE_NAME;


// an activity which is executed when a poster is selected, and details are shown

public class MovieDetails extends AppCompatActivity {
    private TextView overview;
    private TextView release_date;
    private TextView aver_vote;
    private ImageView poster;
    private TextView originalTitle;
    private Button movieModeButton;
    private SQLiteDatabase db;
    private Movie movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);
        movieModeButton = findViewById(R.id.movie_mode_button);



        ActionBar act = getSupportActionBar();
        if(act!= null){
            act.setDisplayHomeAsUpEnabled(true);
        }




        //initializing views in movie details
        overview = (TextView) findViewById(R.id.overview);
        release_date = (TextView) findViewById(R.id.releaseDate);
        aver_vote = (TextView) findViewById(R.id.averageRating);
        originalTitle = (TextView) findViewById(R.id.original_title);

        // initialize movieHelper and get database to insert and delete data when starButton clicked
        MovieDbHelper dbHelper = new MovieDbHelper(this);
        db = dbHelper.getWritableDatabase();

        //get movie that was passed in MainActivity and passing data into MovieDetails
        movie =  getIntent().getParcelableExtra("movie");
        String pstr = movie.getPoster_path();
        String releaseDate = "Release date: " + movie.getRelease_date();
        String rating = String.valueOf(movie.getVote_average()) + "/10";



        overview.setText(movie.getOverview());
        release_date.setText(releaseDate);
        aver_vote.setText(rating);
        originalTitle.setText(movie.getTitle());
        handlingImages(pstr);


        // sharedPreferences will be used to save the state of button which will add/delete movie from Favorites
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
        poster = findViewById(R.id.poster);
        final String url1 = Utils.BASE_URL + "w342/" + posterString;
        Picasso.get().load(url1).into(poster);
    }

    private long insertMovie(){

        Movie movie = getIntent().getParcelableExtra("movie");
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ID, movie.getId());
        cv.put(COLUMN_TITLE, movie.getTitle());
        cv.put(COLUMN_POSTER, movie.getPoster_path());
        cv.put(COLUMN_OVERVIEW, movie.getOverview());
        cv.put(COLUMN_AVERAGE_VOTE, movie.getVote_average());
        cv.put(COLUMN_RELEASE_DATE, movie.getRelease_date());

        return db.insert(TABLE_NAME, null,  cv);
    }
    private boolean deleteMovie(){
        Movie movie = getIntent().getParcelableExtra("movie");

        return db.delete(TABLE_NAME, COLUMN_ID + "=" + Integer.toString(movie.getId()), null )  > 0;

    }

    public void addToFavouriteMovies(View view) {

        if(getContentResolver().query(CONTENT_URI, null, COLUMN_ID + "=?",
                new String[]{String.valueOf(movie.getId())},
                null,
                null).getCount() == 0)
        {
            view.setBackground(getResources().getDrawable(android.R.drawable.btn_star_big_on));

            //Log.d("added", String.valueOf(insertMovie()));

            //insertMovie();


            ContentValues cv = new ContentValues();
            cv.put(COLUMN_ID, movie.getId());
            cv.put(COLUMN_TITLE, movie.getTitle());
            cv.put(COLUMN_OVERVIEW, movie.getOverview());
            cv.put(COLUMN_POSTER, movie.getPoster_path());
            cv.put(COLUMN_RELEASE_DATE, movie.getRelease_date());
            cv.put(COLUMN_AVERAGE_VOTE, movie.getVote_average());

            getContentResolver().insert(CONTENT_URI, cv);


        }else{

            view.setBackground(getResources().getDrawable(android.R.drawable.btn_star_big_off));
            Log.d("deleted", String.valueOf(deleteMovie()));

            //deleteMovie();
            Uri uri = MovieContract.MovieDbEntry.CONTENT_URI.buildUpon().
                    appendPath(String.valueOf(movie.getId())).build();

            getContentResolver().delete(uri, null, null);

            movie.changeMovieMode();
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            onBackPressed();
            }

        return super.onOptionsItemSelected(item);

    }

}