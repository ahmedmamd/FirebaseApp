package com.example.firebaseapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.firebaseapp.R;
import com.example.firebaseapp.databinding.ActivityMainBinding;
import com.example.firebaseapp.viewmodell.AcountViewModell;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    AcountViewModell acountViewModell;
    String email ;
    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this , R.layout.activity_main);
        setUpObserver();
        setUpUi();



    }
    private void setUpUi() {
        binding.signUP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , SignUp.class);
                startActivity(intent);
            }
        });
        binding.loginBtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormHasError())
                    return;
                acountViewModell.signIn(MainActivity.this ,email ,password);
            }
        });
        binding.forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPassword();
            }
        });
    }

    private void forgetPassword() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = binding.userEmail.getText().toString();

        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("forgetPassword", "Email sent.");
                        }
                    }
                });
    }

    private boolean isFormHasError() {
        boolean isFormHasError = false;
        email = binding.userEmail.getText().toString();
        password =  binding.userPassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            binding.userPassword.setError("please enter your email" );
            binding.userEmail.requestFocus();
            binding.userEmail.setBackgroundResource(R.drawable.bordercolor);
          return   isFormHasError = true ;
        } if (TextUtils.isEmpty(password)){
            binding.userPassword.setError("please enter your password" );
            binding.userPassword.requestFocus();
            binding.userPassword.setBackgroundResource(R.drawable.bordercolor);
           return isFormHasError = true;
        }
            return isFormHasError;
    }

    private void setUpObserver() {
        acountViewModell = new ViewModelProvider(this).get(AcountViewModell.class);
        acountViewModell.observeSignIn().observe(this ,  signIn -> {
            if (!TextUtils.isEmpty(signIn)){
                if (signIn.equals("signIn success")){
                    Toast.makeText(this , signIn , Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this , Data.class);
                    startActivity(intent);
                }else
                    Toast.makeText(this , signIn , Toast.LENGTH_SHORT).show();

            }
        } );
    }


}