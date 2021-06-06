package com.example.firebaseapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class Utils {
    private final int PICK_IMAGE_REQUEST = 71;

    //choose image from gallery
    public void chooseImageFromGallery(Context context) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult((Activity) context,Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST, Bundle.EMPTY);
    }

    //textView
    @BindingAdapter("notNullText")
    public static void text(TextView textView, String str) {
        textView.setText(str == null || str.isEmpty() || str.trim().isEmpty() || str == "null"?"":str);
    }
}
