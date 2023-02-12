package com.example.chat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.chat.adapter.UserAdapter;
import com.example.chat.databinding.ActivityUsersBinding;
import com.example.chat.listeners.UserListener;
import com.example.chat.models.User;
import com.example.chat.utilities.Constants;
import com.example.chat.utilities.PreferenceManger;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersActivity extends BaseActivity implements UserListener {
    private ActivityUsersBinding binding;
    private PreferenceManger preferenceManger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManger= new PreferenceManger(getApplicationContext());
        setListener();
        getUsers();

    }

    private void setListener(){
        binding.imageBackUsersActivity.setOnClickListener(v -> onBackPressed());
    }
    private void getUsers(){
        loading(true);
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserId= preferenceManger.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult() != null){
                        List<User> users= new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                            if (currentUserId.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            User user= new User();
                            user.name= queryDocumentSnapshot.getString(Constants.KEY_NAME);
                            user.email= queryDocumentSnapshot.getString(Constants.KEY_EMAIL);
                            user.image= queryDocumentSnapshot.getString(Constants.KEY_IMAGE);
                            user.token= queryDocumentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            user.id= queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if(users.size() > 0){
                            UserAdapter userAdapter= new UserAdapter(users, this);
                            binding.recyclerviewUsersActivity.setAdapter(userAdapter);
                            binding.recyclerviewUsersActivity.setVisibility(View.VISIBLE);
                        }else {
                            showErrorMessage();
                        }
                    }else {
                        showErrorMessage();
                    }
                });
    }
    private void showErrorMessage(){
        binding.textErrorUsersActivity.setText(String.format("%s", "No user available"));
        binding.textErrorUsersActivity.setVisibility(View.VISIBLE);
    }
    private void loading(boolean isLoading){
        if(isLoading){
            binding.progressBarUsersActivity.setVisibility(View.VISIBLE);
        }else {
            binding.progressBarUsersActivity.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onUserClicked(User user) {
        Intent intent= new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
        finish();
    }
}