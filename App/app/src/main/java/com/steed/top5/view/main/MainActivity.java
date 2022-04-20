package com.steed.top5.view.main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.MobileAds;
import com.steed.top5.R;
import com.steed.top5.databinding.ActivityMainBinding;
import com.steed.top5.singleton.UserSingleton;
import com.steed.top5.util.Constants;
import com.steed.top5.viewmodel.MainViewModel;

public class  MainActivity extends AppCompatActivity {
    private String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private NavController controller;
    private MainViewModel viewModel;

    private UserSingleton userSingleton = UserSingleton.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        controller = Navigation.findNavController(this, R.id.main_app_nav_host_fragment);

        initViewModel();
        updateUserProfileLogoutTxt();

        if(userSingleton.currentUser!=null) {
            binding.greetingsUser.setText("HEY " + (userSingleton.currentUser.name.toUpperCase()) + "!");


            final String profilePhoto = userSingleton.currentUser.profilePhoto;

            if(!TextUtils.isEmpty(profilePhoto)) {
                Glide.with(this).load(profilePhoto).apply(new RequestOptions()
                        .placeholder(R.drawable.user)).into(binding.userBtn);
            }

        }

        NavigationUI.setupWithNavController(binding.footer, controller);

        controller.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getLabel().equals("UserPrefFragment")) {
                binding.footerLayout.setVisibility(View.GONE);
                binding.filterBtn.setVisibility(View.INVISIBLE);
                binding.userBtn.setVisibility(View.INVISIBLE);
              
            } else if (destination.getLabel().equals("ContentFragment") || destination.getLabel().equals("DetailsFragment") || destination.getLabel().equals("CommentsFragment") || destination.getLabel().equals("TagPostsFragment")) {
                binding.headerLayout.setVisibility(View.GONE);
                binding.footerLayout.setVisibility(View.GONE);
            } else {
                binding.headerLayout.setVisibility(View.VISIBLE);
                binding.filterBtn.setVisibility(View.VISIBLE);
                binding.userBtn.setVisibility(View.VISIBLE);

                if (destination.getLabel().equals("FiltersFragment")) {
                    binding.footerLayout.setVisibility(View.GONE);
                    //binding.userBtn.setImageResource(R.drawable.user);
                    binding.userBtn.setVisibility(View.GONE);
                    binding.filterBtn.setImageResource(R.drawable.filter_selected);
                    binding.logoutTxt.setVisibility(View.INVISIBLE);
                } else if (destination.getLabel().equals("ProfileFragment")) {
                    binding.footerLayout.setVisibility(View.GONE);
                    binding.logoutTxt.setVisibility(View.VISIBLE);
                    binding.filterBtn.setImageResource(R.drawable.filter);
                    binding.filterBtn.setVisibility(View.GONE);
                    //binding.userBtn.setImageResource(R.drawable.user_selected);
                } else {
                    binding.footerLayout.setVisibility(View.VISIBLE);
                    binding.filterBtn.setImageResource(R.drawable.filter);
                    //binding.userBtn.setImageResource(R.drawable.user);
                    binding.logoutTxt.setVisibility(View.INVISIBLE);
                }
            }
        });

        binding.filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controller.getCurrentDestination().getLabel().equals("HomeFragment")) {
                    controller.navigate(R.id.action_homeFragment_to_filtersFragment);
                } else if (controller.getCurrentDestination().getLabel().equals("BrowseFragment")) {
                    controller.navigate(R.id.action_browseFragment_to_filtersFragment);
                } else if (controller.getCurrentDestination().getLabel().equals("SavedFragment")) {
                    controller.navigate(R.id.action_savedFragment_to_filtersFragment);
                }else if (controller.getCurrentDestination().getLabel().equals("CategoryFragment")) {
                    controller.navigate(R.id.action_categoryFragment_to_filtersFragment);
                }
            }
        });


        binding.userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String currentDestinationLabel = controller.getCurrentDestination().getLabel().toString();

                Log.i(TAG, "Current Destination "+currentDestinationLabel);

                if (currentDestinationLabel.equals("HomeFragment")) {
                    controller.navigate(R.id.action_homeFragment_to_profileFragment);
                } else if (currentDestinationLabel.equals("BrowseFragment")) {
                    controller.navigate(R.id.action_browseFragment_to_profileFragment);
                } else if (currentDestinationLabel.equals("SavedFragment")) {
                    controller.navigate(R.id.action_savedFragment_to_profileFragment);
                } else if (currentDestinationLabel.equals("CategoryFragment")) {
                    controller.navigate(R.id.action_categoryFragment_to_profileFragment);
                }else if (currentDestinationLabel.equals("ProfileFragment")) {
                    viewModel.signOutUser();
                    viewModel.signOutLiveData.observe(MainActivity.this, new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                          controller.navigate(ProfileFragmentDirections.actionProfileFragmentToCredentialsActivity());
                        }
                    });
                }
            }
        });

        binding.logoutTxt.setOnClickListener(view -> {
            final String currentDestinationLabel = controller.getCurrentDestination().getLabel().toString();
            Log.i(TAG, "Logout Current Destination "+currentDestinationLabel);
            if (currentDestinationLabel.equals("ProfileFragment")) {
                viewModel.signOutUser();
                viewModel.signOutLiveData.observe(MainActivity.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                         controller.navigate(ProfileFragmentDirections.actionProfileFragmentToCredentialsActivity());
                    }
                });
            }
        });

    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
    }

    private boolean getIsRTL() {
        SharedPreferences preferenceSharedPreferences = getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE);
        return preferenceSharedPreferences.getString(Constants.PREFERRED_LANG_PREFERRED, "en").equals("fa");
    }

    private void updateUserProfileLogoutTxt(){
        if(getIsRTL()) {
            binding.logoutTxt.setImageResource(R.drawable.logout_fa);
        } else {
            binding.logoutTxt.setImageResource(R.drawable.logout);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);



    }
}
