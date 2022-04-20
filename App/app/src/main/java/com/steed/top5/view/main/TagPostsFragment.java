package com.steed.top5.view.main;


import android.graphics.Rect;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.steed.top5.R;
import com.steed.top5.adapter.SearchItemsListAdapter;
import com.steed.top5.databinding.FragmentTagPostsBinding;
import com.steed.top5.listener.SearchItemsListItemClickListener;
import com.steed.top5.pojo.Category;
import com.steed.top5.pojo.Post;
import com.steed.top5.pojo.PostsResponse;
import com.steed.top5.viewmodel.MainViewModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TagPostsFragment extends Fragment {

    FragmentTagPostsBinding binding;
    private NavController controller;
    private MainViewModel viewModel;

    String tag;

    private ArrayList<Post> posts;

    private SearchItemsListAdapter postsListAdapter;

    private int postsListWidth;

    public TagPostsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tag_posts, container, false);
        initController();
        initViewModel();

        tag = TagPostsFragmentArgs.fromBundle(getArguments()).getTagNameArg();

        binding.tagTitleTxt.setText(tag);

        posts = new ArrayList<>();

        binding.postsList.setItemAnimator(null);
        binding.postsList.setNestedScrollingEnabled(false);
        binding.postsList.setHasFixedSize(true);

        postsListAdapter = new SearchItemsListAdapter(requireContext(), posts, new SearchItemsListItemClickListener() {
            @Override
            public void OnItemClicked(int index) {
                TagPostsFragmentDirections.ActionTagPostsFragmentToContentFragment action = TagPostsFragmentDirections.actionTagPostsFragmentToContentFragment().setPostArg(posts.get(index));
                controller.navigate(action);
            }
        });
        binding.postsList.setAdapter(postsListAdapter);

        LinearLayoutManager postsListLayoutManager = new LinearLayoutManager(this.getContext(), GridLayoutManager.VERTICAL, false) {
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                postsListWidth = getWidth();
                return true;
            }
        };
        postsListLayoutManager.setItemPrefetchEnabled(true);
        binding.postsList.setLayoutManager(postsListLayoutManager);

        binding.postsList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.bottom = (int) (postsListWidth * 0.058);
            }
        });

        viewModel.getAllCategories();
        viewModel.allCategoriesLiveData.observe(requireActivity(), new Observer<ArrayList<Category>>() {
            @Override
            public void onChanged(ArrayList<Category> categories) {
                viewModel.getTagAllPosts(tag);
                viewModel.allTagPostsLiveData.observe(requireActivity(), new Observer<PostsResponse>() {
                    @Override
                    public void onChanged(PostsResponse postsResponse) {
                        if(!postsResponse.isError) {
                            posts.addAll(postsResponse.posts);
                            binding.loadingLayout.setVisibility(View.GONE);
                            postsListAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                controller.navigateUp();
            }
        });

        return binding.getRoot();
    }

    private void afterDBLoad(){
        binding.loadingLayout.setVisibility(View.GONE);


    }

    private void initController() {
        controller = NavHostFragment.findNavController(this);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

}
