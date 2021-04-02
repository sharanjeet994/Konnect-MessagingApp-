package com.example.android.konnect;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.android.konnect.Fragments.ChatsFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.internal.Slashes;

import static java.lang.Thread.sleep;



public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        sleep(3000);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    finally {
                        if (user!=null){
                            startActivity(new Intent(SplashScreen.this,MainActivity.class));
                        }
                        else{
                            startActivity(new Intent(SplashScreen.this,LogInActivity.class));
                        }
                    }
                }
            };thread.start();



    }
}

