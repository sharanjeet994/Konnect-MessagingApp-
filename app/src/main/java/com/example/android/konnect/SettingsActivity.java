package com.example.android.konnect;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.android.konnect.Models.Users;
import com.example.android.konnect.databinding.ActivitySettingsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;

    FirebaseStorage storage;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        storage = FirebaseStorage.getInstance();
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        binding.backSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = binding.etNameSettings.getText().toString().trim();
                String bio = binding.etBio.getText().toString().trim();

                HashMap<String,Object> map = new HashMap<>();
                map.put("userName",userName);
                map.put("bio",bio);

                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(map);

                Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });

        binding.picPlusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent();
                i.setAction(Intent.ACTION_GET_CONTENT);
                i.setType("image/*");
                startActivityForResult(i,72);


            }
        });

        database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    Users users = snapshot.getValue(Users.class);
                    Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.ic_user_foreground).into(binding.profilePicSettings);

                    binding.etNameSettings.setText(users.getUserName());
                    binding.etBio.setText(users.getBio());


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        try {

                Uri file = data.getData();  // Phone Internal Storage Path
                binding.profilePicSettings.setImageURI(file);

                final StorageReference reference = storage.getReference().child("profilePics").child(FirebaseAuth.getInstance().getUid());
                // Whenever the user updates the image it gets replaced as it is saved as a specific user id
                reference.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                                        .child("profilePic").setValue(uri.toString());

                            }
                        });

                        Toast.makeText(SettingsActivity.this, "Profile Picture Updated", Toast.LENGTH_SHORT).show();

                    }
                });


        }
        catch (Exception e){
            Toast.makeText(this, "No Image Selected", Toast.LENGTH_SHORT).show();
        }


    }
}