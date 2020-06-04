package com.example.mywhatsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.LogOutCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ArrayList<String> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ParseInstallation.getCurrentInstallation().saveInBackground();
//        listViewMain.setAdapter(adapter);
        userList = new ArrayList<>();
        final ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, userList);
        final ListView listViewMain = findViewById(R.id.listViewMain);
        listViewMain.setOnItemClickListener(this);
        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_container);

        try {
            ParseQuery<ParseUser> users = ParseUser.getQuery();
            users.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
            users.findInBackground(new FindCallback<ParseUser>() {
                public void done(List<ParseUser> objects, ParseException e) {
                    if (e == null && objects.size() > 0) {
                        for (ParseUser user : objects) {
                            userList.add(user.getUsername());
                        }
                        listViewMain.setAdapter(adapter);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                try {
                    ParseQuery<ParseUser> users = ParseUser.getQuery();
                    users.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
                    users.whereNotContainedIn("username", userList);
                    users.findInBackground(new FindCallback<ParseUser>() {
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (objects.size() > 0) {
                                if (e == null) {
                                    for (ParseUser user : objects) {
                                        userList.add(user.getUsername());
                                    }
                                    adapter.notifyDataSetChanged();
                                    if (swipeRefreshLayout.isRefreshing()) {
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
                            } else {
                                // Something is wrong
                                //Toast.makeText(getApplicationContext(), e + "", Toast.LENGTH_SHORT).show();
                                if (swipeRefreshLayout.isRefreshing()) {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        }
                    });
                }
                catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.logout_item:
                FancyToast.makeText(getApplicationContext(), ParseUser.getCurrentUser().getUsername() + " is logged out!", Toast.LENGTH_SHORT, FancyToast.DEFAULT, true).show();
                ParseUser.getCurrentUser().logOutInBackground(new LogOutCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });

                break;
        }
        return super.onOptionsItemSelected(item);
        }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(MainActivity.this, ChatActivity.class);
        intent.putExtra("selectedUser", userList.get(position));                // this way we send data to other activity
        startActivity(intent);
    }
}
