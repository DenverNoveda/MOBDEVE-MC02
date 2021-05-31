package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {

    private ArrayList<Review> reviews;
    private TextView tv_name_date2, tv_rating, tv_userReview;

    public ReviewsAdapter(ArrayList<Review> reviews) {this.reviews = reviews; }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder (final View view) {

            super(view);
            tv_name_date2 = view.findViewById(R.id.tv_name_date2);
            tv_rating = view.findViewById(R.id.tv_rating);
            tv_userReview = view.findViewById(R.id.tv_userReview);
        }
    }

    @NonNull
    @Override
    public ReviewsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View orderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews_list, parent, false);
        return new MyViewHolder(orderView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        tv_name_date2.setText(reviews.get(position).getReviewAuthor() + " | " + reviews.get(position).getReviewPublished());
        tv_rating.setText(reviews.get(position).getRating() + " / 5");
        tv_userReview.setText(reviews.get(position).getReviewContent());
        Log.v("revAdapter: ", "SET: " + tv_name_date2.getText().toString());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}