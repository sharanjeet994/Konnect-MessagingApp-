package com.example.android.konnect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.konnect.Models.Users;
import com.example.android.konnect.databinding.ActivityLogInBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

public class LogInActivity extends AppCompatActivity {

    ActivityLogInBinding binding;

    ProgressDialog progressDialog;

    FirebaseAuth auth;

    private static final String TAG ="Login Activity" ;

    GoogleSignInClient mGoogleSignInClient;

    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // enable the method below when completely done with register and login and delete the code in the last

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //user stays signed in even if the application closes
        if(user != null) {  // To not ask the user to login again and again
            // User is signed in
            Intent i = new Intent(LogInActivity.this, MainActivity.class);// later have to change it to CatalogActivity.class
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");

        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        progressDialog = new ProgressDialog(LogInActivity.this);
        progressDialog.setTitle("Logging In");
        progressDialog.setMessage("Just A Moment :)");

        binding.textViewClickToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this,RegisterActivity.class));
            }
        });

        binding.buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editTextTextEmailAddress.getText().toString().trim();
                String password = binding.editTextTextPassword.getText().toString().trim();
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
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(LogInActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(LogInActivity.this,MainActivity.class));
                        }
                        else {
                            Toast.makeText(LogInActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        binding.buttonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // delete this if statement after enabling the above method

//        if (auth.getCurrentUser()!=null){
//
//            startActivity(new Intent(LogInActivity.this,MainActivity.class));
//        }

    }
    int RC_SIGN_IN = 72;
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();

                            Users userClass = new Users();
                            userClass.setUserId(user.getUid());
                            userClass.setUserName(user.getDisplayName());
                            userClass.setProfilePic(user.getPhotoUrl().toString());

                            database.getReference().child("Users").child(user.getUid()).setValue(userClass);

                            startActivity(new Intent(LogInActivity.this,MainActivity.class));
                            Toast.makeText(LogInActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(mBinding.mainLayout, "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }
}