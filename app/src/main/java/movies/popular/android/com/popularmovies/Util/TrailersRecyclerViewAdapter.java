package movies.popular.android.com.popularmovies.Util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import movies.popular.android.com.popularmovies.R;


/*An adapter that manages the data in trailers recyclerView
* Its holder has TextView(Trailer label) and the Button when clicked switches the video key
* and plays the corresponding trailer*/
public class TrailersRecyclerViewAdapter extends RecyclerView.Adapter<TrailersRecyclerViewAdapter.TrailerViewHolder> {

    private int numOfVideos;
    private Context context;
    private onButtonClickListener listener;

    public TrailersRecyclerViewAdapter(Context context , int numOfVideos, onButtonClickListener listener){
        this.context = context;
        this.numOfVideos = numOfVideos;
        this.listener = listener;
    }


    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        int id  = R.layout.trailers_list_view;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(id, parent, false);
        TrailerViewHolder holder = new TrailerViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, final int position) {
        holder.trailer.append(Integer.toString(position+1));
        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.clickListener(position);
            }
        });
    }
    // the number of videos is gotten from an activity that uses this adapter
    @Override
    public int getItemCount() {
        return numOfVideos;
    }

    public interface onButtonClickListener {

        public void clickListener(int pos);

    }
    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        private Button playButton;
        private TextView trailer;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            trailer = (TextView) itemView.findViewById(R.id.traler_label);
            playButton = (Button) itemView.findViewById(R.id.trailer_play_button);
        }


    }

}
