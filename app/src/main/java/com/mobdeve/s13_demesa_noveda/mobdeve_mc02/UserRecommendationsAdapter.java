package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserRecommendationsAdapter extends RecyclerView.Adapter<UserRecommendationsAdapter.MyViewHolder> {

    private ArrayList<Recommend> recommendations;
    private TextView tv_movie_date, tv_userRecommend;

    public UserRecommendationsAdapter(ArrayList<Recommend> recommendations) {this.recommendations = recommendations; }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder (final View view) {

            super(view);
            tv_movie_date = view.findViewById(R.id.tv_movie_date);
            tv_userRecommend = view.findViewById(R.id.tv_userRecommend);
        }
    }

    @NonNull
    @Override
    public UserRecommendationsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View orderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_recommendations_list, parent, false);
        return new MyViewHolder(orderView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        tv_movie_date.setText(recommendations.get(position).getReviewMovieName() + " | " + recommendations.get(position).getRecommendPublished());
        tv_userRecommend.setText(recommendations.get(position).getRecommendContent());
        Log.v("userRecoAdapter: ", "SET: " + tv_movie_date.getText().toString());
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }


}
