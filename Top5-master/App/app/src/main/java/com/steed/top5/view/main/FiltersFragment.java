package com.steed.top5.view.main;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;

import com.steed.top5.R;
import com.steed.top5.adapter.InterestsListAdapter;
import com.steed.top5.databinding.FragmentFiltersBinding;
import com.steed.top5.listener.InterestsListItemClickListener;
import com.steed.top5.pojo.Category;
import com.steed.top5.pojo.FiltersResponse;
import com.steed.top5.pojo.SaveResponse;
import com.steed.top5.util.Constants;
import com.steed.top5.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class FiltersFragment extends Fragment {

    private FragmentFiltersBinding binding;
    private NavController controller;
    private MainViewModel viewModel;

    private int interestsListWidth;

    private ArrayList<Category> allInterests;
    private ArrayList<String> selectedInterests, updatedInterests;

    private CheckBox lastCheckedCheckBox;
    private int lastCheckedCheckBoxIndex;

    private boolean isFilterSaved, areInterestsSaved;

    public FiltersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filters, container, false);
        controller = NavHostFragment.findNavController(this);

        initViewModel();
        initCheckBoxes();

        viewModel.getAllCategories();
        viewModel.allCategoriesLiveData.observe(requireActivity(), new Observer<ArrayList<Category>>() {
            @Override
            public void onChanged(ArrayList<Category> categories) {
                if (viewModel.allCategoriesLiveData.hasActiveObservers()) {
                    viewModel.allCategoriesLiveData.removeObservers(requireActivity());
                }
                viewModel.getFiltersAndSelectedCategories();
                viewModel.filtersResponseLiveData.observe(requireActivity(), new Observer<FiltersResponse>() {
                    @Override
                    public void onChanged(FiltersResponse filtersResponse) {
                        if (viewModel.filtersResponseLiveData.hasActiveObservers()) {
                            viewModel.filtersResponseLiveData.removeObservers(requireActivity());
                        }
                        allInterests = filtersResponse.allCategories;
                        selectedInterests = filtersResponse.selectedCategories;
                        updatedInterests = new ArrayList<>();
                        updatedInterests.addAll(selectedInterests);

                        SharedPreferences preferenceSharedPreferences = requireActivity().getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE);
                        SharedPreferences.Editor preferenceSharedPreferencesEditor = preferenceSharedPreferences.edit();
                        preferenceSharedPreferencesEditor.putInt(Constants.PREFERRED_FILTER_INDEX, filtersResponse.filterID);
                        preferenceSharedPreferencesEditor.apply();

                        afterDBLoad();
                    }
                });
            }
        });


        return binding.getRoot();
    }

    void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    void initCheckBoxes() {
        final HashMap<ConstraintLayout, CheckBox> checkboxes = new HashMap<>();
        checkboxes.put(binding.checkbox1, binding.checkboxInp1);
        checkboxes.put(binding.checkbox2, binding.checkboxInp2);
        checkboxes.put(binding.checkbox3, binding.checkboxInp3);
        checkboxes.put(binding.checkbox4, binding.checkboxInp4);

        ArrayList<ConstraintLayout> layouts = new ArrayList<>();
        layouts.add(binding.checkbox1);
        layouts.add(binding.checkbox2);
        layouts.add(binding.checkbox3);
        layouts.add(binding.checkbox4);

        SharedPreferences preferenceSharedPreferences = requireActivity().getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE);
        lastCheckedCheckBoxIndex = preferenceSharedPreferences.getInt(Constants.PREFERRED_FILTER_INDEX, Constants.PREFERRED_FILTER_INDEX_DEFAULT);

        lastCheckedCheckBox = checkboxes.get(layouts.get(lastCheckedCheckBoxIndex));
        lastCheckedCheckBox.setChecked(true);

        for (int i = 0; i < layouts.size(); i++) {
            ConstraintLayout layout = layouts.get(i);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkboxes.get(layout).isChecked()) {
                        lastCheckedCheckBox.setChecked(false);
                        checkboxes.get(layout).setChecked(true);
                        lastCheckedCheckBox = checkboxes.get(layout);
                        lastCheckedCheckBoxIndex = layouts.indexOf(layout);
                    }
                }
            });
        }
    }

    void afterDBLoad() {
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
                outRect.right = (int) (interestsListWidth * 0.037);
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(FiltersFragment.this).navigateUp();
            }
        });

        InterestsListAdapter interestsListAdapter = new InterestsListAdapter(getContext(), allInterests, selectedInterests, new InterestsListItemClickListener() {
            @Override
            public void OnItemClicked(String id) {
                if (updatedInterests.contains(id)) {
                    updatedInterests.remove(id);
                } else {
                    updatedInterests.add(id);
                }
            }
        });
        binding.interestsList.setAdapter(interestsListAdapter);

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferenceSharedPreferences = requireActivity().getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE);
                int previouslySavedFilterIndex = preferenceSharedPreferences.getInt(Constants.PREFERRED_FILTER_INDEX, Constants.PREFERRED_FILTER_INDEX_DEFAULT);

                if (lastCheckedCheckBoxIndex != previouslySavedFilterIndex) {
                    showOverlay();
                    viewModel.setFilterID("" + lastCheckedCheckBoxIndex);
                    viewModel.filterIDSavedLiveData.observe(requireActivity(), new Observer<SaveResponse>() {
                        @Override
                        public void onChanged(SaveResponse saveResponse) {
                            viewModel.filterIDSavedLiveData.removeObservers(requireActivity());

                            SharedPreferences.Editor preferenceSharedPreferencesEditor = preferenceSharedPreferences.edit();
                            preferenceSharedPreferencesEditor.putInt(Constants.PREFERRED_FILTER_INDEX, lastCheckedCheckBoxIndex);
                            preferenceSharedPreferencesEditor.apply();

                            isFilterSaved = true;

                            if (areInterestsSaved) {
                                hideOverlay();
                                controller.navigateUp();
                            }
                        }
                    });
                } else {
                    isFilterSaved = true;
                }

                if (!selectedInterests.containsAll(updatedInterests) || !updatedInterests.containsAll(selectedInterests) || selectedInterests.size() != updatedInterests.size()) {
                    if (!isOverlayVisible())
                        showOverlay();

                    viewModel.saveFiltersAndCategories(updatedInterests);
                    viewModel.filtersAndCategoriesSaveResponseLiveData.observe(requireActivity(), new Observer<SaveResponse>() {
                        @Override
                        public void onChanged(SaveResponse saveResponse) {
                            if (viewModel.filtersAndCategoriesSaveResponseLiveData.hasActiveObservers()) {
                                viewModel.filtersAndCategoriesSaveResponseLiveData.removeObservers(requireActivity());
                            }
                            areInterestsSaved = true;
                            if (isFilterSaved) {
                                hideOverlay();
                                controller.navigateUp();
                            }
                        }
                    });
                } else {
                    areInterestsSaved = true;
                }

                if (isFilterSaved && areInterestsSaved)
                    controller.navigateUp();
            }
        });

        binding.loadingLayout.setVisibility(View.GONE);
    }

    private void showOverlay() {
        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        binding.progressBarHolder.setAnimation(inAnimation);
        binding.progressBarHolder.setVisibility(View.VISIBLE);
    }

    private boolean isOverlayVisible() {
        return binding.progressBarHolder.getVisibility() == View.VISIBLE;
    }

    private void hideOverlay() {
        AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        binding.progressBarHolder.setAnimation(outAnimation);
        binding.progressBarHolder.setVisibility(View.GONE);
    }
}
