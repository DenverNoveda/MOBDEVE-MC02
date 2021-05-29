package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MyViewHolder> {
    private ArrayList<Movie> data;
    public MovieListAdapter(ArrayList data){this.data = data;}

    public int getItemCount(){return data.size();}

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_listItemName;
        private ImageView iv_listItemPoster;
        private LinearLayout movieListItem_linearLayout;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);
            this.iv_listItemPoster = itemView.findViewById(R.id.iv_listItemPoster);
            this.tv_listItemName = itemView.findViewById(R.id.tv_listItemName);
            this.movieListItem_linearLayout = itemView.findViewById(R.id.movieListItem_LinearLayout);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(v.getContext(), MovieInfoActivity.class);
                    String movieID = data.get(getAdapterPosition()).getMovieID();
                    i.putExtra("MOVIE_ID", movieID);
                    v.getContext().startActivity(i);
                }
            });
        }

        public void setMovieName(String title){
            this.tv_listItemName.setText(title);
        }
        public void setMoviePoster(int position){
            Picasso.get().load(data.get(position).getImage()).into(iv_listItemPoster);
            iv_listItemPoster.setImageAlpha(255);
        }

    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.v("Movie Name: ",  data.get(position).getMovieName());
        holder.setMovieName(data.get(position).getMovieName());
        holder.setMoviePoster(position);
    }

}
