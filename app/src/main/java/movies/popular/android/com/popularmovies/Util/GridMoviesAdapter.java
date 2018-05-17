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

import java.util.List;

import movies.popular.android.com.popularmovies.MainActivity;
import movies.popular.android.com.popularmovies.Modul.Movie;
import movies.popular.android.com.popularmovies.MovieDetails;
import movies.popular.android.com.popularmovies.R;


/**
 * Created by lkhnt on 3/24/2018.
 */


// very simple arrayAdapter which is used with gridView, I mainly looked for GridView definition in developers.android
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
        Context context = parent.getContext();
        int idOfRecyclerView = R.layout.movie_list;

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(idOfRecyclerView, parent, false);

        MovieViewHolder  holder = new MovieViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int width, height;
        if (dm.heightPixels > dm.widthPixels){
            width = dm.widthPixels / 2;
            height = height =( 3 * width )/2;
        }else{
            width = dm.widthPixels / 3 ;
            height =( 3 * width )/2;
        }
        Movie movie = movies.get(position);
        //holder.moviePoster.setLayoutParams(new RecyclerView.LayoutParams(100,100));
        final String SIZE = "w342//";
        String final_path = Utils.BASE_URL + SIZE + movie.getPoster_path();
        Picasso.get().load(final_path).centerCrop(77).resize(width, height).into(holder.moviePoster);

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
