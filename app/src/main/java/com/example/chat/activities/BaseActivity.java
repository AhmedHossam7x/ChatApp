package com.example.chat.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.chat.utilities.Constants;
import com.example.chat.utilities.PreferenceManger;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {

    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManger preferenceManger= new PreferenceManger(getApplicationContext());
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        documentReference= database.collection(Constants.KEY_COLLECTION_USERS)
                .document(preferenceManger.getString(Constants.KEY_USER_ID));

    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(Constants.KEY_AVAILABILITY, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(Constants.KEY_AVAILABILITY, 1);
    }
}
