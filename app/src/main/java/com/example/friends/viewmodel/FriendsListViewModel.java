package com.example.friends.viewmodel;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.friends.entity.Friend;
import com.example.friends.model.FriendRepository;

import java.util.List;

public class FriendsListViewModel extends ViewModel {

    private FriendRepository repository;
    private LiveData<List<Friend>> allFriends;

    public void initialize(Application application)
    {
        if(repository == null)
        {
            repository = new FriendRepository(application);
            allFriends = repository.getAll();
        }
    }

    public LiveData<List<Friend>> getAllFriends()
    {
        return allFriends;
    }
}
