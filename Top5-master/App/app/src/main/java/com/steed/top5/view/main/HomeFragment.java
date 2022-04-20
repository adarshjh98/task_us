package com.steed.top5.view.main;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.steed.top5.R;
import com.steed.top5.adapter.PreferenceItemsListAdapter;
import com.steed.top5.databinding.FragmentHomeBinding;
import com.steed.top5.listener.PreferenceItemsListItemClickListener;
import com.steed.top5.pojo.Category;
import com.steed.top5.pojo.LikeResponse;
import com.steed.top5.pojo.Post;
import com.steed.top5.pojo.PostsResponse;
import com.steed.top5.pojo.SaveResponse;
import com.steed.top5.util.Constants;
import com.steed.top5.viewmodel.MainViewModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private NavController controller;
    private MainViewModel viewModel;

    private PreferenceItemsListAdapter preferenceItemsListAdapter;

    private ArrayList<Post> preferenceItems;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        initViewModel();
        initController();
        initBannerAd();

        viewModel.getAllUserTagsAndRecentlyViewedPosts();
        viewModel.completionResponseLiveData.observe(requireActivity(), new Observer<SaveResponse>() {
            @Override
            public void onChanged(SaveResponse saveResponse) {
                viewModel.getAllCategories();
                viewModel.allCategoriesLiveData.observe(requireActivity(), new Observer<ArrayList<Category>>() {
                    @Override
                    public void onChanged(ArrayList<Category> categories) {
                        SharedPreferences preferenceSharedPreferences = requireActivity().getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE);
                        int filterIndex = preferenceSharedPreferences.getInt(Constants.PREFERRED_FILTER_INDEX, Constants.PREFERRED_FILTER_INDEX_DEFAULT);
                        viewModel.getAllSelectedCategoricalPosts(filterIndex);
                        viewModel.allPostsLiveData.observe(requireActivity(), new Observer<PostsResponse>() {
                            @Override
                            public void onChanged(PostsResponse postsResponse) {
                                if (postsResponse.isError) {
                                    binding.statusTxt.setText(postsResponse.statusMessage);
                                    binding.loadingLayout.setVisibility(View.GONE);
                                    binding.noneLayout.setVisibility(View.VISIBLE);
                                } else {
                                    if (preferenceItems != null) {
                                        preferenceItems.clear();
                                        preferenceItems.addAll(postsResponse.posts);
                                        preferenceItemsListAdapter.notifyDataSetChanged();
                                    } else {
                                        preferenceItems = new ArrayList<>();
                                        preferenceItems.addAll(postsResponse.posts);
                                    }
                                    afterDBLoad();
                                }
                            }
                        });

//                viewModel.allCategoriesLiveData.removeObservers(requireActivity());
                    }
                });

                viewModel.completionResponseLiveData.removeObservers(requireActivity());
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

    private void initBannerAd() {
        MobileAds.initialize(requireActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adBanner.loadAd(adRequest);
    }

    private void afterDBLoad() {
        binding.preferenceItemsList.setItemAnimator(null);
        binding.preferenceItemsList.setNestedScrollingEnabled(false);
        binding.preferenceItemsList.setHasFixedSize(true);

        preferenceItemsListAdapter = new PreferenceItemsListAdapter(getContext(), preferenceItems, new PreferenceItemsListItemClickListener() {
            @Override
            public void OnItemClicked(int index) {
                final Post post = preferenceItems.get(index);

                Log.i(TAG, "Post Link "+post.link);
                viewModel.updateFiltersData(post);
                HomeFragmentDirections.ActionHomeFragmentToContentFragment action = HomeFragmentDirections.actionHomeFragmentToContentFragment().setPostArg(post);
                controller.navigate(action);
            }

            @Override
            public void OnPostLikeContainerClicked(int index) {
                viewModel.setLikeOrUnlikePost(preferenceItems.get(index));
                viewModel.postLikedOrUnlikedLiveData.observe(requireActivity(), new Observer<LikeResponse>() {
                    @Override
                    public void onChanged(LikeResponse response) {
                        if (response.isSuccess) {
                            preferenceItems.set(preferenceItems.indexOf(response.post), response.post);
                            preferenceItemsListAdapter.notifyItemChanged(preferenceItems.indexOf(response.post));
                        }
                    }
                });
            }

            @Override
            public void OnPostCommentsContainerClicked(int index) {
                viewModel.updateFiltersData(preferenceItems.get(index));
                HomeFragmentDirections.ActionHomeFragmentToContentFragment action = HomeFragmentDirections.actionHomeFragmentToContentFragment().setPostArg(preferenceItems.get(index));
                controller.navigate(action);
            }

            @Override
            public void OnPostSaveBtnClicked(int index) {
                viewModel.savePost(preferenceItems.get(index));
                viewModel.savePostLiveData.observe(requireActivity(), new Observer<SaveResponse>() {
                    @Override
                    public void onChanged(SaveResponse saveResponse) {
                        if (!saveResponse.isError) {
                            Post tempPost = preferenceItems.get(index);
                            tempPost.isSaved = saveResponse.isSaved;
                            preferenceItems.get(index).isSaved = saveResponse.isSaved;
                            preferenceItemsListAdapter.notifyItemChanged(index);
                            Toast.makeText(requireContext(), saveResponse.isSaved ? "Saved!" : "Unsaved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), saveResponse.statusMessage, Toast.LENGTH_SHORT).show();
                        }

                        if (viewModel.savePostLiveData.hasActiveObservers()) {
                            viewModel.savePostLiveData.removeObservers(requireActivity());
                        }
                    }
                });
            }
        });
        binding.preferenceItemsList.setAdapter(preferenceItemsListAdapter);


        LinearLayoutManager preferenceItemsListLayoutManager = new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false);
        preferenceItemsListLayoutManager.setItemPrefetchEnabled(true);

        binding.preferenceItemsList.setLayoutManager(preferenceItemsListLayoutManager);

        binding.loadingLayout.setVisibility(View.GONE);
    }

}
