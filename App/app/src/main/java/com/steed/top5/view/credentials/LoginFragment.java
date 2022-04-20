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
import com.steed.top5.databinding.FragmentLoginBinding;
import com.steed.top5.pojo.AuthResponse;
import com.steed.top5.pojo.User;
import com.steed.top5.singleton.UserSingleton;
import com.steed.top5.util.Constants;
import com.steed.top5.viewmodel.CredentialsViewModel;

import java.util.regex.Pattern;

import static com.steed.top5.view.credentials.LoginFragmentDirections.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;
    private NavController controller;
    private CredentialsViewModel viewModel;

    private UserSingleton userSingleton = UserSingleton.getInstance();

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);

        controller = NavHostFragment.findNavController(this);

        binding.emailInp.setText("wafulaantony88@gmail.com");
        binding.passwordInp.setText("vLsu=r1daxa4o7");

        initViewModel();
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
        binding.forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(actionLoginFragmentToForgotPasswordFragment());
            }
        });

        binding.newAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(actionLoginFragmentToSignupFragment(), new NavOptions.Builder().setPopUpTo(R.id.startFragment, false).build());
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateForm()) {
                    showOverlay();
                    viewModel.signInWithCredentials(new User(binding.emailInp.getText().toString().trim(), binding.passwordInp.getText().toString().trim()));
                    viewModel.authenticatedUserLiveData.observe(requireActivity(), new Observer<AuthResponse>() {
                        @Override
                        public void onChanged(AuthResponse authResponse) {
                            hideOverlay();
                            if(viewModel.authenticatedUserLiveData.hasActiveObservers()) {
                                viewModel.authenticatedUserLiveData.removeObservers(requireActivity());
                            }
                            if (authResponse.isError) {
                                showError(authResponse.statusMessage);
                            } else {
                                userSingleton.currentUser = authResponse.user;
                                controller.navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity());
                            }
                        }
                    });
                }
            }
        });
    }

    private boolean getIsRTL() {
        SharedPreferences preferenceSharedPreferences = requireActivity().getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE);
        return preferenceSharedPreferences.getString(Constants.PREFERRED_LANG_PREFERRED, "en").equals("fa");
    }

    private void rtlFix() {
        if (getIsRTL()) {
            binding.passwordInp.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        }
    }

    private boolean validateForm() {
        boolean isEmailValid = validateEmailInput();
        boolean isPasswordValid = validatePasswordInput();
        return isEmailValid && isPasswordValid;
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
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}
