package com.mobdeve.s13_demesa_noveda.mobdeve_mc02;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class UserFavoritesAdapter extends RecyclerView.Adapter<UserFavoritesAdapter.MyViewHolder> {

    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseFirestore database = FirebaseFirestore.getInstance();
    private FirebaseUser currentUser = auth.getCurrentUser();

    private ArrayList<Favorite> favorites;
    private TextView tv_favMovie;
    private Button btn_removeFromFav;

    public UserFavoritesAdapter(ArrayList<Favorite> favorites) {this.favorites = favorites; }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder (final View view) {

            super(view);
            tv_favMovie = view.findViewById(R.id.tv_favMovie);
            btn_removeFromFav = view.findViewById(R.id.btn_removeFromFav);
        }

        public void setButtonListener(View.OnClickListener ocl) {
            btn_removeFromFav.setOnClickListener(ocl);
        }
    }

    @NonNull
    @Override
    public UserFavoritesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View orderView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorites_list, parent, false);
        return new MyViewHolder(orderView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        tv_favMovie.setText(favorites.get(position).getFavoriteMovieName());

        holder.setButtonListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateUserFavorites(favorites.get(position).getFavoriteMovieName());
                favorites.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, favorites.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public void updateUserFavorites(String movie) {
        String user = currentUser.getDisplayName();

        database.collection("favorites").whereEqualTo("username", user).whereEqualTo("movie", movie).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                document.getReference().delete();
                                Log.d("UserFavoritesAdapter", "updateUserFavorites(): Document successfully deleted.");
                            }
                        } else {
                            Log.d("UserFavoritesAdapter", "updateUserFavorites(): Error getting documents: ", task.getException());
                        }
                    }
                });
    }


}
