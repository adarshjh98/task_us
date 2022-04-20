package com.steed.top5.model;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.steed.top5.pojo.AuthResponse;
import com.steed.top5.pojo.User;

public class CredentialsModel {
    private String TAG = "CredentialsModel";

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

    public MutableLiveData<User> checkIfUserIsAuthenticatedInFirebase() {
        final MutableLiveData<User> authenticatedUserMutableLiveData = new MutableLiveData<>();
        User user = new User();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {

            user.isAuthenticated = false;
            authenticatedUserMutableLiveData.setValue(user);
        } else {

            user.uid = firebaseUser.getUid();
            user.name = firebaseUser.getDisplayName();
            user.email = firebaseUser.getEmail();

            user.isAuthenticated = true;
            Log.i(TAG, "Name : "+user.name);


            //if (user.name.isEmpty()) {
                firebaseDatabase.child("users/regularUsers/" + user.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userSnapshot = snapshot.getValue(User.class);
                        if (userSnapshot != null) {
                            user.name = userSnapshot.name;
                            user.profilePhoto = userSnapshot.profilePhoto;
                        }

                        authenticatedUserMutableLiveData.setValue(user);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
//            } else {
//                authenticatedUserMutableLiveData.setValue(user);
//            }

        }
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<AuthResponse> firebaseSignInWithCredentials(User credentials) {
        final MutableLiveData<AuthResponse> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signInWithEmailAndPassword(credentials.email, credentials.password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> authTask) {
                AuthResponse authResponse = new AuthResponse();
                if (authTask.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        User user = new User();
                        user.uid = firebaseUser.getUid();
                        user.name = firebaseUser.getDisplayName();
                        user.email = firebaseUser.getEmail();

                        user.isNew = false;

                        try {
                            user.isNew = authTask.getResult().getAdditionalUserInfo().isNewUser();
                        } catch (Exception e) {
                        }

                        Log.i(TAG, "Name : "+user.name);
                        final Uri photoUrl = firebaseUser.getPhotoUrl();
                        if(photoUrl!=null) {
                            Log.i(TAG, "Profile Url : " + photoUrl.toString());
                        }

                        //if (user.name == null) {
                            firebaseDatabase.child("users/regularUsers/" + user.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    final User snapshotValue = snapshot.getValue(User.class);
                                    if (snapshotValue != null) {
                                        user.name = snapshotValue.name;
                                        user.profilePhoto = snapshotValue.profilePhoto;
                                    }

                                    authResponse.user = user;
                                    authenticatedUserMutableLiveData.setValue(authResponse);

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
//                        } else {
//                            authResponse.user = user;
//                            authenticatedUserMutableLiveData.setValue(authResponse);
//                        }
                    } else {
                        authResponse.isError = true;
                        authResponse.statusMessage = "Some error occurred.";
                        authenticatedUserMutableLiveData.setValue(authResponse);
                    }
                } else {
                    authResponse.isError = true;
                    authResponse.statusMessage = authTask.getException().getMessage();
                    authenticatedUserMutableLiveData.setValue(authResponse);
                }
            }
        });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<AuthResponse> firebaseSignUpWithCredentials(final User credentials) {
        final MutableLiveData<AuthResponse> authenticatedUserMutableLiveData = new MutableLiveData<>();
        firebaseAuth.createUserWithEmailAndPassword(credentials.email, credentials.password).addOnCompleteListener(authTask -> {
            final AuthResponse authResponse = new AuthResponse();
            if (authTask.isSuccessful()) {
                final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(credentials.name).build();
                    final User user = new User();
                    user.uid = firebaseUser.getUid();
                    firebaseUser.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.name = firebaseUser.getDisplayName();
                                user.email = firebaseUser.getEmail();
                                user.isNew = true;
                                authResponse.user = user;

                                firebaseDatabase.child("users").child("regularUsers").child(user.uid).setValue(user.toJson()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            authenticatedUserMutableLiveData.setValue(authResponse);
                                        } else {
                                            authResponse.isError = true;
                                            authResponse.statusMessage = task.getException().getMessage();
                                            authenticatedUserMutableLiveData.setValue(authResponse);
                                        }
                                    }
                                });
                            } else {
                                authResponse.isError = true;
                                authResponse.statusMessage = task.getException().getMessage();
                                authenticatedUserMutableLiveData.setValue(authResponse);
                            }
                        }
                    });
                }
            } else {
                authResponse.isError = true;
                authResponse.statusMessage = authTask.getException().getMessage();
                authenticatedUserMutableLiveData.setValue(authResponse);
            }
        });
        return authenticatedUserMutableLiveData;
    }

    public MutableLiveData<Boolean> sendPasswordResetEmail(String email) {
        final MutableLiveData<Boolean> sendEmailMutableLiveData = new MutableLiveData<>();

        firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                sendEmailMutableLiveData.setValue(task.isSuccessful());
            }
        });

        return sendEmailMutableLiveData;
    }

}
