package com.steed.top5.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.steed.top5.model.ProfileModel;
import com.steed.top5.pojo.SaveResponse;

import java.util.ArrayList;

public class ProfileViewModel extends AndroidViewModel {

    private ProfileModel profileModel;
    public LiveData<String> downloadURLiveData;
    public LiveData<SaveResponse> saveResponseLiveData;


    public ProfileViewModel(@NonNull Application application) {
        super(application);
        profileModel = new ProfileModel();
    }

    public void uploadPhoto(String photoPath){
        downloadURLiveData = profileModel.uploadProfilePhoto(photoPath);
    }


    public void updateProfilePhoto(String newPhoto) {
        saveResponseLiveData = profileModel.updateProfilePhotoURL(newPhoto);
    }

}
