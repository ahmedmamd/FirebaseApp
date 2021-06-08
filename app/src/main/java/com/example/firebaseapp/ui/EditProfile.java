package com.example.firebaseapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.firebaseapp.R;
import com.example.firebaseapp.Utils.Utils;
import com.example.firebaseapp.databinding.ActivityEditProfileBinding;
import com.example.firebaseapp.modell.Post;
import com.example.firebaseapp.modell.Profile;
import com.example.firebaseapp.viewmodell.AcountViewModell;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class EditProfile extends AppCompatActivity {

    AcountViewModell acountViewModell = new AcountViewModell();
Profile profile;
    Utils utils = new Utils();
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    ActivityEditProfileBinding binding;
//    List<Profile> getProfileList =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this ,R.layout.activity_edit_profile );

        setUpUi();
        setUpObserver();


    }

    private void setUpObserver() {
        acountViewModell = new ViewModelProvider(this).get(AcountViewModell.class);
        acountViewModell.getProfileData(this);
        acountViewModell.getProfileLiveData().observe(this, profile -> {
            if (profile == null ){
                return;
            }else {
                this.profile = profile;
                setUpProfileData();
            }
        });
        acountViewModell.updateProfileLiveData().observe(this,resultMassage -> {
            if (!TextUtils.isEmpty(resultMassage))
                Toast.makeText(this,resultMassage,Toast.LENGTH_SHORT).show();
        });
    }

    private void setUpUi() {
        binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.chooseImageFromGallery(EditProfile.this);
            }
        });
        binding.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               signup();
            }
        });
    }
    private void signup() {
        if (isFormHasError())return;
        profile.setEmail(binding.userEmail.getText().toString());
        profile.setPassword(binding.userPassword.getText().toString());
        profile.setPhoneNum(binding.phone.getText().toString());
        profile.setUserName(binding.name.getText().toString());
        acountViewModell.updateProfile(this,filePath , profile );
    }

    private void setUpProfileData() {
        Picasso.with(this).load(profile.getUserImage())
                .resize(60 ,60).into(binding.imgProfile);
        binding.userEmail.setText(profile.getEmail());
        binding.userPassword.setText(profile.getPassword());
        binding.name.setText(profile.getUserName());
        binding.phone.setText(profile.getPhoneNum());
    }

    private boolean isFormHasError() {
        String phoneNum,userName;
        boolean isFormHasError = false;
        phoneNum = binding.phone.getText().toString();
        userName = binding.name.getText().toString();
       if (TextUtils.isEmpty(phoneNum)){
            binding.phone.setError("please enter your phone" );
            binding.phone.requestFocus();
            binding.phone.setBackgroundResource(R.drawable.focus);
            return   isFormHasError = true ;
        } if (TextUtils.isEmpty(userName)){
            binding.name.setError("please enter your userName" );
            binding.name.requestFocus();
            binding.name.setBackgroundResource(R.drawable.focus);
            return   isFormHasError = true ;
        }
        return isFormHasError;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            Log.e("file Path", ""+filePath );
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                binding.imgProfile.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
    }
}
}