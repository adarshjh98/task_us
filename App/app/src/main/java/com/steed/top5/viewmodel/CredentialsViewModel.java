package com.steed.top5.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.steed.top5.model.CredentialsModel;
import com.steed.top5.pojo.AuthResponse;
import com.steed.top5.pojo.User;

public class CredentialsViewModel extends AndroidViewModel {

    private CredentialsModel credentialsModel;
    public LiveData<AuthResponse> authenticatedUserLiveData;
    public LiveData<User> userCheckLiveData;
    public LiveData<Boolean> sendEmailLiveData;

    public CredentialsViewModel(@NonNull Application application) {
        super(application);
        credentialsModel = new CredentialsModel();
    }

    public void checkIfUserIsAuthenticated(){
        userCheckLiveData = credentialsModel.checkIfUserIsAuthenticatedInFirebase();
    }

    public void signInWithCredentials(User user) {
        authenticatedUserLiveData = credentialsModel.firebaseSignInWithCredentials(user);
    }

    public void signUpWithCredentials(User user) {
        authenticatedUserLiveData = credentialsModel.firebaseSignUpWithCredentials(user);
    }

    public void sendPasswordResetEmail(String email) {
        sendEmailLiveData = credentialsModel.sendPasswordResetEmail(email);
    }
}
