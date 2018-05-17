package com.example.android.utabazzar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.android.utabazzar.customfonts.MyTextView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.squareup.picasso.Picasso;

public class ProductDetails1 extends AppCompatActivity {
    Spinner spinner1, spinner2, spinner3;
    Button delete, message;
    SharedPreferences sharedPreferences;
    String myPreferences = "MY_PREFERENCES";
    ImageView imageView;
    TextView producName, productPrice, productId, seller_name, seller_phone, seller_email, seller_block, seller_room, time_period;
    String prod_id;
    com.example.android.utabazzar.customfonts.MyTextView buynowButton;
    public static String seller_email_stat, product_name_stat, seller_utaid_stat;

    public static String productPriceUsd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details_final);
        MobileAds.initialize(this, "ca-app-pub-1934170678397804~9054093363");
        sharedPreferences = ProductDetails1.this.getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-1934170678397804/2059987756");
        imageView = (ImageView) findViewById(R.id.product_pic);
        producName = (TextView) findViewById(R.id.product_name1);
        productPrice = (TextView) findViewById(R.id.product_price1);
        seller_name = (TextView) findViewById(R.id.sold_by);
        //seller_phone = (TextView) findViewById(R.id.seller_phone);
        //seller_email = (TextView) findViewById(R.id.seller_email);
        //seller_block = (TextView) findViewById(R.id.seller_block);
        //seller_room = (TextView) findViewById(R.id.seller_room);
        //time_period = (TextView) findViewById(R.id.time_period);

        delete = (Button) findViewById(R.id.delete);


        buynowButton = (MyTextView) findViewById(R.id.buyNowV);
        message = (Button) findViewById(R.id.message);
        adView = findViewById(R.id.adView1);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        Album album = (Album) getIntent().getSerializableExtra("Album");
        Picasso.with(this).load("http://52.90.174.26:8000" + album.getUrl()).into(imageView);
        producName.setText(album.getProduct_name());
        productPrice.setText("$"+album.getProduct_price());
        seller_name.setText(album.getSeller_name());
        seller_email_stat = album.getSeller_email();
        seller_utaid_stat = album.getSeller_block();
        product_name_stat = album.getProduct_name();

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.messageSeller);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Chat_room.class);
                intent.putExtra("room_name",ProductDetails1.product_name_stat+"@"+ProductDetails1.seller_utaid_stat+"@"+sharedPreferences.getString("user_name","Name"));
                intent.putExtra("user_name",sharedPreferences.getString("email_id","Email"));
                startActivity(intent);
                /*Intent intent2= new Intent(getApplicationContext(),ChatActivity.class);
                startActivity(intent2);
                finish();*/
            }
        });
        productPriceUsd = album.getProduct_price();
        //productId.setText(album.getProduct_id());
        //seller_phone.setText(album.getSeller_phone());
        //seller_email.setText(album.getSeller_email());
        buynowButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ProductDetails1.this, Payment.class);
                startActivity(intent);
            }

        });
    }
}
