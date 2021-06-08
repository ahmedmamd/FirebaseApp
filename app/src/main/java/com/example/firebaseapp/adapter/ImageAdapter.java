package com.example.firebaseapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firebaseapp.R;
import com.example.firebaseapp.databinding.ItemListImageBinding;
import com.example.firebaseapp.modell.Post;
import com.example.firebaseapp.viewmodell.AcountViewModell;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    Context context;
    List<String > imageList;

    public ImageAdapter(Context context, List<String> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemListImageBinding binding;
        binding = DataBindingUtil.inflate(LayoutInflater.from(context) , R.layout.item_list_image, parent , false);
        return new ImageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Picasso.with(context).load(imageList.get(position)).into(holder.binding.imgList);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {
        public ItemListImageBinding binding;
        public ImageViewHolder(@NonNull ItemListImageBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
