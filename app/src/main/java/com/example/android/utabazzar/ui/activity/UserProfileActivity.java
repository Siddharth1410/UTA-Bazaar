package com.example.android.utabazzar.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.utabazzar.BottomNavigation;
import com.example.android.utabazzar.PagerActivity;
import com.example.android.utabazzar.SellActivity;
import com.example.android.utabazzar.SplashScreen;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import com.example.android.utabazzar.R;
import com.example.android.utabazzar.ui.adapter.UserProfileAdapter;
import com.example.android.utabazzar.ui.utils.CircleTransformation;
import com.example.android.utabazzar.ui.view.RevealBackgroundView;

/**
 * Created by Miroslaw Stanek on 14.01.15.
 */
public class UserProfileActivity extends BaseDrawerActivity implements RevealBackgroundView.OnStateChangeListener {
    public static final String ARG_REVEAL_START_LOCATION = "reveal_start_location";
    SharedPreferences sharedPreferences;
    String myPreferences = "MY_PREFERENCES";
    String emailId = "email_id";
    private static final int USER_OPTIONS_ANIMATION_DELAY = 300;
    private static final Interpolator INTERPOLATOR = new DecelerateInterpolator();
    public ActionBar toolbar;
    @BindView(R.id.vRevealBackground)
    RevealBackgroundView vRevealBackground;


    @BindView(R.id.tlUserProfileTabs)
    TabLayout tlUserProfileTabs;

    @BindView(R.id.ivUserProfilePhoto)
    ImageView ivUserProfilePhoto;
    @BindView(R.id.vUserDetails)
    View vUserDetails;
    @BindView(R.id.btnLogout)
    Button logout;
    @BindView(R.id.vUserProfileRoot)
    View vUserProfileRoot;
    String currActivity;
    private int avatarSize;
    private String profilePhoto;
    private UserProfileAdapter userPhotosAdapter;
    TextView tvUserName;
    TextView tvUserEmail;
    FloatingActionButton profileSell;
    public static void startUserProfileFromLocation(int[] startingLocation, Activity startingActivity) {
        Intent intent = new Intent(startingActivity, UserProfileActivity.class);
        intent.putExtra(ARG_REVEAL_START_LOCATION, startingLocation);
        startingActivity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        sharedPreferences = UserProfileActivity.this.getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
        this.avatarSize = getResources().getDimensionPixelSize(R.dimen.user_profile_avatar_size);
        this.profilePhoto = "https://pbs.twimg.com/profile_images/931643595051950080/4pGLu7Zi_400x400.jpg";
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerActivity adapter = new PagerActivity(this, getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tlUserProfileTabs);
        tabLayout.setupWithViewPager(viewPager);
        tvUserName = (TextView) findViewById(R.id.userNameP);
        tvUserEmail = findViewById(R.id.userEmailP);
        tvUserName.setText(sharedPreferences.getString("user_name","Name"));
        tvUserEmail.setText(sharedPreferences.getString("email_id","Email"));
        profileSell = (FloatingActionButton) findViewById(R.id.btnCreate);
        logout = (Button) findViewById(R.id.btnLogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
        Picasso.with(this)
                .load(profilePhoto)
                .placeholder(R.drawable.img_circle_placeholder)
                .resize(avatarSize, avatarSize)
                .centerCrop()
                .transform(new CircleTransformation())
                .into(ivUserProfilePhoto);


       /* TabLayout tabs = (TabLayout) findViewById(R.id.tlUserProfileTabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());*/

       // pager.setAdapter(adapter);
        //tabs.setupWithViewPager(pager);
        //setupTabs();
        setupUserProfileGrid();
        setupRevealBackground(savedInstanceState);
        profileSell.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                currActivity = "UserProfileActivity";
                Intent intent = new Intent(UserProfileActivity.this, SellActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
    public void onBackPressed() {
        Intent intent = new Intent(UserProfileActivity.this, BottomNavigation.class);
        startActivity(intent);
        finish();
        toolbar = getSupportActionBar();
    }
    private void setupTabs() {
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));
    }

    private void setupUserProfileGrid() {
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
    }

    private void setupRevealBackground(Bundle savedInstanceState) {
        vRevealBackground.setOnStateChangeListener(this);
        if (savedInstanceState == null) {
            final int[] startingLocation = getIntent().getIntArrayExtra(ARG_REVEAL_START_LOCATION);
            vRevealBackground.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    vRevealBackground.getViewTreeObserver().removeOnPreDrawListener(this);
                    vRevealBackground.startFromLocation(startingLocation);
                    return true;
                }
            });
        } else {
            vRevealBackground.setToFinishedFrame();
            userPhotosAdapter.setLockedAnimations(true);
        }
    }

    @Override
    public void onStateChange(int state) {
        if (RevealBackgroundView.STATE_FINISHED == state) {

            tlUserProfileTabs.setVisibility(View.VISIBLE);
            vUserProfileRoot.setVisibility(View.VISIBLE);
            userPhotosAdapter = new UserProfileAdapter(this);

            animateUserProfileOptions();
            animateUserProfileHeader();
        } else {
            tlUserProfileTabs.setVisibility(View.INVISIBLE);
            vUserProfileRoot.setVisibility(View.INVISIBLE);
        }
    }

    private void animateUserProfileOptions() {
        tlUserProfileTabs.setTranslationY(-tlUserProfileTabs.getHeight());
        tlUserProfileTabs.animate().translationY(0).setDuration(300).setStartDelay(USER_OPTIONS_ANIMATION_DELAY).setInterpolator(INTERPOLATOR);
    }

    private void animateUserProfileHeader() {
           vUserProfileRoot.setTranslationY(-vUserProfileRoot.getHeight());
           ivUserProfilePhoto.setTranslationY(-ivUserProfilePhoto.getHeight());
           vUserDetails.setTranslationY(-vUserDetails.getHeight());


           vUserProfileRoot.animate().translationY(0).setDuration(300).setInterpolator(INTERPOLATOR);
           ivUserProfilePhoto.animate().translationY(0).setDuration(300).setStartDelay(100).setInterpolator(INTERPOLATOR);
           vUserDetails.animate().translationY(0).setDuration(300).setStartDelay(200).setInterpolator(INTERPOLATOR);
    }

    private void signOut() {
        SharedPreferences.Editor e = sharedPreferences.edit();
        e.clear();
        e.apply();
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("IS_LOGGED", "0");
//        editor.apply();
        //Login_Fragment.mGoogleSignInClient.signOut();
        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);
        finish();
    }
}
