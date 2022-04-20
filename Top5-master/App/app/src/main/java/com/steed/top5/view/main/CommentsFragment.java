package com.steed.top5.view.main;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.steed.top5.R;
import com.steed.top5.adapter.CommentsListAdapter;
import com.steed.top5.databinding.FragmentCommentsBinding;
import com.steed.top5.databinding.FragmentContentBinding;
import com.steed.top5.listener.CommentsListItemClickListener;
import com.steed.top5.pojo.Comment;
import com.steed.top5.pojo.CommentsResponse;
import com.steed.top5.pojo.Post;
import com.steed.top5.util.Constants;
import com.steed.top5.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class CommentsFragment extends Fragment {

    private FragmentCommentsBinding binding;
    private NavController controller;
    private MainViewModel viewModel;

    private Post post;

    private CommentsListAdapter commentsListAdapter;
    private ArrayList<Comment> comments;

//    private boolean isLikerStrLoaded, areAllCommentsLoaded;

    public CommentsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_comments, container, false);
        initController();
        initViewModel();

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        post = CommentsFragmentArgs.fromBundle(getArguments()).getPostArg();

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE);
        boolean isLangEn = sharedPreferences.getString(Constants.PREFERRED_LANG_PREFERRED, "en").equals("en");

        viewModel.getAllPostLikerString(post.id, post.isLiked, isLangEn);
        viewModel.allPostLikerStringLiveData.observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
//                isLikerStrLoaded = true;
                binding.liker.setText(s);

//                if(areAllCommentsLoaded) {
//                    afterDBLoad();
//                }
            }
        });

        viewModel.getPostComments(post);
        viewModel.allPostCommentsLiveData.observe(requireActivity(), new Observer<CommentsResponse>() {
            @Override
            public void onChanged(CommentsResponse commentsResponse) {
//                areAllCommentsLoaded = true;
                if (!commentsResponse.isError) {
                    comments = new ArrayList<>();
                    comments.addAll(commentsResponse.comments);

//                    if(isLikerStrLoaded) {
                        afterDBLoad();
//                    }
                } else {
                    Toast.makeText(requireContext(), commentsResponse.statusMessage, Toast.LENGTH_SHORT).show();
                }

                if(viewModel.allPostCommentsLiveData.hasActiveObservers()) {
                    viewModel.allPostCommentsLiveData.removeObservers(requireActivity());
                }
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

    private void initController() {
        controller = NavHostFragment.findNavController(this);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
    }

    private void afterDBLoad() {
        binding.commentsList.setHasFixedSize(true);

        binding.commentsList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        commentsListAdapter = new CommentsListAdapter(comments, new CommentsListItemClickListener() {
            @Override
            public void OnDeleteClicked(int index) {
                viewModel.deleteCommentFromPost(post.category.id, comments.get(index));
                viewModel.deleteCommentLiveData.observe(requireActivity(), new Observer<CommentsResponse>() {
                    @Override
                    public void onChanged(CommentsResponse commentsResponse) {
                        comments.remove(index);
                        commentsListAdapter.notifyDataSetChanged();

                        viewModel.deleteCommentLiveData.removeObservers(requireActivity());
                    }
                });
            }
        });
        binding.commentsList.setAdapter(commentsListAdapter);

        binding.loadingLayout.setVisibility(View.GONE);
        binding.loadedLayout.setVisibility(View.VISIBLE);

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentMsg = binding.commentInp.getText().toString().trim();
                if(commentMsg.length() > 0) {
                    viewModel.commentOnPost(post.category.id, new Comment(post, "", "", binding.commentInp.getText().toString().trim(), Calendar.getInstance().getTimeInMillis()));
                    viewModel.commentOnPostLiveData.observe(requireActivity(), new Observer<CommentsResponse>() {
                        @Override
                        public void onChanged(CommentsResponse commentsResponse) {
                            if(!commentsResponse.isError) {
                                binding.commentInp.setText("");
                                comments.addAll(commentsResponse.comments);
                                commentsListAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(requireContext(), commentsResponse.statusMessage, Toast.LENGTH_SHORT).show();
                            }

                            if(viewModel.commentOnPostLiveData.hasActiveObservers()) {
                                viewModel.commentOnPostLiveData.removeObservers(requireActivity());
                            }
                        }
                    });
                } else {
                    Toast.makeText(requireContext(), "Please enter a valid comment!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
