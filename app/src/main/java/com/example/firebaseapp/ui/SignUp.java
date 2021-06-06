package com.example.firebaseapp.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

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
import com.example.firebaseapp.databinding.ActivitySignUpBinding;
import com.example.firebaseapp.modell.Profile;
import com.example.firebaseapp.ui.Data;
import com.example.firebaseapp.viewmodell.AcountViewModell;

import java.io.IOException;

public class SignUp extends AppCompatActivity {

    AcountViewModell acountViewModell;
    Profile profile;
    ActivitySignUpBinding binding;
    String TAG = "signup";
    private final int PICK_IMAGE_REQUEST = 71;
     Uri filePath;
     Utils utils =new Utils();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);

        setUpUi();
        setUpObserver();
    }

    private void setUpUi() {
        binding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormHasError())return;
                signup();
            }
        });
        binding.signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this , MainActivity.class));
            }
        });
        binding.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.chooseImageFromGallery(SignUp.this);
            }
        });
    }

    private void signup() {
        profile = new Profile();
        profile.setEmail(binding.userEmail.getText().toString());
        profile.setPassword(binding.userPassword.getText().toString());
        profile.setPhoneNum(binding.phone.getText().toString());
        profile.setUserName(binding.name.getText().toString());
        acountViewModell.createUserAccount(this, profile.getEmail(), profile.getPassword());
    }

    private void setUpObserver() {
        acountViewModell = new ViewModelProvider(this).get(AcountViewModell.class);
        acountViewModell.observeUserId().observe(this, userId -> {
            profile.setId(userId);
            acountViewModell.createUserProfile(this, profile,filePath);
        });
        acountViewModell.observeCreationProfile().observe(this, resultMessage -> {
            if (!TextUtils.isEmpty(resultMessage))
                Toast.makeText(this, resultMessage, Toast.LENGTH_SHORT).show();
                navToDataActivity();
        });
    }

    private void navToDataActivity() {
        startActivity(new Intent(this,Data.class));
    }

    private boolean isFormHasError() {
        String email , password ,phoneNum,userName;
        boolean isFormHasError = false;
        email = binding.userEmail.getText().toString();
        password = binding.userPassword.getText().toString();
        phoneNum = binding.phone.getText().toString();
        userName = binding.name.getText().toString();
        if (TextUtils.isEmpty(email)){
            binding.userEmail.setError("please enter your post" );
            binding.userEmail.requestFocus();
            binding.userEmail.setBackgroundResource(R.drawable.bordercolor);
            return   isFormHasError = true ;
        } if (TextUtils.isEmpty(password)){
            binding.userPassword.setError("please enter your post" );
            binding.userPassword.requestFocus();
            binding.userPassword.setBackgroundResource(R.drawable.bordercolor);
            return   isFormHasError = true ;
        } if (TextUtils.isEmpty(phoneNum)){
            binding.phone.setError("please enter your post" );
            binding.phone.requestFocus();
            binding.phone.setBackgroundResource(R.drawable.bordercolor);
            return   isFormHasError = true ;
        } if (TextUtils.isEmpty(userName)){
            binding.name.setError("please enter your post" );
            binding.name.requestFocus();
            binding.name.setBackgroundResource(R.drawable.bordercolor);
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
