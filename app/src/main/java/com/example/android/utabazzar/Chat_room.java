package com.example.android.utabazzar;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.*;
import co.intentservice.chatui.models.ChatMessage;

public class Chat_room extends AppCompatActivity {
    //private Button btn_send_msg;
    //private EditText input_msg;
    //private TextView chat_conversation;
    public ActionBar toolbar;
    private String user_name,room_name, type;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference();
    private String temp_key;
    ChatView chatView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);
        chatView = (ChatView) findViewById(R.id.chat_view);
        //btn_send_msg = (Button) findViewById(R.id.btn_send);
        //input_msg = (EditText) findViewById(R.id.msg_input);
        //chat_conversation = (TextView) findViewById(R.id.textView);

        user_name = getIntent().getExtras().get("user_name").toString();
        Log.e("user_name chatRoom", user_name);
        room_name = getIntent().getExtras().get("room_name").toString();
        try{
            type = getIntent().getExtras().get("type").toString();
        }catch (Exception e){
            type = "some";
        }

        setTitle(room_name);
        if(type.equals("club")){
            root = root.child("club_management").child("clubs").child(room_name).child("messages");
        }else{
            root = FirebaseDatabase.getInstance().getReference().child(room_name);
        }

        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener(){
            @Override
            public boolean sendMessage(co.intentservice.chatui.models.ChatMessage chatMessage) {

                Map<String,Object> map = new HashMap<String, Object>();
                temp_key = root.push().getKey();
                root.updateChildren(map);
                Date date = new Date();
                DatabaseReference message_root = root.child(temp_key);
                Map<String,Object> map2 = new HashMap<String, Object>();
                map2.put("name",user_name);

                map2.put("msg", chatView.getTypedMessage());
                map2.put("time", date.getTime());
                //input_msg.setText("");

                message_root.updateChildren(map2);
                chatView.getInputEditText().setText("");
                return false;
            }

        });

        root.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                append_chat_conversation(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void onBackPressed() {
        if(type.equals("club")) {
            Intent intent = new Intent(Chat_room.this, club_tab.class);
            intent.putExtra("CLUB_NAME", room_name);
            startActivity(intent);
            finish();
            toolbar = getSupportActionBar();
        }
        else {
            Intent intent = new Intent(Chat_room.this, BottomNavigation.class);
            intent.putExtra("CLUB_NAME", room_name);
            startActivity(intent);
            finish();
            toolbar = getSupportActionBar();
        }
    }

    private String chat_msg,chat_user_name, stime;
    private long time;

    private void append_chat_conversation(DataSnapshot dataSnapshot) {

        Iterator i = dataSnapshot.getChildren().iterator();

        while (i.hasNext()){

            chat_msg = (String) ((DataSnapshot)i.next()).getValue();
            chat_user_name = (String) ((DataSnapshot)i.next()).getValue();
            time = (long)((DataSnapshot)i.next()).getValue();


            //chat_conversation.append(chat_user_name +" : "+chat_msg +" \n");
            if(user_name.equals(chat_user_name)){
                co.intentservice.chatui.models.ChatMessage message = new ChatMessage(chat_msg, time, ChatMessage.Type.SENT);
                chatView.addMessage(message);

            }else {
                co.intentservice.chatui.models.ChatMessage message = new ChatMessage(chat_msg, time, ChatMessage.Type.RECEIVED);
                chatView.addMessage(message);
            }
        }


    }
}
