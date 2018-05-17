package com.example.android.utabazzar;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.utabazzar.ui.activity.UserProfileActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




public class ClubManagement extends AppCompatActivity {

    private String m_Text = "";
    public String user_name;
    public Map <String, Boolean> map;
    SharedPreferences sharedPreferences;
    String myPreferences = "MY_PREFERENCES";
    public ActionBar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_management);

        sharedPreferences = this.getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
        user_name=sharedPreferences.getString("user_name","Name");


        final List<String> clubs_list = new ArrayList<String>();

        final List<String> list = new ArrayList<>();

        map = new HashMap<String, Boolean>();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, clubs_list);

        final Toast t = Toast.makeText(this,"You are not allowed in this club", Toast.LENGTH_SHORT);

        // Write a message to the database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("club_management");

        //Read from database
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.child("clubs").getChildren()) {
                    String club_name = ds.getKey();
                    map.put(club_name, false);
                    clubs_list.add(club_name);

                    for(DataSnapshot member: ds.child("members").getChildren()){
                        if(user_name.equals(member.getKey())){
                            map.put(club_name, true);
                            break;
                        }
                        else{
                            map.put(club_name, false);
                        }
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        myRef.addListenerForSingleValueEvent(eventListener);


        final ListView lv = (ListView) findViewById(R.id.lv);
        final Button btn = (Button) findViewById(R.id.btn);


        lv.setAdapter(arrayAdapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputDialog(clubs_list, arrayAdapter, lv, myRef);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                boolean user_is_in_club;
                String club = (String) adapterView.getItemAtPosition(i);
                if(map.get(club)) {


                    Intent intent = new Intent(ClubManagement.this, club_tab.class);
                    //based on item add info to intent
                    intent.putExtra("CLUB_NAME", club);
                    startActivity(intent);
                }
                else{
                    System.out.println("Go away");
                    t.show();
                }
            }
        });

    }

    void InputDialog(final List<String> clubs_list, final ArrayAdapter<String> arrayAdapter, final ListView lv, final DatabaseReference myRef){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Club");

// Set up the input
        final EditText input = new EditText(this);
// Specify the type of input expected; this, for example
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                if(m_Text != "") {
                    clubs_list.add(m_Text);
                    myRef.child("clubs").child(m_Text).setValue(m_Text);
                    myRef.child("clubs").child(m_Text).child("members").child(user_name).child("name").setValue(user_name);
                    map.put(m_Text, true);
                    arrayAdapter.notifyDataSetChanged();
                    // Add to preferences here
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                m_Text = "";
            }
        });

        builder.show();
    }

    public void onBackPressed() {
        Intent intent = new Intent(ClubManagement.this, BottomNavigation.class);
        startActivity(intent);
        finish();
        toolbar = getSupportActionBar();
    }
}
