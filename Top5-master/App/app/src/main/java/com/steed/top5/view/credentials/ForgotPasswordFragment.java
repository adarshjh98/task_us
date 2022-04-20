package com.steed.top5.view.credentials;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import com.steed.top5.R;
import com.steed.top5.databinding.FragmentForgotPasswordBinding;
import com.steed.top5.databinding.FragmentProfileBinding;
import com.steed.top5.util.ChromeTabUtils;
import com.steed.top5.util.Constants;
import com.steed.top5.viewmodel.CredentialsViewModel;

import java.util.regex.Pattern;

public class ForgotPasswordFragment extends Fragment {

    private FragmentForgotPasswordBinding binding;
    private NavController controller;
    private CredentialsViewModel viewModel;




    public ForgotPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgot_password, container, false);
        controller = NavHostFragment.findNavController(this);

        initViewModel();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigateUp();
            }
        });

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateEmailInput()) {
                    showOverlay();
                    viewModel.sendPasswordResetEmail(binding.emailInp.getText().toString());
                    viewModel.sendEmailLiveData.observe(requireActivity(), new Observer<Boolean>() {
                        @Override
                        public void onChanged(Boolean aBoolean) {
                            hideOverlay();
                            if(aBoolean) {
                                Toast.makeText(requireContext(), "Sent!", Toast.LENGTH_SHORT).show();
                                controller.navigateUp();
                            } else {
                                viewModel.sendEmailLiveData.removeObservers(requireActivity());
                                Toast.makeText(requireContext(), "Failed to send email!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "Invalid Email!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return binding.getRoot();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CredentialsViewModel.class);
    }

    private boolean validateEmailInput() {
        String emailVal = binding.emailInp.getText().toString().trim();
        Pattern pattern = Pattern.compile(Constants.EMAIL_REGEX);
        boolean isValid = pattern.matcher(emailVal).matches();
        binding.emailInpWarning.setVisibility(isValid ? View.GONE : View.VISIBLE);
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
}
