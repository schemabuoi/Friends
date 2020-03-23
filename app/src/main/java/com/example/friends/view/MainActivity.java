package com.example.friends.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.friends.R;
import com.example.friends.entity.Friend;
import com.example.friends.viewmodel.FriendViewModel;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FriendViewModel friendViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeRecyclerView();
        initializeAddFriendButton();
    }

    private void initializeRecyclerView()
    {
        RecyclerView recFriends = findViewById(R.id.recFriends);
        final FriendAdapter adapter = new FriendAdapter();
        recFriends.setLayoutManager(new LinearLayoutManager(this));
        recFriends.setHasFixedSize(true);
        recFriends.setAdapter(adapter);

        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        friendViewModel.initialize(getApplication());
        friendViewModel.getAll().observe(this, new Observer<List<Friend>>() {
            @Override
            public void onChanged(List<Friend> friends) {
                adapter.setFriendList(friends);
            }
        });
    }

    private void initializeAddFriendButton()
    {
        MaterialButton addFriend = findViewById(R.id.btnAddFriend);
        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddFriendActivity();
            }
        });

    }

    private void startAddFriendActivity()
    {
        Intent intent = new Intent(this, AddFriendActivity.class);
        startActivity(intent);
    }

}
