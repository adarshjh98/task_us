package com.steed.top5.view.main;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.steed.top5.R;
import com.steed.top5.adapter.BrowseCategoriesListAdapter;
import com.steed.top5.adapter.PopularListAdapter;
import com.steed.top5.adapter.SearchItemsListAdapter;
import com.steed.top5.databinding.FragmentBrowseBinding;
import com.steed.top5.listener.BrowseCategoriesListItemClickListener;
import com.steed.top5.listener.PopularListItemClickListener;
import com.steed.top5.listener.SearchItemsListItemClickListener;
import com.steed.top5.pojo.Category;
import com.steed.top5.pojo.Post;
import com.steed.top5.pojo.PostsResponse;
import com.steed.top5.pojo.TagsResponse;
import com.steed.top5.util.Constants;
import com.steed.top5.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class BrowseFragment extends Fragment {

    private int browseCategoriesListWidth, popularListWidth, searchItemsListWidth;

    private FragmentBrowseBinding binding;
    private NavController controller;
    private MainViewModel viewModel;

    private ArrayList<Post> popularItems;
    private ArrayList<Post> queriedSearchItems;

    private boolean isRTL;

    private PopularListAdapter popularListAdapter;
    private SearchItemsListAdapter searchItemsListAdapter;

    public BrowseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_browse, container, false);
        initController();
        initViewModel();

        setIsRTL();

        ArrayList<HashMap<String, String>> browseCategories = new ArrayList<>();
//
//        HashMap<String, String> categoryMap1 = new HashMap<>();
//        categoryMap1.put("name", "Spanish food");
//        categoryMap1.put("image", "https://firebasestorage.googleapis.com/v0/b/top-50-9951b.appspot.com/o/browse_categories_dummy_image.png?alt=media&token=3a1c9853-4ee3-42f0-9116-1ff6ad547ef6");
//        categoryMap1.put("typeColor", "#A27DCE");
//        categoryMap1.put("typeImage", "https://firebasestorage.googleapis.com/v0/b/top-50-9951b.appspot.com/o/content%2Fcategories%2Ffood.png?alt=media&token=2a886017-bbc0-46ce-8a96-a605dd1bf8f8");
//
//        browseCategories.add(categoryMap1);
//
//        HashMap<String, Object> categoryMap2 = new HashMap<>();
//        categoryMap2.put("name", "Pop music");
//        categoryMap2.put("image", "https://firebasestorage.googleapis.com/v0/b/top-50-9951b.appspot.com/o/pop_music.png?alt=media&token=f8a93acb-a8f0-4a29-8307-130238663365");
//        categoryMap2.put("typeColor", "#7EADDE");
//        categoryMap2.put("typeImage", "https://firebasestorage.googleapis.com/v0/b/top-50-9951b.appspot.com/o/content%2Fcategories%2Fmusic.png?alt=media&token=35b0dbeb-9b59-4bff-a57d-d5d1af76d3d2");
//
//        browseCategories.add(categoryMap2);
//
//        HashMap<String, Object> categoryMap3 = new HashMap<>();
//        categoryMap3.put("name", "Abstract art");
//        categoryMap3.put("image", "https://firebasestorage.googleapis.com/v0/b/top-50-9951b.appspot.com/o/abstract_art.png?alt=media&token=1916a6e9-cb32-4e46-8605-e1638012c329");
//        categoryMap3.put("typeColor", "#EF6E41");
//        categoryMap3.put("typeImage", "https://firebasestorage.googleapis.com/v0/b/top-50-9951b.appspot.com/o/content%2Fcategories%2Fart.png?alt=media&token=d3c40a8d-6ff8-45a3-903a-efe323ad4810");
//
//        browseCategories.add(categoryMap3);
//
//        HashMap<String, Object> categoryMap4 = new HashMap<>();
//        categoryMap4.put("name", "Planting");
//        categoryMap4.put("image", "https://firebasestorage.googleapis.com/v0/b/top-50-9951b.appspot.com/o/planting.png?alt=media&token=7a5f7ab3-dc9e-4822-ba33-63094b521be3");
//        categoryMap4.put("typeColor", "#89BF6F");
//        categoryMap4.put("typeImage", "https://firebasestorage.googleapis.com/v0/b/top-50-9951b.appspot.com/o/content%2Fcategories%2Fplanting.png?alt=media&token=644243d3-3df3-43de-908c-31e4e537665d");
//
//        browseCategories.add(categoryMap4);

        popularItems = new ArrayList<>();

        queriedSearchItems = new ArrayList<>();

        // Browse Categories List
        binding.browseCategoriesList.setHasFixedSize(true);

        BrowseCategoriesListAdapter browseCategoriesListAdapter = new BrowseCategoriesListAdapter(requireContext(), browseCategories, new BrowseCategoriesListItemClickListener() {
            @Override
            public void OnItemClicked(int index) {
                BrowseFragmentDirections.ActionBrowseFragmentToTagPostsFragment action = BrowseFragmentDirections.actionBrowseFragmentToTagPostsFragment(browseCategories.get(index).get("tag"));
                controller.navigate(action);
            }
        });
        binding.browseCategoriesList.setAdapter(browseCategoriesListAdapter);

        browseCategoriesListWidth = (int) (getWidth() * 0.811);

        GridLayoutManager browseCategoriesListLayoutManager = new GridLayoutManager(this.getContext(), 2, GridLayoutManager.VERTICAL, false);
        browseCategoriesListLayoutManager.setItemPrefetchEnabled(true);
        binding.browseCategoriesList.setLayoutManager(browseCategoriesListLayoutManager);

        binding.browseCategoriesList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position

                if (position % 2 == 0) {
                    outRect.right = (int) (browseCategoriesListWidth * 0.043);
                } else {
                    outRect.left = (int) (browseCategoriesListWidth * 0.043);
                }
//
                outRect.bottom = (int) (browseCategoriesListWidth * 0.086);
            }
        });

        // Popular List
        binding.popularList.setHasFixedSize(true);

        popularListAdapter = new PopularListAdapter(requireContext(), popularItems, new PopularListItemClickListener() {
            @Override
            public void OnItemClicked(int index) {
                BrowseFragmentDirections.ActionBrowseFragmentToContentFragment action = BrowseFragmentDirections.actionBrowseFragmentToContentFragment().setPostArg(popularItems.get(index));
                controller.navigate(action);
            }
        });
        binding.popularList.setAdapter(popularListAdapter);

        popularListWidth = getWidth();

        LinearLayoutManager popularListLayoutManager = new LinearLayoutManager(this.getContext(), GridLayoutManager.HORIZONTAL, false) {
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                lp.width = (int) (popularListWidth * 0.267);
                lp.height = (int) (popularListWidth * 0.267);
                return true;
            }
        };
        popularListLayoutManager.setItemPrefetchEnabled(true);
        binding.popularList.setLayoutManager(popularListLayoutManager);

        binding.popularList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view); // item position

                if ((!isRTL && position == 0) || (isRTL && position == popularItems.size() - 1)) {
                    outRect.left = (int) (popularListWidth * 0.086);
                    outRect.right = (int) (popularListWidth * 0.015);
                } else if ((isRTL && position == popularItems.size() - 1) || (isRTL && position == 0)) {
                    outRect.right = (int) (popularListWidth * 0.086);
                    outRect.left = (int) (popularListWidth * 0.015);
                } else {
                    outRect.right = (int) (popularListWidth * 0.015);
                    outRect.left = (int) (popularListWidth * 0.015);
                }
            }
        });

        viewModel.getAllCategories();
        viewModel.allCategoriesLiveData.observe(requireActivity(), new Observer<ArrayList<Category>>() {
            @Override
            public void onChanged(ArrayList<Category> categories) {
                viewModel.getTopTaggedPosts();
                viewModel.topTaggedPosts.observe(requireActivity(), new Observer<TagsResponse>() {
                    @Override
                    public void onChanged(TagsResponse tagsResponse) {
                        if(!tagsResponse.isError) {
                            browseCategories.clear();
                            browseCategories.addAll(tagsResponse.tags);
                            browseCategoriesListAdapter.notifyDataSetChanged();
                        }
                    }
                });

                viewModel.getMostPopularPosts();
                viewModel.mostPopularPosts.observe(requireActivity(), new Observer<PostsResponse>() {
                    @Override
                    public void onChanged(PostsResponse postsResponse) {
                        if(!postsResponse.isError) {
                            popularItems.clear();
                            popularItems.addAll(postsResponse.posts);
                            popularListAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

        // Search Items List
        binding.searchItemsList.setItemAnimator(null);
        binding.searchItemsList.setNestedScrollingEnabled(false);
        binding.searchItemsList.setHasFixedSize(true);

        searchItemsListAdapter = new SearchItemsListAdapter(requireContext(), queriedSearchItems, new SearchItemsListItemClickListener() {
            @Override
            public void OnItemClicked(int index) {
                BrowseFragmentDirections.ActionBrowseFragmentToContentFragment action = BrowseFragmentDirections.actionBrowseFragmentToContentFragment().setPostArg(queriedSearchItems.get(index));
                controller.navigate(action);
            }
        });
        binding.searchItemsList.setAdapter(searchItemsListAdapter);

        LinearLayoutManager searchItemsListLayoutManager = new LinearLayoutManager(this.getContext(), GridLayoutManager.VERTICAL, false) {
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                searchItemsListWidth = getWidth();
                return true;
            }
        };
        searchItemsListLayoutManager.setItemPrefetchEnabled(true);
        binding.searchItemsList.setLayoutManager(searchItemsListLayoutManager);

        binding.searchItemsList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = (int) (searchItemsListWidth * 0.058);
            }
        });

        // Search Bar
        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    binding.noneLayout.setVisibility(View.GONE);
//                    binding.loadingLayout.setVisibility(View.GONE);
                    binding.searchTermLayout.setVisibility(View.GONE);
                    binding.noSearchTermLayout.setVisibility(View.VISIBLE);
                } else {
                    binding.noSearchTermLayout.setVisibility(View.GONE);
                    binding.noneLayout.setVisibility(View.GONE);
                    binding.searchTermLayout.setVisibility(View.GONE);

                    viewModel.getAllCategories();
                    viewModel.allCategoriesLiveData.observe(requireActivity(), new Observer<ArrayList<Category>>() {
                        @Override
                        public void onChanged(ArrayList<Category> categories) {
                            viewModel.getQueriedPosts(s.toString());

                            if(!viewModel.allQueriedPostsLiveData.hasActiveObservers()){
                                viewModel.allQueriedPostsLiveData.observe(requireActivity(), new Observer<PostsResponse>() {
                                    @Override
                                    public void onChanged(PostsResponse postsResponse) {
                                        if(!postsResponse.isError) {
                                            queriedSearchItems.clear();
                                            queriedSearchItems.addAll(postsResponse.posts);
                                            searchItemsListAdapter.notifyDataSetChanged();

                                            if(postsResponse.posts.size() == 0) {
                                                binding.noneLayout.setVisibility(View.VISIBLE);
                                            } else {
                                                binding.noneLayout.setVisibility(View.GONE);
                                                binding.searchTermLayout.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
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

    private void initController() {
        controller = NavHostFragment.findNavController(this);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    private int getWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    private void setIsRTL() {
        SharedPreferences preferenceSharedPreferences = requireActivity().getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE);
        isRTL = preferenceSharedPreferences.getString(Constants.PREFERRED_LANG_PREFERRED, "en").equals("fa");
    }

}
