package com.example.android.konnect.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.konnect.ChatDetailActivity;
import com.example.android.konnect.Models.Users;
import com.example.android.konnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>  {

    ArrayList<Users> usersArrayList;
    Context context;

    public UsersAdapter(ArrayList<Users> usersArrayList, Context context) {
        this.usersArrayList = usersArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.sample_user,parent,false);


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Users users = usersArrayList.get(position);
        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.ic_user_foreground).into(holder.userPic);
        holder.userName.setText(users.getUserName());

        FirebaseDatabase.getInstance().getReference().child("chats").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+users.getUserId())
                .orderByChild("timeStamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChildren()){
                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                        holder.recentMessage.setText(dataSnapshot.child("message").getValue().toString());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatDetailActivity.class);
                    intent.putExtra("userId",users.getUserId());
                    intent.putExtra("profilePic",users.getProfilePic());
                    intent.putExtra("userName",users.getUserName());

                        context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView userPic;
        TextView userName,recentMessage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userPic = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.relative_layout);
            recentMessage = itemView.findViewById(R.id.recentMessage);

        }
    }
}
