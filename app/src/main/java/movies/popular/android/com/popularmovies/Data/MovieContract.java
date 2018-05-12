package movies.popular.android.com.popularmovies.Data;


import android.net.Uri;

public class MovieContract {

    //Authority
    public static final String AUTHORITY = "movies.popular.android.com.popularmovies";

    //Base Url
    private static final Uri BASE_CONTENT_URL = Uri.parse("content://" + AUTHORITY);

    //path for table
    public static final String PATH_MOVIES = "movie_details";



    public static class MovieDbEntry {

        public static final Uri CONTENT_URI =  BASE_CONTENT_URL.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String TABLE_NAME = "movie_details";

        public static final String COLUMN_ID = "id";

        public static final String COLUMN_TITLE = "title";

        public static final String COLUMN_OVERVIEW = "overview";

        public static final String COLUMN_POSTER = "poster_path";

        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static final String COLUMN_AVERAGE_VOTE =  "average_ranking";

        public static final String COLUMN_TIMESTAMP = "timestamp";

    }


}
