package com.steed.top5.view.credentials;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.steed.top5.R;
import com.steed.top5.pojo.User;
import com.steed.top5.singleton.UserSingleton;
import com.steed.top5.util.Constants;
import com.steed.top5.viewmodel.CredentialsViewModel;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class SplashFragment extends Fragment {

    private NavController controller;
    private CredentialsViewModel viewModel;

    private Handler handler = new Handler();

    private UserSingleton userSingleton = UserSingleton.getInstance();

    public SplashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        loadLocale();

        initController();
        initViewModel();

        // creating timer task, timer
        TimerTask tasknew = new TimerScheduleDelay();
        Timer timer = new Timer();

        // scheduling the task at interval
        timer.schedule(tasknew, 1000);


        return inflater.inflate(R.layout.fragment_splash, container, false);
    }

    private class TimerScheduleDelay extends TimerTask {
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    viewModel.checkIfUserIsAuthenticated();
                    viewModel.userCheckLiveData.observe(requireActivity(), new Observer<User>() {
                        @Override
                        public void onChanged(User user) {
                            if (user.isAuthenticated) {
                                userSingleton.currentUser = user;
                                controller.navigate(SplashFragmentDirections.actionSplashFragmentToMainActivity());
                            } else {
                                controller.navigate(SplashFragmentDirections.actionSplashFragmentToStartFragment(), new NavOptions.Builder().setPopUpTo(R.id.splashFragment, true).build());
                            }
                        }
                    });
                }
            });
        }
    }

    public void run() {
        System.out.println("timer working");
    }

    private void initController() {
        controller = NavHostFragment.findNavController(this);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(CredentialsViewModel.class);
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

    public void loadLocale(){
        SharedPreferences preferenceSharedPref = requireActivity().getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE);
        String lang = preferenceSharedPref.getString(Constants.PREFERRED_LANG_PREFERRED, "en");
        setLocale(lang);

    }

}
