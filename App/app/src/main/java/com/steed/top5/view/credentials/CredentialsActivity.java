package com.steed.top5.view.credentials;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.google.android.gms.ads.MobileAds;
import com.steed.top5.R;
import com.steed.top5.util.Constants;
import com.steed.top5.viewmodel.CredentialsViewModel;

public class CredentialsActivity extends AppCompatActivity {

    CredentialsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);
        initAdMob();
        initViewModel();
    }

    void initViewModel(){
        viewModel = new ViewModelProvider(this).get(CredentialsViewModel.class);
    }

    void initAdMob(){
        MobileAds.initialize(this, Constants.ADMOB_ID);
    }
}
