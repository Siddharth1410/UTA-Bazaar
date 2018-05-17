package com.example.android.utabazzar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;
import com.example.android.utabazzar.ui.activity.UserProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class ChatActivity extends AppCompatActivity {
    private Button  add_room;
    public ActionBar toolbar;
    private EditText room_name;
    SharedPreferences sharedPreferences;
    String myPreferences = "MY_PREFERENCES";
    String emailId = "email_id";
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private String name, utaID;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);
        sharedPreferences = ChatActivity.this.getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
        listView = (ListView) findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_rooms);

        listView.setAdapter(arrayAdapter);

        name = sharedPreferences.getString("email_id","Email");
        utaID = sharedPreferences.getString("user_name","Name");
        //request_user_name();

        Map<String,Object> map = new HashMap<String, Object>();
        map.put(ProductDetails1.product_name_stat+"@"+ProductDetails1.seller_utaid_stat+"@"+utaID,"");
        root.updateChildren(map);

        Intent intent = new Intent(getApplicationContext(),Chat_room.class);
        intent.putExtra("room_name",ProductDetails1.product_name_stat+"@"+ProductDetails1.seller_utaid_stat+"@"+utaID);
        intent.putExtra("user_name",name);
        startActivity(intent);
        /*
        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String,Object> map = new HashMap<String, Object>();
                map.put(room_name.getText().toString()+"; "+ProductDetails1.seller_email_stat+" ; "+name,"");
                root.updateChildren(map);

            }
        });
        */

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<String>();
                Iterator i = dataSnapshot.getChildren().iterator();
                String temp;
                while (i.hasNext()){
                    if(((DataSnapshot)i.next()).getKey().contains(utaID)){
                        set.add(((DataSnapshot)i.next()).getKey());
                    }
                }

                list_of_rooms.clear();
                list_of_rooms.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(),Chat_room.class);
                intent.putExtra("room_name",((TextView)view).getText().toString() );
                intent.putExtra("user_name",name);
                startActivity(intent);
            }
        });

    }

    public void onBackPressed() {
        Intent intent = new Intent(ChatActivity.this, BottomNavigation.class);
        startActivity(intent);
        finish();
        toolbar = getSupportActionBar();
    }

    private void request_user_name() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name:");

        final EditText input_field = new EditText(this);

        builder.setView(input_field);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                name = input_field.getText().toString();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_user_name();
            }
        });

        builder.show();

    }

}
