package com.example.chat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.chat.R;
import com.example.chat.databinding.ActivitySignInBinding;
import com.example.chat.utilities.Constants;
import com.example.chat.utilities.PreferenceManger;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private PreferenceManger preferenceManger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManger= new PreferenceManger(getApplicationContext());
        binding= ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(preferenceManger.getBoolean(Constants.KEY_IS_SIGNED_IN)){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
        setListener();

    }

    private void setListener(){
        binding.tvCreateSignInActivity.setOnClickListener(v -> {
                startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        });
        binding.btnSignInActivity.setOnClickListener(v -> {
            if(isValidSignIn()){
                signIn();
            }
        });
    }
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private boolean isValidSignIn(){
        if(binding.inputEmailSignInActivity.getText().toString().trim().isEmpty()){
            showToast("Plz enter Email");
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmailSignInActivity.getText().toString().trim()).matches()){
            showToast("Not Valid Email");
            return false;
        }else if(binding.inputPasswordSignInActivity.getText().toString().trim().isEmpty()){
            showToast("Plz enter Password");
            return false;
        }else {
            return true;
        }
    }
    private void signIn(){
        loading(true);
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, binding.inputEmailSignInActivity.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, binding.inputPasswordSignInActivity.getText().toString())
                .get()
                .addOnCompleteListener(task ->{
                   if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){
                       DocumentSnapshot documentSnapshot= task.getResult().getDocuments().get(0);
                       preferenceManger.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                       preferenceManger.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                       preferenceManger.putString(Constants.KEY_NAME, documentSnapshot.getString(Constants.KEY_NAME));
                       preferenceManger.putString(Constants.KEY_IMAGE, documentSnapshot.getString(Constants.KEY_IMAGE));
                       Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                       intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                       startActivity(intent);
                   }else {
                       loading(false);
                       showToast("Enable to signIn");
                   }
                });
    }
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.btnSignInActivity.setVisibility(View.INVISIBLE);
            binding.progressBarSignInActivity.setVisibility(View.VISIBLE);
        }else {
            binding.progressBarSignInActivity.setVisibility(View.INVISIBLE);
            binding.btnSignInActivity.setVisibility(View.VISIBLE);
        }
    }

}