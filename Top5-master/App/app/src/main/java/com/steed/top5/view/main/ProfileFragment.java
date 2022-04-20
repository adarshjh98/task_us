package com.steed.top5.view.main;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.steed.top5.R;
import com.steed.top5.adapter.ProfileInterestsListAdapter;
import com.steed.top5.databinding.FragmentProfileBinding;
import com.steed.top5.pojo.Category;
import com.steed.top5.pojo.SaveResponse;
import com.steed.top5.singleton.UserSingleton;
import com.steed.top5.util.Constants;
import com.steed.top5.viewmodel.MainViewModel;
import com.steed.top5.viewmodel.ProfileViewModel;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.steed.top5.util.Constants.PHOTO_PICKER_REQUEST_CODE;
import static com.steed.top5.util.Constants.SELECTED_PHOTO_EXTRA;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private String TAG = "ProfileFragment";
    private FragmentProfileBinding binding;
    private NavController controller;
    private MainViewModel viewModel;
    private ProfileViewModel profileViewModel;

    private int interestsListWidth;

    private ArrayList<Category> interests;

    private UserSingleton userSingleton = UserSingleton.getInstance();
    private String currentPhotoPath="";
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadLocale();
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        final String profilePhoto = userSingleton.currentUser.profilePhoto;

        if(!TextUtils.isEmpty(profilePhoto)) {
            Glide.with(this).load(profilePhoto).apply(new RequestOptions()
                    .placeholder(R.mipmap.default_image_holder)).into(binding.userImage);
        }





        initController();
        initViewModel();
        initLangSwitch();

        viewModel.getAllCategories();
        viewModel.allCategoriesLiveData.observe(requireActivity(), new Observer<ArrayList<Category>>() {
            @Override
            public void onChanged(ArrayList<Category> categories) {
                if(viewModel.allCategoriesLiveData.hasActiveObservers()) {
                    viewModel.allCategoriesLiveData.removeObservers(requireActivity());
                }
                viewModel.getSelectedCategories();
                viewModel.selectedCategoriesLiveData.observe(requireActivity(), categories1 -> {
                    if (viewModel.selectedCategoriesLiveData.hasActiveObservers()) {
                        viewModel.selectedCategoriesLiveData.removeObservers(requireActivity());
                    }
                    binding.loadingLayout.setVisibility(View.GONE);

                    interests = categories1;

                    afterDBLoad();
                });
            }
        });

        binding.userImage.setOnClickListener(view -> {
           choosePhoto();
        });
        return binding.getRoot();
    }

    private void choosePhoto(){
        Intent selectImageIntent = new Intent(getActivity(), PhotoPickerActivity.class);
        startActivityForResult(selectImageIntent, PHOTO_PICKER_REQUEST_CODE );
    }

    void initController() {
        controller = NavHostFragment.findNavController(this);
    }

    void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
    }

    void initLangSwitch() {
        SharedPreferences preferenceSharedPref = requireActivity().getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE);
        String lang = preferenceSharedPref.getString(Constants.PREFERRED_LANG_PREFERRED, "en");

        binding.langSwitch.setChecked(lang.equals("fa"));
    }

    void afterDBLoad() {
        binding.userName.setText(userSingleton.currentUser.name.toUpperCase() + "!");
        binding.userEmail.setText(userSingleton.currentUser.email);

        if (interests.isEmpty()) {
            binding.yourInterestsNone.setVisibility(View.VISIBLE);
        } else {
            binding.interestsList.setHasFixedSize(true);

            LinearLayoutManager interestsListLayoutManager = new LinearLayoutManager(this.getContext(), RecyclerView.HORIZONTAL, false) {
                @Override
                public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                    interestsListWidth = getWidth();
                    return true;
                }
            };
            binding.interestsList.setLayoutManager(interestsListLayoutManager);

            binding.interestsList.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    int position = parent.getChildAdapterPosition(view); // item position

                    outRect.right = (int) (interestsListWidth * 0.074);

                    if (position == 0) {
                        outRect.left = (int) (interestsListWidth * 0.092);
                    } else if (position == interests.size() - 1) {
                        outRect.right = (int) (interestsListWidth * 0.092);
                    }
                }
            });

            ProfileInterestsListAdapter profileInterestsListAdapter = new ProfileInterestsListAdapter(getContext(), interests);
            binding.interestsList.setAdapter(profileInterestsListAdapter);

            binding.saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    controller.navigateUp();
                }
            });
        }

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigateUp();
            }
        });

        binding.langSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setLocale("fa");
                    requireActivity().recreate();
                } else {
                    setLocale("en");
                    requireActivity().recreate();
                }
            }
        });
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        requireActivity().getBaseContext().getResources().updateConfiguration(config, requireActivity().getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = requireActivity().getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE).edit();
        editor.putString(Constants.PREFERRED_LANG_PREFERRED, lang);
        editor.apply();
    }

    public void loadLocale() {
        SharedPreferences preferenceSharedPref = requireActivity().getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE);
        String lang = preferenceSharedPref.getString(Constants.PREFERRED_LANG_PREFERRED, "en");
        setLocale(lang);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "Profile onActivityResult");

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_PICKER_REQUEST_CODE :
                    if (data != null) {
                        currentPhotoPath = data.getStringExtra(SELECTED_PHOTO_EXTRA);
                        setPicture();
                        uploadPhoto();

                    }

                    break;

            }
        }
    }


    /**
     * Scale photo to match the size of the destination view.
     */
    private void setPicture() {
        if(!TextUtils.isEmpty(currentPhotoPath)) {
            // Get the dimensions of the View
            int targetW = binding.userImage.getWidth();
            int targetH = binding.userImage.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            binding.userImage.setImageBitmap(bitmap);
        }
    }

    private void uploadPhoto(){
        profileViewModel.uploadPhoto(currentPhotoPath);
        profileViewModel.downloadURLiveData.observe(requireActivity(), downloadURL ->{
            userSingleton.currentUser.profilePhoto = downloadURL;

            /*
            profileViewModel.updateProfilePhoto(downloadURL);
            profileViewModel.saveResponseLiveData.observe(requireActivity(), new Observer<SaveResponse>() {
                @Override
                public void onChanged(SaveResponse saveResponse) {
                    if (saveResponse.isError) {
                        Log.i(TAG, saveResponse.statusMessage);
                    } else {
                        Log.i(TAG, "Photo updated successfully");
                    }
                }
            });*/
            Log.i(TAG, "Profile URL "+downloadURL);
        });
    }


}
