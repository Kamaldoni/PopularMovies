package movies.popular.android.com.popularmovies;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import movies.popular.android.com.popularmovies.Modul.Movie;
import movies.popular.android.com.popularmovies.Util.Utils;


// an activity which is executed when a poster is selected, and details are shown

public class MovieDetails extends AppCompatActivity {
    private TextView overview;
    private TextView release_date;
    private TextView aver_vote;
    private ImageView poster;
    private TextView originalTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        overview = (TextView) findViewById(R.id.overview);
        release_date = (TextView) findViewById(R.id.releaseDate);
        aver_vote = (TextView) findViewById(R.id.averageRating);
        originalTitle = (TextView) findViewById(R.id.original_title);

        Movie movie =  getIntent().getParcelableExtra("movie");


        String pstr = movie.getPoster_path();
        String releaseDate = "Release date: " + movie.getRelease_date();
        String rating = "TMDB: " + String.valueOf(movie.getVote_average());


        overview.setText(movie.getOverview());
        release_date.setText(releaseDate);
        aver_vote.setText(rating);
        originalTitle.setText(movie.getOriginal_title());
        handlingImages(pstr);

    }

    private void handlingImages(String posterString) {
        poster = findViewById(R.id.poster);
        final String url1 = Utils.BASE_URL + "w342/" + posterString;
        Picasso.get().load(url1).into(poster);
    }


}