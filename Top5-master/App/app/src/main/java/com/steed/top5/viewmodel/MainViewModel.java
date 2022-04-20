package com.steed.top5.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.steed.top5.model.MainModel;
import com.steed.top5.pojo.Category;
import com.steed.top5.pojo.Comment;
import com.steed.top5.pojo.CommentsResponse;
import com.steed.top5.pojo.FiltersResponse;
import com.steed.top5.pojo.LikeResponse;
import com.steed.top5.pojo.Post;
import com.steed.top5.pojo.PostsResponse;
import com.steed.top5.pojo.SaveResponse;
import com.steed.top5.pojo.TagsResponse;

import java.util.ArrayList;

public class MainViewModel extends AndroidViewModel {

    private MainModel mainModel;
    public LiveData<Boolean> signOutLiveData;
    public LiveData<LikeResponse> postLikedOrUnlikedLiveData;
    public LiveData<ArrayList<Category>> allCategoriesLiveData, queriedCategoriesLiveData, selectedCategoriesLiveData;
    public LiveData<SaveResponse> saveResponseLiveData, filtersAndCategoriesSaveResponseLiveData, savePostLiveData, completionResponseLiveData, filterIDSavedLiveData;
    public LiveData<PostsResponse> allPostsLiveData, categoryPostsLiveData,allSavedPostsLiveData, allQueriedPostsLiveData, mostPopularPosts, allTagPostsLiveData;
    public LiveData<TagsResponse> topTaggedPosts;
    public LiveData<FiltersResponse> filtersResponseLiveData;
    public LiveData<CommentsResponse> allPostCommentsLiveData, commentOnPostLiveData, deleteCommentLiveData;
    public LiveData<String> allPostLikerStringLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        mainModel = new MainModel();
    }

    public void signOutUser() {
        signOutLiveData = mainModel.signOutUser();
    }

    public void getAllCategories() {
        allCategoriesLiveData = mainModel.getAllCategories();
    }

    public void getAllUserTagsAndRecentlyViewedPosts() {
        completionResponseLiveData = mainModel.getAllUserTagsAndRecentlyViewedPosts();
    }

    public void getQueriedCategories(String searchTerm) {
        queriedCategoriesLiveData = mainModel.getQueriedCategories(searchTerm);
    }

    public void saveSelectedCategories(ArrayList<String> categories) {
        saveResponseLiveData = mainModel.saveSelectedCategories(categories);
    }

    public void getAllSelectedCategoricalPosts(int filter) {
        allPostsLiveData = mainModel.getAllSelectedCategoricalPosts(filter);
    }

    public void setLikeOrUnlikePost(Post post){
        postLikedOrUnlikedLiveData = mainModel.getPostLikedOrUnlikedLiveData(post);
    }

    public void getFiltersAndSelectedCategories() {
        filtersResponseLiveData = mainModel.getFiltersAndSelectedCategories();
    }

    public void setFilterID(String filerID) {
        filterIDSavedLiveData = mainModel.setFilterID(filerID);
    }

    public void saveFiltersAndCategories(ArrayList<String> categories) {
        filtersAndCategoriesSaveResponseLiveData = mainModel.saveFiltersAndCategories(categories);
    }

    public void getSelectedCategories() {
        selectedCategoriesLiveData = mainModel.getSelectedCategories();
    }

    public void getAllPostLikerString(String postID, boolean isLiked, boolean isLangEn) {
        allPostLikerStringLiveData = mainModel.getAllPostLikerString(postID, isLiked, isLangEn);
    }

    public void getPostComments(Post post){
        allPostCommentsLiveData = mainModel.getPostComments(post);
    }

    public void commentOnPost(String categoryID, Comment comment) {
        commentOnPostLiveData = mainModel.commentOnPost(categoryID, comment);
    }

    public void deleteCommentFromPost(String categoryID, Comment comment) {
        deleteCommentLiveData = mainModel.deleteCommentFromPost(categoryID, comment);
    }

    public void getAllSavedPosts() {
        allSavedPostsLiveData = mainModel.getAllSavedPosts();
    }

    public void getCategoryPosts(String categoryID) {
        categoryPostsLiveData = mainModel.getCategoryPosts(categoryID);
    }

    public void savePost(Post post) {
        savePostLiveData = mainModel.savePost(post);
    }

    public void updateFiltersData(Post post) {
        mainModel.updateFiltersData(post);
    }

    public void getQueriedPosts(String searchTerm) {
        allQueriedPostsLiveData = mainModel.getQueriedPosts(searchTerm);
    }

    public void getTopTaggedPosts() {
        topTaggedPosts = mainModel.getTopTaggedPosts();
    }

    public void getTagAllPosts(String tag) {
        allTagPostsLiveData = mainModel.getTagPosts(tag);
    }

    public void getMostPopularPosts() {
        mostPopularPosts = mainModel.getMostPopularPosts();
    }
}
