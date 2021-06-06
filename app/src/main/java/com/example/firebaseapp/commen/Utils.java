package com.example.firebaseapp.commen;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.firebaseapp.modell.Profile;
import com.example.firebaseapp.repositry.Repositry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Utils {
    Repositry repositry;
    Profile profile;
    private FirebaseAuth mAuth;
    String TAG = "signup";

    DatabaseReference databaseReference;
    Context context;
    boolean success;

    //signup using firebase
    public void firebaseAuth(Context context) {
        repositry = new Repositry();
        repositry.signUp.setValue(true);
        profile = new Profile();
        success = false;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
        if (profile.getEmail().toString().equals(null) && profile.getPassword().toString().equals(null)) {
            Log.e("onComplete", "set the value");
        } else {
            mAuth.createUserWithEmailAndPassword(profile.getEmail().trim().toString(), profile.getPassword().trim().toString())
                    .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userId = task.getResult().getUser().getUid();
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                                databaseReference.child("name").setValue(profile.getUserName());
                                databaseReference.child("phone").setValue(profile.getPhoneNum());
                                repositry.signUp.postValue(true);

                                //  updateUI(user);
                            } else {
                                Log.e("onComplete", task.getException().getMessage());
                                repositry.signUp.postValue(false);
                            }


                        }
                    });
        }

    }

}
