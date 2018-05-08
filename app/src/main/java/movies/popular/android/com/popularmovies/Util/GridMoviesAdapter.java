package movies.popular.android.com.popularmovies.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import movies.popular.android.com.popularmovies.Modul.Movie;

/**
 * Created by lkhnt on 3/24/2018.
 */


// very simple arrayAdapter which is used with gridView, I mainly looked for GridView definition in developers.android
public class GridMoviesAdapter extends ArrayAdapter<Movie>{

    private ImageView poster;
    private final String SIZE = "w342//";
    private String final_path;
    public GridMoviesAdapter(@NonNull Context context, @NonNull List<Movie> objects) {
        super(context,0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        poster = new ImageView(getContext());
        Movie movie = getItem(position);
        final_path = Utils.BASE_URL + SIZE + movie.getPoster_path();


        Picasso.get().load(final_path).centerCrop().resize(300,450).into(poster);


        return poster;
    }

}
