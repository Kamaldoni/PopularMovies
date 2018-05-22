package movies.popular.android.com.popularmovies.Util;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import movies.popular.android.com.popularmovies.Modul.Movie;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ParsingJsonUtils {

    private static final String CONTENT = "content";
    private static final String AUTHOR = "author";
    private static final String RESULTS = "results";
    private static final String RUNTIME = "runtime";
    private static final String KEY = "key";



    //gets video keys from given url and movie id, returns list of Strings
    public static List<String> getVideosFromHttp(URL url){

        List<String> videoKeys = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, "{}");
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = null;
        JSONObject json = null;
        JSONArray videos = null;

        try {
            response = client.newCall(request).execute();
            json = new JSONObject(response.body().string());
            videos = json.getJSONArray(RESULTS);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        for (int i = 0; i < videos.length(); i++) {

            JSONObject videoJson = null;
            try {
                videoJson = videos.getJSONObject(i);
                videoKeys.add(videoJson.getString(KEY));
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }

        return videoKeys;
    }

    //gets movie duration from the movie details with given url, returns the String of duration
    //it is making the app really slow,just doing background task for only one data
    public static String getDurationFromApi(URL url){

        String minuts = "min";

        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, "{}");
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        Response response = null;
        JSONObject json = null;
        int duration = 0;

        try {
            response = client.newCall(request).execute();
            json = new JSONObject(response.body().string());
            duration = json.getInt(RUNTIME);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String durationString = Integer.toString(duration);

        return durationString.concat(minuts);
    }

    //gets reviews from url and returns the string which will be set to textView
    //I thought in this way I can make the movieDetailsActivity less complex
    public static String getReviewsFromHttp(URL url){

        String reviews = "";

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
            movies = json.getJSONArray(RESULTS);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }


        for (int i = 0; i < movies.length(); i++) {

            JSONObject reviewJson = null;
            try {
                reviewJson = movies.getJSONObject(i);
                reviews = reviews.concat(reviewJson.getString(AUTHOR) + "\n");
                reviews = reviews.concat(reviewJson.getString(CONTENT) + "\n\n");

            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return reviews;
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


        if(movies!= null){

            for (int i = 0; i < movies.length(); i++) {

                Movie movie = new Movie();
                JSONObject movieJson = null;
                try {
                    movieJson = movies.getJSONObject(i);
                    movie.setPoster_path(movieJson.getString(Utils.MOVIE_POSTER));
                    movie.setTitle(movieJson.getString(Utils.TITLE));
                    movie.setOverview(movieJson.getString(Utils.OVERVIEW));
                    movie.setRelease_date(movieJson.getString(Utils.RELEASE_DATE));
                    movie.setId(movieJson.getInt(Utils.ID));
                    movie.setVote_average(movieJson.getDouble(Utils.VOTE_AVER));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                movieList.add(movie);

            }


        }

    }


}
