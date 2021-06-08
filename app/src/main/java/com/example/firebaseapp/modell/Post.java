package com.example.firebaseapp.modell;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class Post {

    String id,details,userId,userName ,imageUri;
     List<String> uriImage = new ArrayList<>();

    public List<String> getUriImage() {
        return uriImage;
    }

    public void setUriImage(List<String> uriImage) {
        this.uriImage = uriImage;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
