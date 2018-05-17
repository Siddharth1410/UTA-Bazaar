package com.example.android.utabazzar;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class ProductDetails extends AppCompatActivity {

    Button delete, message;
    ImageView imageView;
    TextView producName, productPrice, productId, seller_name, seller_phone, seller_email, seller_block, seller_room, time_period;
    String prod_id;
    String clientToken;
    public static String seller_email_stat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        imageView = (ImageView) findViewById(R.id.image);
        producName = (TextView) findViewById(R.id.product_name);
        productPrice = (TextView) findViewById(R.id.product_price);
        productId = (TextView) findViewById(R.id.productId);
        seller_name = (TextView) findViewById(R.id.seller_name);
        seller_phone = (TextView) findViewById(R.id.seller_phone);
        seller_email = (TextView) findViewById(R.id.seller_email);
        seller_block = (TextView) findViewById(R.id.seller_block);
        seller_room = (TextView) findViewById(R.id.seller_room);
        time_period = (TextView) findViewById(R.id.time_period);


        delete = (Button) findViewById(R.id.delete);

        message = (Button) findViewById(R.id.message);

        Album album = (Album) getIntent().getSerializableExtra("Album");
        Picasso.with(this).load("http://52.90.174.26:8000" + album.getUrl()).into(imageView);
        producName.setText(album.getProduct_name());
        productPrice.setText(album.getProduct_price());
        productId.setText(album.getProduct_id());
        seller_name.setText(album.getSeller_name());
        seller_phone.setText(album.getSeller_phone());
        seller_email.setText(album.getSeller_email());
        seller_block.setText(album.getSeller_block());
        seller_room.setText(album.getSeller_room());
        time_period.setText(album.getTime_period());
        seller_email_stat = album.getSeller_email();
        prod_id = album.getProduct_id();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest stringRequest = new StringRequest(Request.Method.DELETE, "http://52.90.174.26:8000" + "/user/delete/",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Toast.makeText(ProductDetails.this, "Ad Deleted", Toast.LENGTH_SHORT).show();
                                Log.d("Response", response);
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                //this is causing an error
//                            Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show();
                                Log.d("Error.Response", error.toString());
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("id", prod_id);
                        return params;
                    }
                };
                SingletonRequestQueue.getInstance(ProductDetails.this).addToRequestQueue(stringRequest);
            }
        });
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://your-server/client_token", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String clientToken) {
               // this.clie = clientToken;
            }

        });

    }

}
