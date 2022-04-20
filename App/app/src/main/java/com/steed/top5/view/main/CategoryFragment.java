package com.steed.top5.view.main;


import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.steed.top5.R;
import com.steed.top5.adapter.CategoriesListAdapter;
import com.steed.top5.adapter.PreferenceItemsListAdapter;
import com.steed.top5.databinding.FragmentCategoryBinding;
import com.steed.top5.databinding.FragmentHomeBinding;
import com.steed.top5.listener.CategoryListItemClickListener;
import com.steed.top5.pojo.Category;
import com.steed.top5.pojo.Post;
import com.steed.top5.viewmodel.MainViewModel;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;
    private NavController controller;
    private MainViewModel viewModel;

    private PreferenceItemsListAdapter preferenceItemsListAdapter;

    private ArrayList<Post> preferenceItems;

    public CategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false);

        initViewModel();
        initController();


        viewModel.getAllCategories();
        viewModel.allCategoriesLiveData.observe(requireActivity(), new Observer<ArrayList<Category>>() {
            @Override
            public void onChanged(ArrayList<Category> categories) {
                binding.loadingLayout.setVisibility(View.GONE);

                CategoriesListAdapter categoriesListAdapter = new CategoriesListAdapter(getContext(),categories);
                categoriesListAdapter.setCategoryListItemClickListener(new CategoryListItemClickListener() {
                    @Override
                    public void onItemClicked(Category category) {
                        final CategoryFragmentDirections.ActionCategoryFragmentToPostsByCategoryFragment
                                action= CategoryFragmentDirections.actionCategoryFragmentToPostsByCategoryFragment(category.id);
                        controller.navigate(action);
                    }
                });
                binding.categoryRecyclerView.setAdapter(categoriesListAdapter);

            }
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

    private void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    private void initController() {
        controller = NavHostFragment.findNavController(this);
    }


}
