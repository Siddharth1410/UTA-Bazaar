package com.example.android.utabazzar;
//do not use
//import android.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class club_tab extends AppCompatActivity {
    public ActionBar toolbar;
    boolean doubleBackToExitPressedOnce = false;
    public String thisClubName;
    FloatingActionButton addMember;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisClubName = getIntent().getStringExtra("CLUB_NAME");
        setContentView(R.layout.activity_club_tab2);

        toolbar = getSupportActionBar();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        addMember = findViewById(R.id.addMem);
        toolbar.setTitle("Club Store");
        Bundle bundle = new Bundle();
        bundle.putString("CLUB_NAME", thisClubName);
        ClubStoreFragment fragment = new ClubStoreFragment();
        fragment.setArguments(bundle);
        loadFragment(fragment);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(club_tab.this, ClubMembers.class);
                intent.putExtra("CLUB_NAME", thisClubName);
                startActivity(intent);
            }
        });
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_shop:
                    toolbar.setTitle("Club Store");
                    Bundle bundle = new Bundle();
                    bundle.putString("CLUB_NAME", thisClubName);
                    ClubStoreFragment fragment = new ClubStoreFragment();
                    fragment.setArguments(bundle);
                    loadFragment(fragment);
                    return true;
                case R.id.navigation_cart:
                    Intent club_sell = new Intent(getApplicationContext(),ClubSell.class);
                    club_sell.putExtra("CLUB_NAME", thisClubName);
                    startActivity(club_sell);
                    finish();
                    return true;
                case R.id.club_navigation_message:
                    Intent intent = new Intent(getApplicationContext(),club_chat.class);
                    intent.putExtra("CLUB_NAME", thisClubName);
                    startActivity(intent);
                    finish();
                    return true;
            }

            return false;
        }
    };
    //
//    @Override
//    public void onBackPressed() {
//        if (fragment == storeFragment) {
//            finish();
//        } else {
//            loadFragment(storeFragment);
//            fragment = storeFragment;
//        }
//
//    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
