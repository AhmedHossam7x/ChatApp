package com.example.chat.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.chat.adapter.RecentConversionAdapter;
import com.example.chat.databinding.ActivityMainBinding;
import com.example.chat.listeners.ConversionListener;
import com.example.chat.models.ChatMessage;
import com.example.chat.models.User;
import com.example.chat.utilities.Constants;
import com.example.chat.utilities.PreferenceManger;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements ConversionListener {
    private ActivityMainBinding binding;
    private PreferenceManger preferenceManger;
    private List<ChatMessage> conversion;
    private RecentConversionAdapter recentConversionAdapter;
    private FirebaseFirestore database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        preferenceManger= new PreferenceManger(getApplicationContext());
        loadUserDetails();
        getToken();
        setListener();
        listenConversion();

    }

    private void init(){
        conversion= new ArrayList<>();
        recentConversionAdapter= new RecentConversionAdapter(conversion, this);
        binding.conversionRecyclerViewMainActivity.setAdapter(recentConversionAdapter);
        database= FirebaseFirestore.getInstance();
    }
    private void setListener(){
        binding.imageSignOutMainActivity.setOnClickListener(v -> signOut());
        binding.fabMainActivity.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), UsersActivity.class)));
    }
    private void loadUserDetails(){
        binding.textNameMainActivity.setText(preferenceManger.getString(Constants.KEY_NAME));
        byte[] bytes= Base64.decode(preferenceManger.getString(Constants.KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfileMainActivity.setImageBitmap(bitmap);
    }
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private void listenConversion(){
        database.collection(Constants.KEY_COLLECTION_CONVERSION)
                .whereEqualTo(Constants.KEY_SENDER_ID, preferenceManger.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(Constants.KEY_COLLECTION_CONVERSION)
                .whereEqualTo(Constants.KEY_RECEIVER_ID, preferenceManger.getString(Constants.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }
    private final EventListener<QuerySnapshot> eventListener= ((value, error) -> {
        if (error != null){
            return;
        }
        if (value != null){
            for (DocumentChange documentChange: value.getDocumentChanges()){
                if (documentChange.getType() == DocumentChange.Type.ADDED){
                    String senderId= documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    String receiverId= documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    ChatMessage chatMessage= new ChatMessage();
                    chatMessage.senderId= senderId;
                    chatMessage.receiverId= receiverId;
                    if (preferenceManger.getString(Constants.KEY_USER_ID).equals(senderId)){
                        chatMessage.conversionImage= documentChange.getDocument().getString(Constants.KEY_RECEIVER_IMAGE);
                        chatMessage.conversionName= documentChange.getDocument().getString(Constants.KEY_RECEIVER_NAME);
                        chatMessage.conversionId= documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                    }else {
                        chatMessage.conversionImage= documentChange.getDocument().getString(Constants.KEY_SENDER_IMAGE);
                        chatMessage.conversionName= documentChange.getDocument().getString(Constants.KEY_SENDER_NAME);
                        chatMessage.conversionId= documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                    }
                    chatMessage.message= documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                    chatMessage.dataObject= documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                    conversion.add(chatMessage);
                }else if (documentChange.getType() == DocumentChange.Type.MODIFIED){
                    for (int i=0; i<conversion.size(); i++){
                        String senderId= documentChange.getDocument().getString(Constants.KEY_SENDER_ID);
                        String receiverId= documentChange.getDocument().getString(Constants.KEY_RECEIVER_ID);
                        if (conversion.get(i).senderId.equals(senderId) && conversion.get(i).receiverId.equals(receiverId)){
                            conversion.get(i).message= documentChange.getDocument().getString(Constants.KEY_LAST_MESSAGE);
                            conversion.get(i).dataObject= documentChange.getDocument().getDate(Constants.KEY_TIMESTAMP);
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversion, (obj1, obj2) -> obj2.dataObject.compareTo(obj1.dataObject));
            recentConversionAdapter.notifyDataSetChanged();
            binding.conversionRecyclerViewMainActivity.setVisibility(View.VISIBLE);
            binding.conversionRecyclerViewMainActivity.smoothScrollToPosition(0);
            binding.progressBarMainActivity.setVisibility(View.GONE);
        }
    });
    private void getToken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }
    private void updateToken(String token){
        preferenceManger.putString(Constants.KEY_FCM_TOKEN, token);
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        DocumentReference documentReference= database.collection(Constants.KEY_COLLECTION_USERS).document(
                preferenceManger.getString(Constants.KEY_USER_ID)
        );
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
                .addOnFailureListener(e -> showToast("enable update token"));
    }
    private void signOut(){
        showToast("Signing Out...");
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        DocumentReference documentReference=
                database.collection(Constants.KEY_COLLECTION_USERS).document(
                        preferenceManger.getString(Constants.KEY_USER_ID)
                );
        HashMap<String, Object> update= new HashMap<>();
        update.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(update)
                .addOnSuccessListener(unused -> {
                    preferenceManger.clear();
                    startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("Unable SignOut."));
    }

    @Override
    public void onConversionClicked(User user) {
        Intent intent= new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(Constants.KEY_USER, user);
        startActivity(intent);
    }
}