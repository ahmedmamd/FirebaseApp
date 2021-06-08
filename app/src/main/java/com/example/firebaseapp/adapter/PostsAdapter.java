package com.example.firebaseapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseapp.R;
import com.example.firebaseapp.databinding.ItemUserPostsBinding;
import com.example.firebaseapp.modell.Post;
import com.example.firebaseapp.viewmodell.AcountViewModell;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostsViewHolder> {
    Context context;
    List<Post> postsList;
    AcountViewModell acountViewModell;
    ImageAdapter imageAdapter;

    public PostsAdapter(Context context, List<Post> postsList) {
        this.context = context;
        this.postsList = postsList;
    }

    @NonNull
    @Override
    public PostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserPostsBinding binding;
        binding = DataBindingUtil.inflate(LayoutInflater.from(context) ,R.layout.item_user_posts, parent , false);
        return new PostsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PostsViewHolder holder, int position) {

        imageAdapter =new ImageAdapter(context,postsList.get(position).getUriImage());
        holder.binding.imgRec.setLayoutManager(new LinearLayoutManager(context,RecyclerView.HORIZONTAL,false));
        holder.binding.imgRec.setAdapter(imageAdapter);

        acountViewModell = new AcountViewModell();
        acountViewModell.getuserName(postsList.get(position).getUserId());
        acountViewModell.getUseNameLiveData().observe((LifecycleOwner) context, userName -> {
            if (userName == null || userName.isEmpty()){
                return;
            }
            postsList.get(position).setUserName(userName);
            holder.binding.setPosts(postsList.get(position));
        });
        acountViewModell.getImageUriLiveData().observe((LifecycleOwner) context, userImage -> {
            if (userImage == null || userImage.isEmpty()){
                return;
            }
            postsList.get(position).setImageUri(userImage);
        });
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class PostsViewHolder extends RecyclerView.ViewHolder {
            public ItemUserPostsBinding binding;
        public PostsViewHolder(@NonNull ItemUserPostsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
