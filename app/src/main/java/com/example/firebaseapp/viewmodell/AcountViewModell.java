package com.example.firebaseapp.viewmodell;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.firebaseapp.R;
import com.example.firebaseapp.base.BaseViewModell;
import com.example.firebaseapp.modell.Post;
import com.example.firebaseapp.modell.Profile;
import com.example.firebaseapp.ui.MainActivity;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class AcountViewModell extends BaseViewModell {

    FirebaseStorage storage;
    StorageReference storageReference;
    Uri uriVideo;
    private final int PICK_IMAGE_REQUEST = 71;
    Post post;
    Profile profile ;
    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    GoogleSignInClient mGoogleSignInClient;

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData = new MutableLiveData<>();
    public LiveData<FirebaseUser> getFirebaseUserLiveData() {
        return getFirebaseUserMutableLiveData;
    }

    public MutableLiveData<List> getPostsMutableLiveData = new MutableLiveData<>();
    public LiveData<List> getPostsLiveData() {
        return getPostsMutableLiveData;
    }

    public MutableLiveData<GoogleSignInClient> getGoogleSignInClientMutableLiveData = new MutableLiveData<>();
    public LiveData<GoogleSignInClient> getGoogleSignInClientLiveData() {
        return getGoogleSignInClientMutableLiveData;
    }

    public MutableLiveData<String> addImagesMutableLiveData = new MutableLiveData<>();
    public LiveData<String>  addImagesLiveData() {
        return addImagesMutableLiveData;
    }

    public MutableLiveData<String> getImageUriMutableLiveData = new MutableLiveData<>();
    public LiveData<String > getImageUriLiveData() {
        return getImageUriMutableLiveData;
    }

    public MutableLiveData<String > updateProfileMutableLiveData = new MutableLiveData<>();
    public LiveData<String> updateProfileLiveData() {
        return updateProfileMutableLiveData;
    }

    public MutableLiveData<Profile> getProfileMutableLiveData = new MutableLiveData<>();
    public LiveData<Profile> getProfileLiveData() {
        return getProfileMutableLiveData;
    }

    public MutableLiveData<String> getUserNameMutableLiveData = new MutableLiveData<>();
    public LiveData<String> getUseNameLiveData() {
        return getUserNameMutableLiveData;
    }

    public MutableLiveData<String> userIdLiveData = new MutableLiveData<>();
    public LiveData<String> observeUserId() {
        return userIdLiveData;
    }

    public MutableLiveData<String> creationProfileLiveData = new MutableLiveData<>();
    public LiveData<String> observeCreationProfile() {
        return creationProfileLiveData;
    }

    public MutableLiveData<String> signInLivaData = new MutableLiveData<>();
    public LiveData<String> observeSignIn() {
        return signInLivaData;
    }

    public MutableLiveData<String> addPostLivaData = new MutableLiveData<>();
    public LiveData<String> observepostAdded() {
        return addPostLivaData;
    }

    public AcountViewModell() {

    }

// create an account
    public void createUserAccount(Context context, String email, String password) {
        mAuth = FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("isSuccessful", "createUserWithEmail:success");
                            String userId = task.getResult().getUser().getUid();
                            userIdLiveData.setValue(userId);
                        } else {
                            Log.e("createUserAccount","failed: " + task.getException().getMessage());
                        }
                    }
                });
    }
    //create profile
    public void createUserProfile(Context context,Profile profile,Uri filePath) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyUID",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("userId" , profile.getId());
        myEdit.commit();
        uploadImage(context,filePath);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(profile.getId());
        databaseReference.setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    creationProfileLiveData.setValue("User Creation Successfully");
                }else{
                    creationProfileLiveData.setValue("User Creation Failed");
                    Log.e("createUserProfile","failed: " + task.getException().getMessage());
                }
            }
        });
    }
    //signin
    public void signIn(Context context, String email, String password){
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyUID",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(email , password)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String userId = task.getResult().getUser().getUid();
                            myEdit.putString("userId" , userId);
                            myEdit.commit();

//                            userIdLiveData.setValue(userId);
                            Log.d("signIn", "signInUserWithEmail:success"+userId);
                            signInLivaData.setValue("signIn success");
                        }else{
                            Log.d("signIn", "signInUserWithEmail: failed"+task.getException().getMessage());
                            signInLivaData.postValue("signIn failed");
                        }

                    }
                });

    }
    //add post
    public void  addPost(Context context , String setpost ,List<String> uriImages){
        SharedPreferences prefs = context.getSharedPreferences("MyUID",MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts").push();
        post = new Post();
        post.setId(databaseReference.getKey());
        post.setDetails(setpost);
        post.setUserId(prefs.getString("userId",""));
        post.setUriImage(uriImages);
        Log.d("addPost", "add post successFully with UID");
        databaseReference.setValue(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
             if (task.isSuccessful()){
                 addPostLivaData.setValue("User add post Successfully");
             }else{
                 addPostLivaData.setValue("User add post Failed");
                 Log.e("createUserProfile","failed: " + task.getException().getMessage());
             }
            }
        });
    }
    // get list of posts
    public void getPosts(){
        List<Post> postsList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    postsList.clear();
                for (DataSnapshot postSnapshot: snapshot.getChildren()) {
                    post = postSnapshot.getValue(Post.class);
                    postsList.add(post);
                }
                getPostsMutableLiveData.postValue(postsList);
                Log.e("getPosts", "postsList: "+postsList.size());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
    //get data profile to set in edit profile
    public void getProfileData(Context context){
        SharedPreferences prefs = context.getSharedPreferences("MyUID",MODE_PRIVATE);
        String userId = prefs.getString("userId","");
         profile = new Profile();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    profile = snapshot.getValue(Profile.class);
                    getProfileMutableLiveData.postValue(profile);
          }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
// update profile data
    public void updateProfile(Context context,Uri filePath,Profile profile){
        SharedPreferences prefs = context.getSharedPreferences("MyUID",MODE_PRIVATE);
        String userId = prefs.getString("userId","");
        uploadImage(context,filePath);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        databaseReference.setValue(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateProfileMutableLiveData.setValue("User update Successfully");
                }else{
                    updateProfileMutableLiveData.setValue("User update Failed");
                    Log.e("createUserProfile","failed: " + task.getException().getMessage());
                }
            }
        });
    }
    //get user name and and user Image
    public void getuserName(String userId){
        profile =new Profile();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                    profile = snapshot.getValue(Profile.class);
                    getUserNameMutableLiveData.postValue(profile.getUserName());
                    getImageUriMutableLiveData.postValue(profile.getUserImage());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    //upload image
    public void uploadImage(Context context ,Uri filePath ) {
        SharedPreferences prefs = context.getSharedPreferences("MyUID",MODE_PRIVATE);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if(filePath != null)
        {
            Log.e("uploadImage", filePath.toString() );
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Users").child("image"+ prefs.getString("userId",""));
            ref.putFile(filePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    progressDialog.dismiss();
                    Log.e("upload ", "success" );
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        imageUrl(context , downloadUri.toString());
                        Log.e("getDownloadUrl",downloadUri.getPath());
                    } else {
                        Log.e("getDownloadUrl", "failed to get Url" );
                    }
                }
            });
        }
    }
    //add image url inside userId
    public void imageUrl(Context context ,String uri){
    SharedPreferences prefs = context.getSharedPreferences("MyUID",MODE_PRIVATE);
    databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(prefs.getString("userId","")).child("userImage");
    databaseReference.setValue(uri).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if (task.isSuccessful()){
                Log.e("add image", "success ");
            }else{
                Log.e("add image", "failed");
            }

        }
    });
}

    public void uploadMultiImage(Context context ,Uri filePath ) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Images").child(""+System.currentTimeMillis());
            ref.putFile(filePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    progressDialog.dismiss();
                    Log.e("upload ", "success" );
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task){
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.e("downloadUri", downloadUri.toString());
                        addImagesMutableLiveData.postValue(downloadUri.toString());
                    } else {
                        Log.e("getDownloadUrl", "failed to get Url" );
                    }
                }
            });
        }
    }

    public void uploadVideo(Context context ,Uri filePath ) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("Video").child(""+System.currentTimeMillis());
            ref.putFile(filePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    progressDialog.dismiss();
                    Log.e("upload ", "success" );
                    // Continue with the task to get the download URL
                    return ref.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task){
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        videoUrl(context , downloadUri.toString());
                        Log.e("downloadUri", downloadUri.toString());
                    } else {
                        Log.e("getDownloadUrl", "failed to get Url" );
                    }
                }
            });
        }
    }

    public void videoUrl(Context context ,String uri){
        SharedPreferences prefs = context.getSharedPreferences("MyUID",MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(prefs.getString("userId","")).child("userVideo");
        databaseReference.setValue(uri).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Log.e("add video", "success ");
                }else{
                    Log.e("add video", "failed");
                }
            }
        });
}
//get the uri for video
     public Uri getUserVideo(Context context){
        SharedPreferences prefs = context.getSharedPreferences("MyUID",MODE_PRIVATE);
        profile =new Profile();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(prefs.getString("userId",""));
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profile = snapshot.getValue(Profile.class);
                uriVideo = Uri.parse(profile.getUserVideo());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return uriVideo;
    }
// google signIn
        public void signInWithGoogle(Context context){
            mAuth =FirebaseAuth.getInstance();
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(context.getString(R.string.default_web_client_id))
                    .requestServerAuthCode(context.getString(R.string.default_web_client_id))
                    .requestId()
                    .requestEmail()
                    .build();

            mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

            getGoogleSignInClientMutableLiveData.setValue(mGoogleSignInClient);

        }
       public void googleSignOut(Context context){

        }
// oauth with google
    public void firebaseAuthWithGoogle(Context context,String idToken) {
        profile = new Profile();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            profile.setEmail(task.getResult().getUser().getEmail());
                            profile.setPhoneNum(task.getResult().getUser().getPhoneNumber());
                            profile.setId(task.getResult().getUser().getUid());
                            profile.setUserImage(task.getResult().getUser().getPhotoUrl().toString());
                            profile.setUserName(task.getResult().getUser().getDisplayName());
                            createUserProfile(context , profile ,null);
                            Log.d("", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            signInLivaData.setValue("signIn success");
                           // updateUI(user);
                        } else {
                            signInLivaData.postValue("signIn failed");
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            updateUI(null);
                        }
                    }
                });
    }
//oauth with facebook
    public void handleFacebookAccessToken(Context context,AccessToken token) {
        profile = new Profile();
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) context , new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
//                            profile.setEmail(task.getResult().getUser().getEmail());
//                            profile.setPhoneNum(task.getResult().getUser().getPhoneNumber());
//                            profile.setId(task.getResult().getUser().getUid());
//                            profile.setUserImage(task.getResult().getUser().getPhotoUrl().toString());
//                            profile.setUserName(task.getResult().getUser().getDisplayName());
//                            createUserProfile(context , profile ,null);
                            // Sign in success, update UI with the signed-in user's information
                            Log.e("firebaseAuthFacebboce", "signInWithCredential:success");
                            signInLivaData.setValue("signIn success");
                        } else {
                            signInLivaData.postValue("signIn failed");
                            // If sign in fails, display a message to the user.
                            Log.e("firebaseAuthFacebboce", "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }
}


