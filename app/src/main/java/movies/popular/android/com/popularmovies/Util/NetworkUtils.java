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

import static movies.popular.android.com.popularmovies.Util.Utils.API_KEY;

public class NetworkUtils {

    final static String AUTHORITY = "api.themoviedb.org";

    final static String LANGUAGE  = "language";

    final static String PAGE  = "page";

    final static String API = "api_key";

    final static String VIDEOS = "videos";

    /*There are only three urls that should be build
    * So I decided to build them separately.
    * These Urls will be used when parsing the json from http(API)*/


    public static URL buildMoviesUrl(String sortType, int page ) throws MalformedURLException {

        Uri.Builder builder = new Uri.Builder();

        builder.scheme("http")
                .authority(AUTHORITY)
                .appendPath("3")
                .appendPath("movie")
                .appendPath(sortType)
                .appendQueryParameter(LANGUAGE, "en_US")
                .appendQueryParameter(PAGE, Integer.toString(page))
                .appendQueryParameter(API , API_KEY);

        URL url = new URL(builder.build().toString());

        return url;
    }

    public static URL buildReviewUrl(int movieId) throws MalformedURLException {

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