package movies.popular.android.com.popularmovies.Util;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import movies.popular.android.com.popularmovies.Modul.Movie;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static movies.popular.android.com.popularmovies.MainActivity.current_page;
import static movies.popular.android.com.popularmovies.Util.Utils.API_KEY;

public class NetworkUtils {

    final static String AUTHORITY = "api.themoviedb.org";

    final static String LANGUAGE  = "language";

    final static String PAGE  = "page";

    final static String API = "api_key";

    final static String VIDEOS = "videos";




    public static URL buildMoviesUrl(String sortType) throws MalformedURLException {

        //"https://api.themoviedb.org/3/movie/" + sortType + "?page=" + Integer.toString(current_page) +
        //                    "&language=en-US&api_key=" + API_KEY;

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http")
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sortType)
                .appendQueryParameter(LANGUAGE, "en_US")
                .appendQueryParameter(PAGE, Integer.toString(current_page))
                .appendQueryParameter(API , API_KEY);

        URL url = new URL(builder.build().toString());

        return url;
    }

    public static URL buildReviewUrl(int movieId) throws MalformedURLException {

        //https://api.themoviedb.org/3/movie
        // /284054/reviews?api_key=5633aeac74744826548fa39314adfc5e&language=en-US&page=1
        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http")
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(Integer.toString(movieId))
                .appendPath("reviews")
                .appendQueryParameter(LANGUAGE, "en_US")
                .appendQueryParameter(API , API_KEY)
                .appendQueryParameter("page", "1");

        URL url  = new URL(builder.build().toString());

        return url;
    }

    public static URL buildMovieDetailUrl (int id) throws MalformedURLException {


        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http")
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(Integer.toString(id))
                .appendQueryParameter(LANGUAGE, "en_US")
                .appendQueryParameter(API , API_KEY)
                .appendQueryParameter("page", "1");

        URL url  = new URL(builder.build().toString());

        return url;
    }

    public static URL buildVideosUrl (int id) throws MalformedURLException {
        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http")
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(Integer.toString(id))
                .appendPath(VIDEOS)
                .appendQueryParameter(LANGUAGE, "en_US")
                .appendQueryParameter(API , API_KEY);

        URL url  = new URL(builder.build().toString());

        return url;

    }

}