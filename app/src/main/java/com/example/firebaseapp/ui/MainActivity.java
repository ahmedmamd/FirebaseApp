package com.example.firebaseapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.firebaseapp.R;
import com.example.firebaseapp.databinding.ActivityMainBinding;
import com.example.firebaseapp.modell.Profile;
import com.example.firebaseapp.viewmodell.AcountViewModell;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.annotations.PublicApi;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    CallbackManager mCallbackManager;

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 1001;
    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth auth;
    Profile profile = new Profile();
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
        binding.signInWithGoogle.setOnClickListener(v -> {
             signInWithGoogle();
       });
        binding.loginFacebooke.setOnClickListener(v -> {
            signInWithFacebook();
        });

    }

    private void signInWithFacebook() {
       // CallbackManager  mCallbackManager = CallbackManager.Factory.create();
        // Initialize Facebook Login button
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();

        binding.loginFacebooke.setReadPermissions(Arrays.asList("email", "public_profile"));
        binding.loginFacebooke.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.e(TAG, "onCompleted: "+object.toString() );
                                try {
//                                    profile.setEmail(object.getString("email"));
                                    profile.setId( object.getString("id"));
                                    profile.setUserImage("https://graph.facebook.com/"+loginResult.getAccessToken().getUserId()+ "/picture?return_ssl_resources=1");
                                    profile.setUserName(object.getString("name"));
                                    acountViewModell.createUserProfile(MainActivity.this , profile ,null);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).executeAsync();
                Log.e(TAG, "facebook:onSuccess:" + loginResult.toString());
                acountViewModell.handleFacebookAccessToken(MainActivity.this,loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.e(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "facebook:onError", error);
            }
        });

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
    private void signInWithGoogle() {
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
               acountViewModell.firebaseAuthWithGoogle(this,account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Google sign in failed"+ e.getMessage());
            }
        }else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
    private void updateUI(FirebaseUser user) {

      if (user!=null){
          Intent intent = new Intent(MainActivity.this , Data.class);
          startActivity(intent);
      }
    }
}