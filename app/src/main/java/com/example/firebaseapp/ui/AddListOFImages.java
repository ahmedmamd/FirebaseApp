package com.example.firebaseapp.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.firebaseapp.R;
import com.example.firebaseapp.Utils.Utils;
import com.example.firebaseapp.databinding.ActivityAddListOFImagesBinding;
import com.example.firebaseapp.viewmodell.AcountViewModell;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AddListOFImages extends AppCompatActivity {
    AcountViewModell acountViewModell = new AcountViewModell();
    Utils utils;
 ActivityAddListOFImagesBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this , R.layout.activity_add_list_o_f_images);
        setUPUi();
    }

    private void setUPUi() {
        utils =new Utils();
        binding.addList.setOnClickListener(v -> {
            utils.addListOfImages(this);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandleResult(requestCode,resultCode ,data,100)){
            ArrayList<Image> images = ImagePicker.getImages(data);

           // Log.e("onActivityResult", images.get(3).component4());
            Log.e("onActivityResult", images.toString());
            for (int path=0 ;path< images.size(); path++){
                 acountViewModell.uploadMultiImage(this , images.get(path).component3());
              //  Picasso.with(this).load(image.getUri()).into(binding.imgView);
            }
        }
    }
}