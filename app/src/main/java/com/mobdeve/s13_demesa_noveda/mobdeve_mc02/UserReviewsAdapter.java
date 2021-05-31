package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class UserReviewsAdapter extends RecyclerView.Adapter<UserReviewsAdapter.MyViewHolder> {

    private ArrayList<Review> reviews;
    private TextView tv_movie_date2, tv_userReview, tv_rating;

    public UserReviewsAdapter(ArrayList<Review> reviews) {this.reviews = reviews; }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder (final View view) {

            super(view);
            tv_movie_date2 = view.findViewById(R.id.tv_movie_date2);
            tv_userReview = view.findViewById(R.id.tv_userReview);
            tv_rating = view.findViewById(R.id.tv_rating);
        }
    }

    @NonNull
    @Override
    public UserReviewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View orderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_reviews_list, parent, false);
        return new MyViewHolder(orderView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        tv_movie_date2.setText(reviews.get(position).getReviewMovieName() + " | " + reviews.get(position).getReviewPublished());
        tv_rating.setText(reviews.get(position).getRating() + " / 5");
        tv_userReview.setText(reviews.get(position).getReviewContent());
        Log.v("userRevAdapter: ", "SET: " + tv_movie_date2.getText().toString());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }


}