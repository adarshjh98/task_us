package com.steed.top5.view.credentials;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;

import com.steed.top5.R;
import com.steed.top5.databinding.FragmentSignupBinding;
import com.steed.top5.pojo.AuthResponse;
import com.steed.top5.pojo.User;
import com.steed.top5.singleton.UserSingleton;
import com.steed.top5.util.ChromeTabUtils;
import com.steed.top5.util.Constants;
import com.steed.top5.viewmodel.CredentialsViewModel;

import java.util.regex.Pattern;

import static com.steed.top5.util.Constants.PRIVACY_POLICY_URL;
import static com.steed.top5.util.Constants.TERMS_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignupFragment extends Fragment {

    private FragmentSignupBinding binding;
    private NavController controller;
    private CredentialsViewModel viewModel;

    private UserSingleton userSingleton = UserSingleton.getInstance();
    private ChromeTabUtils chromeTabUtils;

//    private String selectedGender;

    private boolean isRTL;

    public SignupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        chromeTabUtils = new ChromeTabUtils(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false);

        controller = NavHostFragment.findNavController(this);

        initViewModel();
//        initGenderSpinner();
        initClickListeners();
        rtlFix();

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (viewModel.authenticatedUserLiveData != null && viewModel.authenticatedUserLiveData.hasActiveObservers()) {
            viewModel.authenticatedUserLiveData.removeObservers(requireActivity());
        }
    }

    void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CredentialsViewModel.class);
    }

    void initClickListeners() {
        binding.existingAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment(), new NavOptions.Builder().setPopUpTo(R.id.startFragment, false).build());
            }
        });

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    showOverlay();
                    viewModel.signUpWithCredentials(new User(binding.usernameInp.getText().toString().trim(), binding.emailInp.getText().toString().trim(), binding.passwordInp.getText().toString().trim()));
                    viewModel.authenticatedUserLiveData.observe(requireActivity(), authResponse -> {
                        hideOverlay();
                        if (authResponse.isError) {
                            showError(authResponse.statusMessage);
                        } else {
                            userSingleton.currentUser = authResponse.user;
                            controller.navigate(SignupFragmentDirections.actionSignupFragmentToMainActivity());
                        }

                        if(viewModel.authenticatedUserLiveData.hasActiveObservers()) {
                            viewModel.authenticatedUserLiveData.removeObservers(requireActivity());
                        }
                    });
                }
//                controller.navigate(R.id.action_signupFragment_to_mainActivity);
            }
        });

        binding.termsOfUseTextView.setOnClickListener(view -> {
            chromeTabUtils.openUrl(TERMS_URL);
        });

        binding.privacyPolicyTextView.setOnClickListener(view -> {
            chromeTabUtils.openUrl(PRIVACY_POLICY_URL);
        });
    }

//    private void initGenderSpinner() {
//        String[] items = new String[]{requireActivity().getResources().getString(R.string.gender_male), requireActivity().getResources().getString(R.string.gender_female), requireActivity().getResources().getString(R.string.gender_other), requireActivity().getResources().getString(R.string.gender_prefer_not_to_say)};
//        selectedGender = items[0];
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, items) {
//            @NonNull
//            @Override
//            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
////                return super.getView(position, convertView, parent);
//
//                TextView textView = (TextView) super.getView(position, convertView, parent);
//
//                TextViewCompat.setAutoSizeTextTypeUniformWithPresetSizes(textView, new int[]{10, 11, 12}, TypedValue.COMPLEX_UNIT_SP);
//
//                View view = textView.getRootView();
//
//                view.setBackgroundColor(Color.parseColor("#00000000"));
//                view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
//
//                return view;
//            }
//        };
//        binding.genderInp.setAdapter(adapter);
//    }

    private boolean getIsRTL() {
        SharedPreferences preferenceSharedPreferences = requireActivity().getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE);
        return preferenceSharedPreferences.getString(Constants.PREFERRED_LANG_PREFERRED, "en").equals("fa");
    }

    private void rtlFix() {
        if (getIsRTL()) {
            binding.passwordInp.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
            binding.confirmPasswordInp.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

//            binding.genderInp.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }
    }

    private boolean validateForm() {
        boolean isUsernameValid = validateUsernameInput();
        boolean isEmailValid = validateEmailInput();
        boolean isPasswordValid = validatePasswordInput();
        return isUsernameValid && isEmailValid && isPasswordValid;
    }

    private boolean validateUsernameInput() {
        String nameVal = binding.usernameInp.getText().toString().trim();
        boolean isValid = nameVal.length() > 0;
        binding.usernameInpWarning.setVisibility(isValid ? View.GONE : View.VISIBLE);
        return isValid;
    }

    private boolean validateEmailInput() {
        String emailVal = binding.emailInp.getText().toString().trim();
        Pattern pattern = Pattern.compile(Constants.EMAIL_REGEX);
        boolean isValid = pattern.matcher(emailVal).matches();
        binding.emailInpWarning.setVisibility(isValid ? View.GONE : View.VISIBLE);
        return isValid;
    }

    private boolean validatePasswordInput() {
        String passwordVal = binding.passwordInp.getText().toString().trim();
        boolean isValid = passwordVal.length() >= 6;
        binding.passwordInpWarning.setVisibility(isValid ? View.GONE : View.VISIBLE);

        if (isValid) {
            binding.confirmPasswordInpWarning.setVisibility(!binding.passwordInp.getText().toString().equals(binding.confirmPasswordInp.getText().toString()) ? View.VISIBLE : View.GONE);
        }

        return isValid;
    }

    private void showOverlay() {
        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        binding.progressBarHolder.setAnimation(inAnimation);
        binding.progressBarHolder.setVisibility(View.VISIBLE);
    }

    private void hideOverlay() {
        AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        binding.progressBarHolder.setAnimation(outAnimation);
        binding.progressBarHolder.setVisibility(View.GONE);
    }

    private void showError(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
