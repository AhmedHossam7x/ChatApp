package com.example.chat.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;


import com.example.chat.databinding.ActivitySignUpBinding;
import com.example.chat.utilities.Constants;
import com.example.chat.utilities.PreferenceManger;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding;
    String encodedImage;
    private PreferenceManger preferenceManger;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManger= new PreferenceManger(getApplicationContext());
        setListeners();

    }

    private void setListeners(){
        binding.tvSignInSignUpActivity.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        });
        binding.btnSignUpActivity.setOnClickListener(v -> {
            if (isValidSignUpDetails()){
                signUp();
            }
        });
        binding.frameLayoutSignUpActivity.setOnClickListener(v ->{
            Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });
    }
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
    private boolean isValidSignUpDetails(){
        if(encodedImage == null){
            showToast("Select Profile image or Name");
            return false;
        }else if(binding.inputNameSignUpActivity.getText().toString().trim().isEmpty()){
            showToast("Plz enter Name");
            return false;
        } else if(binding.inputEmailSignUpActivity.getText().toString().trim().isEmpty()){
            showToast("Plz enter Email");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmailSignUpActivity.getText().toString()).matches()){
            showToast("Not Valid Email");
            return false;
        }else if(binding.inputPasswordSignUpActivity.getText().toString().trim().isEmpty()){
            showToast("Plz enter Password");
            return false;
        }else if(!(binding.inputPasswordSignUpActivity.getText().toString().length() ==6)){
            showToast("Must be password 6 or higher");
            return false;
        } else if(binding.inputConfirmPasswordSignUpActivity.getText().toString().trim().isEmpty()){
            showToast("Plz enter Confirm Password");
            return false;
        }else if(!binding.inputPasswordSignUpActivity.getText().toString().trim().equals(binding.inputConfirmPasswordSignUpActivity.getText().toString().trim())){
            showToast("Password & confirm must be same");
            return false;
        }else {
            return true;
        }
    }
    private void signUp(){
        loading(true);
        FirebaseFirestore database= FirebaseFirestore.getInstance();
        HashMap<String, Object> data= new HashMap<>();
        data.put(Constants.KEY_NAME, binding.inputNameSignUpActivity.getText().toString());
        data.put(Constants.KEY_EMAIL, binding.inputEmailSignUpActivity.getText().toString());
        data.put(Constants.KEY_PASSWORD, binding.inputPasswordSignUpActivity.getText().toString());
        data.put(Constants.KEY_IMAGE, encodedImage);
        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManger.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManger.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManger.putString(Constants.KEY_NAME, binding.inputNameSignUpActivity.getText().toString());
                    preferenceManger.putString(Constants.KEY_IMAGE, encodedImage);
                    Intent intent= new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(exception ->{
                    loading(false);
                    showToast(exception.getMessage());
                });
    }
    private  String encodeImage(@NonNull Bitmap bitmap){
        int previewWidth= 150;
        int previewHeight= bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap= Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes= byteArrayOutputStream.toByteArray();

        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    private final ActivityResultLauncher<Intent> pickImage= registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri= result.getData().getData();
                        try {
                            InputStream inputStream= getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                            binding.imageProFiloSignUpActivity.setImageBitmap(bitmap);
                            binding.textAddImageSignUpActivity.setVisibility(View.GONE);
                            encodedImage= encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private void loading(Boolean isLoading){
        if(isLoading){
            binding.btnSignUpActivity.setVisibility(View.INVISIBLE);
            binding.progressBarSignUpActivity.setVisibility(View.VISIBLE);
        }else {
            binding.progressBarSignUpActivity.setVisibility(View.INVISIBLE);
            binding.btnSignUpActivity.setVisibility(View.VISIBLE);
        }
    }
}