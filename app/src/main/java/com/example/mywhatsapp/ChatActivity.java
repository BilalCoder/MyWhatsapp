package com.example.mywhatsapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements View .OnClickListener{
    private ListView chatListView;
    private ArrayList<String> chatList;
    private ArrayAdapter adapter;
    private String selectedUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        selectedUser = getIntent().getStringExtra("selectedUser");
        FancyToast.makeText(getApplicationContext(), "Chat with "+selectedUser, Toast.LENGTH_SHORT, FancyToast.INFO, true).show();
        findViewById(R.id.btnSendMessage).setOnClickListener(this);
        chatListView = findViewById(R.id.listViewMessages);
        chatList = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, chatList);
        chatListView.setAdapter(adapter);

        try {
            ParseQuery<ParseObject> firstUserChatQuery = ParseQuery.getQuery("Chat");
            ParseQuery<ParseObject> secondUserChatQuery = ParseQuery.getQuery("Chat");

            firstUserChatQuery.whereEqualTo("Sender", ParseUser.getCurrentUser().getUsername());
            firstUserChatQuery.whereEqualTo("Receiver", selectedUser);

            secondUserChatQuery.whereEqualTo("Sender", selectedUser);
            secondUserChatQuery.whereEqualTo("Receiver", ParseUser.getCurrentUser().getUsername());

            ArrayList<ParseQuery<ParseObject>> allQueries = new ArrayList<>();
            allQueries.add(firstUserChatQuery);
            allQueries.add(secondUserChatQuery);

            ParseQuery<ParseObject> myQuery = ParseQuery.or(allQueries);
            myQuery.orderByAscending("createAt");

            myQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (objects.size() > 0 && e == null) {
                        for (ParseObject chatObject : objects) {
                            String waMessage = chatObject.get("Message") + "";
                            if (chatObject.get("Sender").equals(ParseUser.getCurrentUser().getUsername())) {
                                waMessage = ParseUser.getCurrentUser().getUsername() + " : " + waMessage;
                            }
                            if (chatObject.get("Sender").equals(selectedUser)) {
                                waMessage = selectedUser + " : " + waMessage;
                            }
                            chatList.add(waMessage);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        final EditText edtMessage = findViewById(R.id.editTextMessage);
        ParseObject chat = new ParseObject("Chat");
        chat.put("Sender", ParseUser.getCurrentUser().getUsername());
        chat.put("Receiver", selectedUser);
        chat.put("Message", edtMessage.getText().toString());
        chat.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    FancyToast.makeText(getApplicationContext(), "Sent to "+selectedUser, Toast.LENGTH_SHORT, FancyToast.SUCCESS, true).show();
                    chatList.add(ParseUser.getCurrentUser().getUsername() + " : " + edtMessage.getText().toString());
                    adapter.notifyDataSetChanged();
                    edtMessage.setText("");
                }
            }
        });

    }
}
