package com.example.android.konnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.android.konnect.Models.Users;
import com.example.android.konnect.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;   // view binding eliminates findviewbyid...

    private FirebaseAuth auth;
    FirebaseDatabase database;

    ProgressDialog progressDialog;

    private static final String TAG ="Register Activity" ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        progressDialog = new ProgressDialog(RegisterActivity.this);
        progressDialog.setTitle("Registration In Process");
        progressDialog.setMessage("Just A Moment :)");

        binding.textViewHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LogInActivity.class));
            }
        });


        binding.buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = binding.editTextTextPersonName.getText().toString().trim();
                String email = binding.editTextTextEmailAddress.getText().toString().trim();
                String password = binding.editTextTextPassword.getText().toString().trim();

                if(name.isEmpty()){
                    binding.editTextTextPersonName.setError("Name is required!!");
                    binding.editTextTextPersonName.requestFocus();
                    return;
                }

                if(email.isEmpty()){
                    binding.editTextTextEmailAddress.setError("Email is required!!");
                    binding.editTextTextEmailAddress.requestFocus();
                    return;
                }
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    binding.editTextTextEmailAddress.setError("Enter a Valid Email Address!!");
                    binding.editTextTextEmailAddress.requestFocus();
                    return;
                }
                if (password.isEmpty()) {
                    binding.editTextTextPassword.setError("Enter a Password!!");
                    binding.editTextTextPassword.requestFocus();
                    return;
                }
                if(password.length() < 6){
                    binding.editTextTextPassword.setError("Enter at least 6 Characters!!");
                    binding.editTextTextPassword.requestFocus();
                    return;
                }
                progressDialog.show();
                auth.createUserWithEmailAndPassword
                        (email,password).
                        addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();

                        if(task.isSuccessful()){
                            Users user = new Users(name,email,password);

                            String id = task.getResult().getUser().getUid();
                            database.getReference().child("Users").child(id).setValue(user);

                            Toast.makeText(RegisterActivity.this, "User Registered Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this,LogInActivity.class));
                        }
                        else{

                            Toast.makeText(RegisterActivity.this,task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                        }


                    }
                });


            }
        });
    }
}