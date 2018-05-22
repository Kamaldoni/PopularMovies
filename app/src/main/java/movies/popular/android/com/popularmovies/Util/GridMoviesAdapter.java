package movies.popular.android.com.popularmovies.Util;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.service.autofill.FillContext;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import movies.popular.android.com.popularmovies.MainActivity;
import movies.popular.android.com.popularmovies.Modul.Movie;
import movies.popular.android.com.popularmovies.MovieDetails;
import movies.popular.android.com.popularmovies.R;


/**
 * Created by lkhnt on 3/24/2018.
 */


/*An adapter which manages the data in movies recyclerView.
* ViewHolder got only the movie poster that should be shown
* and switch Activity when clicked
* In order to fill all the spaces I filled the width of display and made the width/height
* ratio of an image equal to 2/3
* Its same for both landscape and portrait mode
* I used Picasso to load the poster from the given url*/

public class GridMoviesAdapter extends RecyclerView.Adapter<GridMoviesAdapter.MovieViewHolder>{

    private List<Movie> movies;
    private GridItemViewListener listener;
    private Context mContext;

    public GridMoviesAdapter(Context context ,List<Movie> movies, GridItemViewListener listener){
        this.mContext = context;
        this.movies = movies;
        this.listener = listener;
    }


    public interface GridItemViewListener{

        void onClickItemListener(int clickedItemIndex);
    }


    @NonNull
    @Override
    public GridMoviesAdapter.MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        int idOfRecyclerView = R.layout.movie_list;

        LayoutInflater inflater = LayoutInflater.from(mContext);

        View view = inflater.inflate(idOfRecyclerView, parent, false);

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {

        Movie movie = movies.get(position);
        final String SIZE = "w342//";
        String final_path = Utils.BASE_URL + SIZE + movie.getPoster_path();
        Picasso.get().load(final_path).fit().into(holder.moviePoster);

    }

    public void swapData(List<Movie> movieList){
        movies = movieList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        if(movies == null)
        return 0;
        else
            return movies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView moviePoster;


        public MovieViewHolder(View itemView) {
            super(itemView);
            moviePoster = itemView.findViewById(R.id.movie_poster);
            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            listener.onClickItemListener(getAdapterPosition());
        }
    }
}
