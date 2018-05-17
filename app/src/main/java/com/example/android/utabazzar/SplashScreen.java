package com.example.android.utabazzar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.utabazzar.ui.LoginActivity;
import com.romainpiel.shimmer.Shimmer;
import com.romainpiel.shimmer.ShimmerTextView;

public class SplashScreen extends AppCompatActivity {
    ShimmerTextView tv;
    Shimmer shimmer;
    Thread myThread;
    SharedPreferences sharedPreferences;
    String MY_PREFERENCES = "MY_PREFERENCES";
    String EMAIL_ID = "email_id";
    String USER_NAME = "user_name";
    String PASSWORD = "password";
    String IS_LOGGED = "IS_LOGGED";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        tv = findViewById(R.id.shimmerText);
        shimmer = new Shimmer();
        shimmer.start(tv);

        sharedPreferences = SplashScreen.this.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        myThread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1800);
                    if (sharedPreferences.getString(IS_LOGGED, "0").equals("1")) {
                        Intent intent = new Intent(getApplicationContext(), BottomNavigation.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}
