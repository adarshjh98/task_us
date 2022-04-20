package com.steed.top5.view.credentials;


import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.steed.top5.R;
import com.steed.top5.databinding.FragmentStartBinding;
import com.steed.top5.util.ChromeTabUtils;

import static com.steed.top5.util.Constants.PRIVACY_POLICY_URL;
import static com.steed.top5.util.Constants.TERMS_URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {

    private FragmentStartBinding binding;
    private NavController controller;
    private ChromeTabUtils chromeTabUtils;


    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false);
        chromeTabUtils = new ChromeTabUtils(getActivity());
        controller = NavHostFragment.findNavController(this);

//        binding.newQuickLoginTxt.setText(getText(R.string.new_quick_login_face_id));

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(R.id.action_startFragment_to_loginFragment);
            }
        });

        binding.signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigate(R.id.action_startFragment_to_signupFragment);
            }
        });

        binding.termsOfUseTextView.setOnClickListener(view -> {
            chromeTabUtils.openUrl(TERMS_URL);
        });

        binding.privacyPolicyTextView.setOnClickListener(view -> {
            chromeTabUtils.openUrl(PRIVACY_POLICY_URL);
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle);
                alertDialogBuilder.setMessage("Are you sure that you want to EXIT?");
                alertDialogBuilder.setPositiveButton("yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                getActivity().finishAffinity();
                            }
                        });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        return binding.getRoot();
    }

}
