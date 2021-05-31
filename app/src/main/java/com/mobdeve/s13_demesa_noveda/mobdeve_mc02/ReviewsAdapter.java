package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = auth.getCurrentUser();

    private ArrayList<Review> reviews;
    private TextView tv_name_date2, tv_rating, tv_userReview, tv_reviewLikeNo, tv_reviewDislikeNo;
    private ImageView iv_reviewLike, iv_reviewDislike;
    private String user;

    public ReviewsAdapter(ArrayList<Review> reviews) {this.reviews = reviews; }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder (final View view) {

            super(view);
            tv_name_date2 = view.findViewById(R.id.tv_name_date2);
            tv_rating = view.findViewById(R.id.tv_rating);
            tv_userReview = view.findViewById(R.id.tv_userReview);
            tv_reviewLikeNo = view.findViewById(R.id.tv_reviewLikeNo);
            tv_reviewDislikeNo = view.findViewById(R.id.tv_reviewDislikeNo);
            iv_reviewLike = view.findViewById(R.id.iv_reviewLike);
            iv_reviewDislike = view.findViewById(R.id.iv_reviewDislike);

            user = currentUser.getDisplayName();
        }

        public void setButtonListener(View.OnClickListener ocl) {
            iv_reviewLike.setOnClickListener(ocl);
            iv_reviewDislike.setOnClickListener(ocl);
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
        tv_reviewLikeNo.setText(reviews.get(position).getLikes() + "");
        tv_reviewDislikeNo.setText(reviews.get(position).getDislikes() + "");
        iv_reviewLike.setImageResource(R.drawable.like);
        iv_reviewDislike.setImageResource(R.drawable.dislike);
        Log.v("revAdapter: ", "SET: " + tv_name_date2.getText().toString());

        holder.setButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("ReviewAdapter", "LikeFunction():Triggered");
                if (v.getId() == R.id.iv_reviewLike) {
                    // like review
                    addLike(position);
                    notifyDataSetChanged();
                }

                if (v.getId() == R.id.iv_reviewDislike) {
                    // dislike review
                    addDislike(position);
                    notifyDataSetChanged();
                }
            }
        });
    }
    public void addLike(int position) {
        database.collection("reviews").whereEqualTo("username", reviews.get(position).getReviewAuthor()).whereEqualTo("reviewText", reviews.get(position).getReviewContent()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                QueryDocumentSnapshot doc = document;
                                long l = (long) doc.get("likes");
                                long d = (long) doc.get("dislikes");
                                boolean isLiked = false;
                                boolean isDisliked = false;

                                ArrayList<String> dislikers = (ArrayList<String>) doc.get("userDislikers");
                                for(int i = 0; i < dislikers.size(); i++){
                                    if(dislikers.get(i).equalsIgnoreCase(user)){
                                        isDisliked = true;
                                        dislikers.remove(i);
                                    }

                                }
                                ArrayList<String> likers = (ArrayList<String>) doc.get("userLikers");
                                for(int i = 0; i < likers.size(); i++){
                                    if(likers.get(i).equalsIgnoreCase(user)){
                                        isLiked = true;
                                        likers.remove(i);
                                    }
                                }

                                if(!isLiked){
                                    if(!isDisliked){
                                        int likeVal = (int) l + 1;
                                        likers.add(user);
                                        reviews.get(position).setLikes(likeVal);
                                        document.getReference().update("likes", likeVal);
                                        document.getReference().update("userLikers", likers);
                                        notifyDataSetChanged();
                                    }else{
                                        int likeVal = (int) l + 1;
                                        int dislikeVal = (int) d - 1;
                                        likers.add(user);
                                        reviews.get(position).setLikes(likeVal);
                                        reviews.get(position).setDislikes(dislikeVal);
                                        document.getReference().update("likes", likeVal);
                                        document.getReference().update("dislikes", dislikeVal);
                                        document.getReference().update("userLikers", likers);
                                        document.getReference().update("userDislikers", dislikers);
                                    }
                                }else{
                                    int likeVal = (int) l - 1;
                                    reviews.get(position).setLikes(likeVal);
                                    document.getReference().update("likes", likeVal);
                                    document.getReference().update("userLikers", likers);
                                }
                                Log.d("RecommendAdapter", "LikeFunction(): +1 Like!: ");
                            }
                        } else {
                            Log.d("RecommendAdapter", "LikeFunction(): Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    public void addDislike(int position){
        database.collection("reviews").whereEqualTo("username", reviews.get(position).getReviewAuthor()).whereEqualTo("reviewText", reviews.get(position).getReviewContent()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                QueryDocumentSnapshot doc = document;
                                long l = (long) doc.get("likes");
                                long d = (long) doc.get("dislikes");
                                boolean isLiked = false;
                                boolean isDisliked = false;

                                ArrayList<String> dislikers = (ArrayList<String>) doc.get("userDislikers");
                                for(int i = 0; i < dislikers.size(); i++){
                                    if(dislikers.get(i).equalsIgnoreCase(user)){
                                        isDisliked = true;
                                        dislikers.remove(i);
                                    }
                                }
                                ArrayList<String> likers = (ArrayList<String>) doc.get("userLikers");
                                for(int i = 0; i < likers.size(); i++){
                                    if(likers.get(i).equalsIgnoreCase(user)){
                                        isLiked = true;
                                        likers.remove(i);
                                    }
                                }

                                if(!isDisliked){
                                    if(!isLiked){
                                        int dislikeVal = (int) d + 1;
                                        dislikers.add(user);
                                        reviews.get(position).setDislikes(dislikeVal);
                                        document.getReference().update("dislikes", dislikeVal);
                                        document.getReference().update("userDislikers", dislikers);
                                    }else{
                                        int dislikeVal = (int) d + 1;
                                        int likeVal = (int) l - 1;
                                        dislikers.add(user);
                                        reviews.get(position).setLikes(likeVal);
                                        reviews.get(position).setDislikes(dislikeVal);
                                        document.getReference().update("likes", likeVal);
                                        document.getReference().update("dislikes", dislikeVal);
                                        document.getReference().update("userLikers", likers);
                                        document.getReference().update("userDislikers", dislikers);
                                    }
                                }else{
                                    int dislikeVal = (int) d - 1;
                                    reviews.get(position).setDislikes(dislikeVal);
                                    document.getReference().update("dislikes", dislikeVal);
                                    document.getReference().update("userDislikers", dislikers);
                                }
                                Log.d("RecommendAdapter", "DislikeFunction(): +1 Like!: ");
                            }
                        } else {
                            Log.d("RecommendAdapter", "DislikeFunction(): Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }
}