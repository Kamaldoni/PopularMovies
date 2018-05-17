package movies.popular.android.com.popularmovies.Modul;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lkhnt on 3/23/2018.
 */

//this is a class which is used to parse movie details from json

public class Movie implements Parcelable{

    private int id;
    private String title;

    private String poster_path;
    private String overview;
    private String original_title;
    private double vote_average;
    private String release_date;


    public Movie(){
        // just to be able to initialize an object
    }
    public Movie(Parcel in) {
        id = in.readInt();
        title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        original_title = in.readString();
        release_date = in.readString();
        vote_average = in.readDouble();
    }
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(poster_path);
        dest.writeString(overview);
        dest.writeString(original_title);
        dest.writeString(release_date);
        dest.writeDouble(vote_average);
    }


    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }


    public double getVote_average() {
        return vote_average;
    }

    public void setVote_average(double vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }


}
