package com.example.android.konnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.android.konnect.Adapters.ChatsAdapter;
import com.example.android.konnect.Models.Messages;
import com.example.android.konnect.databinding.ActivityChatDetailBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class ChatDetailActivity extends AppCompatActivity {

    ActivityChatDetailBinding binding;

    FirebaseDatabase database;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        database = FirebaseDatabase.getInstance();

        auth = FirebaseAuth.getInstance();

        final String senderId = auth.getUid();
        String receiverId = getIntent().getStringExtra("userId");
        String userName = getIntent().getStringExtra("userName");
        String profilePic = getIntent().getStringExtra("profilePic");

        binding.userName.setText(userName);
        Picasso.get().load(profilePic).placeholder(R.drawable.ic_user_foreground).into(binding.profileImage);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();// jumps to MainActivity
            }
        });

        ArrayList<Messages> messagesArrayList = new ArrayList<>();

        final ChatsAdapter chatsAdapter = new ChatsAdapter(messagesArrayList,this,receiverId);

        binding.chatsRecyclerView.setAdapter(chatsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatsRecyclerView.setLayoutManager(layoutManager);

        final String senderSide = senderId + receiverId;
        final String receiverSide = receiverId + senderId;

        database.getReference().child("chats").child(senderSide).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Messages messages = dataSnapshot.getValue(Messages.class);

                    messages.setMessageId(dataSnapshot.getKey());

                    messagesArrayList.add(messages);
                    binding.chatsRecyclerView.smoothScrollToPosition(messagesArrayList.size()-1);
                }
                chatsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        binding.sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = binding.editTextMessage.getText().toString();
                if (message.isEmpty()){
                    binding.editTextMessage.setError("Enter your message");
                    return;
                }

                final Messages messages = new Messages(senderId,message);
                messages.setTimeStamp(new Date().getTime());
                binding.editTextMessage.setText("");

                database.getReference().child("chats").child(senderSide).push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        database.getReference().child("chats").child(receiverSide).push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            chatsAdapter.notifyDataSetChanged();

                            }
                        });
                    }
                });

            }
        });



    }
}