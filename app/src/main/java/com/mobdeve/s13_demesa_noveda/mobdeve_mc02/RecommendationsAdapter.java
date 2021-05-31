package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

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

public class RecommendationsAdapter extends RecyclerView.Adapter<RecommendationsAdapter.MyViewHolder> {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = auth.getCurrentUser();

    private ArrayList<Recommend> recommendations;
    private TextView tv_name_date, tv_recommend, tv_recommendLikeNo, tv_recommendDislikeNo;
    private ImageView iv_recommendLike, iv_recommendDislike;
    private String user;


    public RecommendationsAdapter(ArrayList<Recommend> recommendations) {this.recommendations = recommendations; }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder (final View view) {

            super(view);
            tv_name_date = view.findViewById(R.id.tv_name_date);
            tv_recommend = view.findViewById(R.id.tv_userRecommend);
            tv_recommendLikeNo = view.findViewById(R.id.tv_recommendLikeNo);
            tv_recommendDislikeNo = view.findViewById(R.id.tv_recommendDislikeNo);
            iv_recommendLike = view.findViewById(R.id.iv_recommendLike);
            iv_recommendDislike = view.findViewById(R.id.iv_recommendDislike);

            user = currentUser.getDisplayName();
        }
        public void setButtonListener(View.OnClickListener ocl) {
            iv_recommendLike.setOnClickListener(ocl);
            iv_recommendDislike.setOnClickListener(ocl);
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
        tv_recommendLikeNo.setText((String.valueOf(recommendations.get(position).getLikes())));
        tv_recommendDislikeNo.setText((String.valueOf(recommendations.get(position).getDislikes())));
        Log.v("recoAdapter: ", "SET: " + tv_name_date.getText().toString());
        holder.setButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("RecommendationsAdapter", "LikeFunction():Triggered");
                if (v.getId() == R.id.iv_recommendLike) {
                    // like review
                    addLike(position);
                    notifyDataSetChanged();
                }

                if (v.getId() == R.id.iv_recommendDislike) {
                    // dislike review
                    addDislike(position);
                    notifyDataSetChanged();
                }
            }
        });
    }
    public void addLike(int position) {
        database.collection("recommendations").whereEqualTo("username", recommendations.get(position).getReviewAuthor()).whereEqualTo("recommendationText", recommendations.get(position).getRecommendContent()).get()
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
                                        recommendations.get(position).setLikes(likeVal);
                                        document.getReference().update("likes", likeVal);
                                        document.getReference().update("userLikers", likers);
                                        notifyDataSetChanged();
                                     }else{
                                        int likeVal = (int) l + 1;
                                        int dislikeVal = (int) d - 1;
                                        likers.add(user);
                                        recommendations.get(position).setLikes(likeVal);
                                        recommendations.get(position).setDislikes(dislikeVal);
                                        document.getReference().update("likes", likeVal);
                                        document.getReference().update("dislikes", dislikeVal);
                                        document.getReference().update("userLikers", likers);
                                        document.getReference().update("userDislikers", dislikers);
                                    }
                                }else{
                                    int likeVal = (int) l - 1;
                                    recommendations.get(position).setLikes(likeVal);
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
        database.collection("recommendations").whereEqualTo("username", recommendations.get(position).getReviewAuthor()).whereEqualTo("recommendationText", recommendations.get(position).getRecommendContent()).get()
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
                                        recommendations.get(position).setDislikes(dislikeVal);
                                        document.getReference().update("dislikes", dislikeVal);
                                        document.getReference().update("userDislikers", dislikers);
                                    }else{
                                        int dislikeVal = (int) d + 1;
                                        int likeVal = (int) l - 1;
                                        dislikers.add(user);
                                        recommendations.get(position).setLikes(likeVal);
                                        recommendations.get(position).setDislikes(dislikeVal);
                                        document.getReference().update("likes", likeVal);
                                        document.getReference().update("dislikes", dislikeVal);
                                        document.getReference().update("userLikers", likers);
                                        document.getReference().update("userDislikers", dislikers);
                                    }
                                }else{
                                    int dislikeVal = (int) d - 1;
                                    recommendations.get(position).setDislikes(dislikeVal);
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
        return recommendations.size();
    }
}
