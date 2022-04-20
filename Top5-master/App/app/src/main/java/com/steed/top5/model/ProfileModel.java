package com.steed.top5.model;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.steed.top5.pojo.SaveResponse;
import com.steed.top5.pojo.User;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class ProfileModel {
    private String TAG = "ProfileModel";
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();
    // Create a storage reference from our app
    private StorageReference storageRef = storage.getReference();

    public  MutableLiveData<String> uploadProfilePhoto(String profilePhoto) {
        MutableLiveData<String> downloadURL = new MutableLiveData<>();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null && !TextUtils.isEmpty(profilePhoto)) {
            String uid = firebaseUser.getUid();
            StorageReference profilePhotoRef = storageRef.child(String.format("content/profile_photos/%s.jpg",uid));
            try {
                InputStream stream = new FileInputStream(new File(profilePhoto));
                final UploadTask uploadTask = profilePhotoRef.putStream(stream);
                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        exception.printStackTrace();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        final StorageMetadata metadata = taskSnapshot.getMetadata();

                        profilePhotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String url = uri.toString();
                                Log.i(TAG, "Download URL "+url);
                                downloadURL.setValue(url);
                            }
                        });
                        Log.i(TAG, metadata.getName()+" has been uploaded successfully to "+metadata.getBucket());
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        return downloadURL;
    }

    public MutableLiveData<SaveResponse> updateProfilePhotoURL(String newURL){
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        final MutableLiveData<SaveResponse> savedMutableLiveData = new MutableLiveData<>();

        if (firebaseUser != null && !TextUtils.isEmpty(newURL)) {
            String uid = firebaseUser.getUid();
            firebaseDatabase.child("users").child("regularUsers").child(uid).child("profilePhoto").setValue(newURL).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    SaveResponse saveResponse = new SaveResponse();

                    if (task.isSuccessful()) {
                        saveResponse.isSaved = true;
                        savedMutableLiveData.setValue(saveResponse);
                    } else {
                        saveResponse.isError = true;
                        saveResponse.statusMessage = task.getException().getMessage();
                        savedMutableLiveData.setValue(saveResponse);
                    }
                }
            });


        }

        return savedMutableLiveData;
    }
}
