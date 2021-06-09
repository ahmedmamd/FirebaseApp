package com.example.firebaseapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1001;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth auth;

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

        acountViewModell.signInWithGoogle(this);
//        signInwithGoogle();
        binding.signInWithGoogle.setOnClickListener(v -> {
             signIn();
       });
    }

     private void forgetPassword() {
            if (isEmailNotValid()){
                Toast.makeText(MainActivity.this , "please enter your email " , Toast.LENGTH_SHORT).show();
                return;
            }else{
                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = binding.userEmail.getText().toString();
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this , "massage sent successfully " ,Toast.LENGTH_SHORT).show();
                                }else
                                    Toast.makeText(MainActivity.this , "an error occurred " , Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        }

     private boolean isFormHasError() {
        boolean isFormHasError = false;
        email = binding.userEmail.getText().toString();
        password =  binding.userPassword.getText().toString();
        if (TextUtils.isEmpty(email)){
            binding.userPassword.setError("please enter your email" );
            binding.userEmail.requestFocus();
            binding.userEmail.setBackgroundResource(R.drawable.focus);
          return   isFormHasError = true ;
        } if (TextUtils.isEmpty(password)){
            binding.userPassword.setError("please enter your password" );
            binding.userPassword.requestFocus();
            binding.userPassword.setBackgroundResource(R.drawable.focus);
           return isFormHasError = true;
        }
            return isFormHasError;
    }

     private boolean isEmailNotValid(){
         boolean isFormHasError = false;
         email = binding.userEmail.getText().toString();
         if (TextUtils.isEmpty(email)){
             binding.userPassword.setError("please enter your email" );
             binding.userEmail.requestFocus();
             binding.userEmail.setBackgroundResource(R.drawable.focus);
             return   isFormHasError = true ;
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
        acountViewModell.getGoogleSignInClientLiveData().observe(this,googleSignInClient -> {
            mGoogleSignInClient = googleSignInClient;
        });

    }

 //test sign in with google
    private void signIn() {
        Log.e(TAG, "signIn: " + mGoogleSignInClient.getSignInIntent());
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);

                Log.e(TAG, "firebaseAuthWithGoogle:"+ account.getId() );
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed"+ e.getMessage());
            }
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }
    // auth firebase with google
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }
    private void updateUI(FirebaseUser user) {
      if (user!=null){
          Intent intent = new Intent(MainActivity.this , Data.class);
          startActivity(intent);
      }
    }
}