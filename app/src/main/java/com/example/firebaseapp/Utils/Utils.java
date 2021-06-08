package com.example.firebaseapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.firebaseapp.R;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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
// binding imageview
    @BindingAdapter("imageBinding")
    public static void bindUser(ImageView view, String imageUrl) {
        Picasso.with(view.getContext())
                .load(imageUrl).fit()
                .placeholder(R.drawable.name)
                .into(view);
    }
    //textView
    @BindingAdapter("notNullText")
    public static void text(TextView textView, String str) {
        textView.setText(str == null || str.isEmpty() || str.trim().isEmpty() || str == "null"?"":str);
    }
    //Add list of images
    public void addListOfImages(Context context){
        ArrayList<Image> images = new ArrayList();
        ImagePicker.with((Activity) context)
                .setFolderMode(true)
                .setFolderTitle("Album")
                .setDirectoryName("Image Picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(10)
                .setLimitMessage("You can select up to 10 images")
                .setSelectedImages(images)
                .setRequestCode(100)
                .start();
    }
}
