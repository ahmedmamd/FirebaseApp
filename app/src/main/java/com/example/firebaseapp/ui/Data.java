package com.example.firebaseapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.firebaseapp.R;
import com.example.firebaseapp.Utils.Utils;
import com.example.firebaseapp.adapter.PostsAdapter;
import com.example.firebaseapp.databinding.ActivityDataBinding;
import com.example.firebaseapp.modell.Post;
import com.example.firebaseapp.viewmodell.AcountViewModell;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.nguyenhoanglam.imagepicker.model.Image;
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker;

import java.util.ArrayList;
import java.util.List;

public class Data extends AppCompatActivity {
    List<String > image =new ArrayList<>();
    Utils utils =new Utils();
    AcountViewModell acountViewModell;
    ActivityDataBinding binding;
     String addPost;
     PostsAdapter postsAdapter ;
     List<Post> postList =new ArrayList<>();
      Post post = new Post();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this , R.layout.activity_data);
        setUpObserver();
        setUpUi();

    }

    private void setUpObserver() {
        acountViewModell = new ViewModelProvider(this).get(AcountViewModell.class);
        acountViewModell.observepostAdded().observe(this , post -> {
           if (!TextUtils.isEmpty(post)){
               Toast.makeText(this , post , Toast.LENGTH_SHORT).show();
           }
        });
        acountViewModell.getPostsLiveData().observe(this , posts -> {
            if (posts == null || posts.isEmpty()) {
                postList.clear();
                postsAdapter.notifyDataSetChanged();
                return;
            }
            postList.clear();
            postList.addAll(posts);
            postsAdapter.notifyDataSetChanged();
        });
        acountViewModell.addImagesLiveData().observe(this ,s -> {
            image.add(s);
            Toast.makeText(this,"image added successfully ",Toast.LENGTH_SHORT).show();
        });
    }
    private void setUpUi() {
        postsAdapter  = new PostsAdapter(this ,postList );
        binding.postsRec.setLayoutManager(new LinearLayoutManager(this));
        binding.postsRec.setAdapter(postsAdapter);
        binding.toolbar.setTitle("posts ");
        binding.toolbar.inflateMenu(R.menu.setting_menu);
        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
             @Override
             public boolean onMenuItemClick(MenuItem item) {
                 if(item.getItemId()==R.id.actionEditProfile)
                 {
                     startActivity(new Intent(Data.this , EditProfile.class));
                 }
                 else if(item.getItemId()== R.id.action_settings)
                 {
                  Toast.makeText(Data.this ,"no stting here ",Toast.LENGTH_SHORT).show();
                 }else if (item.getItemId()==R.id.logout){
                    signout();
                 }else if (item.getItemId()==R.id.addList){
                     startActivity(new Intent(Data.this , AddListOFImages.class));
                 }
                 return false;
             }
         });

        binding.addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormHasError())return;
                addpost(image);
            }
        });
        acountViewModell.getPosts();
        binding.listImage.setOnClickListener(v -> {
            utils.addListOfImages(this);
        });
    }

    private void signout() {
             FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
             firebaseAuth.signOut();
        if (firebaseAuth.getCurrentUser() == null){
             startActivity(new Intent(Data.this , MainActivity.class));
             Toast.makeText(this , "logout complete",Toast.LENGTH_SHORT).show();
        }
    }

    private void addpost(List<String >uri) {
        acountViewModell.addPost(this , binding.textPost.getText().toString(),uri );
        binding.textPost.setText("");
    }
    private boolean isFormHasError() {
        boolean isFormHasError = false;
        addPost = binding.textPost.getText().toString();
        if (TextUtils.isEmpty(addPost)){
            binding.textPost.setError("please enter your post" );
            binding.textPost.requestFocus();
            binding.textPost.setBackgroundResource(R.drawable.focus);
            return   isFormHasError = true ;
        }
        return isFormHasError;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ImagePicker.shouldHandleResult(requestCode,resultCode ,data,100)){
            ArrayList<Image> images = ImagePicker.getImages(data);
            Log.e("onActivityResult", images.toString());
            for (int path=0 ;path< images.size(); path++){
               acountViewModell.uploadMultiImage(this , images.get(path).component3());
            }
        }
    }

}