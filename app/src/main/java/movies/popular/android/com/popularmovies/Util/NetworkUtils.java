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

    public static URL buildUrl(String sortType) throws MalformedURLException {

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


    //gets json from API using given URL and fetches the data to the provided List,

    public static void getResponseFromHttpUrl(List<Movie> movieList, URL url ){

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
                movie.setOriginal_title(movieJson.getString(Utils.ORIGINAL_TITLE));
                movie.setOverview(movieJson.getString(Utils.OVERVIEW));
                movie.setRelease_date(movieJson.getString(Utils.RELEASE_DATE));
                movie.setVote_average(movieJson.getDouble(Utils.VOTE_AVER));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            movieList.add(movie);

        }

    }


}