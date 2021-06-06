package com.example.firebaseapp.repositry;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class Repositry {

    public Repositry() {

    }

    public MutableLiveData<Boolean> signUp = new MutableLiveData<>();

    public LiveData<Boolean> signUpLiveData(){

        return signUp;
    }


}
