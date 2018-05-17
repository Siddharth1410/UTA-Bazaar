package com.example.android.utabazzar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClubMembers extends AppCompatActivity {

    private String text_id = "";
    private String text_name = "";
    SharedPreferences sharedPreferences;
    String myPreferences = "MY_PREFERENCES";
    //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
    //String user_ID = myPrefs.getString("", "nothing");
 //   String user_name = preferences.getString("user_name", "User Name");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_members);
        sharedPreferences = this.getSharedPreferences(myPreferences, Context.MODE_PRIVATE);
        String user_name=sharedPreferences.getString("user_name","Name");

        final String thisClubName = getIntent().getStringExtra("CLUB_NAME");

       System.out.println(user_name);

        final List<String> club_member_list = new ArrayList<String>();

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_list_item_1, club_member_list);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("club_management");


        //Read from database club members
        ValueEventListener eventListenerClubMembers = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<String> list = new ArrayList<>();

                DataSnapshot clubMembers = dataSnapshot.child("clubs").child(thisClubName).child("members");
                for(DataSnapshot ds : clubMembers.getChildren()) {
                    String memberID = (String)ds.getKey();
                    String memberName = (String)ds.child("name").getValue();
                    club_member_list.add(memberName + " - " + memberID);
                    //list.add(memberName);
                }
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        myRef.addListenerForSingleValueEvent(eventListenerClubMembers);

        final ListView lv = (ListView) findViewById(R.id.lv);
        final Button btn = (Button) findViewById(R.id.btn);

        lv.setAdapter(arrayAdapter);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputDialog(club_member_list, thisClubName, arrayAdapter, myRef);
            }
        });

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                RemoveMemberConfirmation(adapterView, view, i, l, myRef, arrayAdapter, club_member_list, thisClubName);
                return false;
            }
        });
    }

    void InputDialog(final List<String> club_member_list, final String thisClubName, final ArrayAdapter<String> arrayAdapter, final DatabaseReference myRef){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add member by UTA ID and give them a name");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        // Add a TextView here for the "Title" label, as noted in the comments
        final EditText idBox = new EditText(this);
        idBox.setHint("UTA ID");
        layout.addView(idBox); // Notice this is an add method

        // Add another TextView here for the "Description" label
        final EditText nameBox = new EditText(this);
        nameBox.setHint("Name");
        layout.addView(nameBox); // Another add method

        builder.setView(layout); // Again this is a set method, not add

        // Set up the buttons
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                text_id = idBox.getText().toString();
                text_name = nameBox.getText().toString();
                if(text_id != "" && text_name != "") {
                    myRef.child("clubs").child(thisClubName).child("members").child(text_id).child("name").setValue(text_name);
                    club_member_list.add(text_name + " - " + text_id);
                    arrayAdapter.notifyDataSetChanged();
                    // Add to preferences here
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                text_id = "";
                text_name = "";
            }
        });

        builder.show();
    }

    void RemoveMemberConfirmation(final AdapterView<?> adapterView, View view, final int i, long l, final DatabaseReference myRef, final ArrayAdapter arrayAdapter, final List<String> club_member_list, final String thisClubName){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Remove Member?");


// Set up the buttons
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Object clubMember = adapterView.getItemAtPosition(i);
                String clubMemberInfo = (String)adapterView.getItemAtPosition(i);
                String[] clubMemberArr = clubMemberInfo.split(" ");
                String clubMemberID = clubMemberArr[clubMemberArr.length - 1];
                System.out.println(clubMemberID);
                club_member_list.remove(clubMember);
                arrayAdapter.notifyDataSetChanged();
                myRef.child("clubs").child(thisClubName).child("members").child(clubMemberID).child("name").removeValue();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
