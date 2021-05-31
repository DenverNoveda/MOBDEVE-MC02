package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.MyViewHolder> {

    private ArrayList<Recommend> recommendations;
    private TextView tv_name_date, tv_recommend;

    public RecommendationsAdapter(ArrayList<Recommend> recommendations) {this.recommendations = recommendations; }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder (final View view) {

            super(view);
            tv_name_date = view.findViewById(R.id.tv_name_date);
            tv_recommend = view.findViewById(R.id.tv_userRecommend);
        }
    }

    @NonNull
    @Override
    public RecommendationsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View orderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendations_list, parent, false);
        return new MyViewHolder(orderView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        tv_name_date.setText(recommendations.get(position).getReviewAuthor() + " | " + recommendations.get(position).getRecommendPublished());
        tv_recommend.setText(recommendations.get(position).getRecommendContent());
        Log.v("recoAdapter: ", "SET: " + tv_name_date.getText().toString());
    }

    @Override
    public int getItemCount() {
        return recommendations.size();
    }
}
