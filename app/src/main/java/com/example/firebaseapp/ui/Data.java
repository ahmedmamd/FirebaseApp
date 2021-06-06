package com.example.firebaseapp.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.firebaseapp.R;
import com.example.firebaseapp.adapter.PostsAdapter;
import com.example.firebaseapp.databinding.ActivityDataBinding;
import com.example.firebaseapp.modell.Post;
import com.example.firebaseapp.viewmodell.AcountViewModell;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class Data extends AppCompatActivity {
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
            if (posts == null || posts.isEmpty()){
              postList.clear();
              postsAdapter.notifyDataSetChanged();
              return;
            }
            postList.clear();
            postList.addAll(posts);
            postsAdapter.notifyDataSetChanged();
            Log.e("getPostsLiveData", "setUpObserver: post data");
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

                 }
                 return false;
             }
         });

        binding.addPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isFormHasError())return;
                addpost();
            }
        });
        acountViewModell.getPosts();
    }

    private void signout() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signOut();
        if (firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(Data.this , MainActivity.class));
             Toast.makeText(this , "logout complete",Toast.LENGTH_SHORT).show();
        }
    }

    private void addpost() {
        acountViewModell.addPost(this , binding.textPost.getText().toString() );
    }
    private boolean isFormHasError() {
        boolean isFormHasError = false;
        addPost = binding.textPost.getText().toString();
        if (TextUtils.isEmpty(addPost)){
            binding.textPost.setError("please enter your post" );
            binding.textPost.requestFocus();
            binding.textPost.setBackgroundResource(R.drawable.bordercolor);
            return   isFormHasError = true ;
        }
        return isFormHasError;
    }


}