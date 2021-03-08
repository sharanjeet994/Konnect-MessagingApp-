package com.example.android.konnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.view.View;

import com.example.android.konnect.Adapters.ChatsAdapter;
import com.example.android.konnect.Models.Messages;
import com.example.android.konnect.databinding.ActivityChatRoomBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ChatRoomActivity extends AppCompatActivity {

    ActivityChatRoomBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivityChatRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final ArrayList<Messages> messagesArrayList = new ArrayList<>();

        final String senderId = FirebaseAuth.getInstance().getUid();
        binding.userName.setText("Chat Room");

        final ChatsAdapter chatsAdapter = new ChatsAdapter(messagesArrayList,this);

        binding.chatsRecyclerView.setAdapter(chatsAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.chatsRecyclerView.setLayoutManager(layoutManager);


        database.getReference().child("Chat Room").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                messagesArrayList.clear();

                for (DataSnapshot dataSnapshot:snapshot.getChildren()){

                    Messages messages = dataSnapshot.getValue(Messages.class);

                    messagesArrayList.add(messages);

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
                final String message = binding.editTextMessage.getText().toString();
                if (message.isEmpty()){
                    binding.editTextMessage.setError("Enter your message");
                    return;
                }
                final Messages messages = new Messages(senderId,message);
                messages.setTimeStamp(new Date().getTime());
                binding.editTextMessage.setText("");

                database.getReference().child("Chat Room").push().setValue(messages).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {



                    }
                });
            }
        });

    }
    private String formatTime(Date timeObject){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm a");

        return simpleDateFormat.format(timeObject);

    }

}