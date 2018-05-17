package com.example.android.utabazzar;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

public class MainActivity1 extends AppCompatActivity {
    private static FragmentManager fragmentManager;
    //DO NOT ADD A '/' AT THE END below
    public static String domain = "http://52.90.174.26:8000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();

        // If savedinstnacestate is null then replace login fragment

    }

    // Replace Login Fragment with animation
    protected void replaceLoginFragment() {


    }

    @Override
    public void onBackPressed() {

    }

}
