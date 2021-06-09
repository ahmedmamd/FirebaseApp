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
    private static final int PICK_IMAGE_REQUEST =72 ;
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
            utils.chooseVideoFromGallery(this);
        });
         binding.playVideo.setOnClickListener(v -> {
             binding.videoShow.setVideoURI(acountViewModell.getUserVideo(this));
             binding.videoShow.start();
         });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            acountViewModell.uploadVideo(AddListOFImages.this,data.getData());
        }
    }
}