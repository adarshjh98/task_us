package com.steed.top5.model;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.steed.top5.pojo.Category;
import com.steed.top5.pojo.Comment;
import com.steed.top5.pojo.CommentsResponse;
import com.steed.top5.pojo.FiltersResponse;
import com.steed.top5.pojo.LikeResponse;
import com.steed.top5.pojo.Post;
import com.steed.top5.pojo.PostsResponse;
import com.steed.top5.pojo.SaveResponse;
import com.steed.top5.pojo.TagsResponse;
import com.steed.top5.pojo.User;
import com.steed.top5.singleton.TagsSingleton;
import com.steed.top5.singleton.UserSingleton;
import com.steed.top5.util.Constants;
import com.steed.top5.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainModel {
    private String TAG = "MainModel";
    private UserSingleton userSingleton = UserSingleton.getInstance();
    private TagsSingleton tagsSingleton = TagsSingleton.getInstance();

    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference();

    private ArrayList<Category> categories = new ArrayList<>();
    private ArrayList<Post> allPosts = new ArrayList<>();
    private ArrayList<String> allPostIDs;

    private MutableLiveData<ArrayList<Category>> allQueriedCategoriesMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PostsResponse> allPostsMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<Boolean> signOutUser() {
        MutableLiveData<Boolean> signOutMutableLiveData = new MutableLiveData<>();
        firebaseAuth.signOut();
        signOutMutableLiveData.setValue(true);
        return signOutMutableLiveData;
    }

    public MutableLiveData<ArrayList<Category>> getAllCategories() {
        final MutableLiveData<ArrayList<Category>> allCategoriesMutableLiveData = new MutableLiveData<>();

        if (!categories.isEmpty()) {
            allCategoriesMutableLiveData.setValue(categories);
            return allCategoriesMutableLiveData;
        }

        firebaseDatabase.child("content").child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot category : snapshot.getChildren()) {
                        categories.add(
                                new Category(
                                        category.getKey(),
                                        category.child("name").getValue().toString(),
                                        category.child("imgURL").getValue().toString(),
                                        category.child("color").getValue().toString()
                                )
                        );
                    }

                    allCategoriesMutableLiveData.setValue(categories);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return allCategoriesMutableLiveData;
    }

    public MutableLiveData<ArrayList<Category>> getQueriedCategories(String searchTerm) {
        searchTerm = searchTerm.toUpperCase();
        ArrayList<Category> queriedCategories = new ArrayList<>();
        if (searchTerm.length() == 0) {
            queriedCategories.addAll(categories);
        } else {
            for (Category category : categories) {
                if (category.name.length() >= searchTerm.length()) {
                    if (category.name.toUpperCase().substring(0, searchTerm.length()).equals(searchTerm)) {
                        queriedCategories.add(category);
                    }
                }
            }
        }

        allQueriedCategoriesMutableLiveData.postValue(queriedCategories);

        return allQueriedCategoriesMutableLiveData;
    }

    public MutableLiveData<SaveResponse> saveSelectedCategories(ArrayList<String> categories) {
        final MutableLiveData<SaveResponse> savedMutableLiveData = new MutableLiveData<>();

        HashMap<String, String> categoryMap = new HashMap<>();
        for (String categoryID : categories) {
            categoryMap.put(categoryID, "categoryID");
        }

        firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("preferences").child("categories").setValue(categoryMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                SaveResponse saveResponse = new SaveResponse();

                if (task.isSuccessful()) {
                    saveResponse.isSaved = true;
                    savedMutableLiveData.setValue(saveResponse);
                } else {
                    saveResponse.isError = true;
                    saveResponse.statusMessage = task.getException().getMessage();
                    savedMutableLiveData.setValue(saveResponse);
                }
            }
        });

        return savedMutableLiveData;
    }

    public MutableLiveData<SaveResponse> getAllUserTagsAndRecentlyViewedPosts() {
        MutableLiveData<SaveResponse> completionResponseLiveData = new MutableLiveData<>();

        if (tagsSingleton.tags.isEmpty()) {
            firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("tags").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot tag : snapshot.getChildren()) {
                        tagsSingleton.tags.add(tag.getValue().toString());
                    }

                    firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("recentlyViewed").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot recentlyViewedSnapshot) {
                            HashMap<String, String> recentPostMap = new HashMap<>();
                            for (DataSnapshot recent : recentlyViewedSnapshot.getChildren()) {
                                recentPostMap.put("post", recent.child("post").getValue().toString());
                                recentPostMap.put("category", recent.child("category").getValue().toString());
                                tagsSingleton.recentlyViewed.add(recentPostMap);
                                tagsSingleton.recentlyViewedPosts.add(recent.child("post").getValue().toString());
                            }

                            SaveResponse saveResponse = new SaveResponse();
                            saveResponse.isSaved = true;
                            completionResponseLiveData.setValue(saveResponse);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            SaveResponse saveResponse = new SaveResponse();
                            saveResponse.isError = true;
                            saveResponse.statusMessage = "Failed to save recently viewed posts!";
                            completionResponseLiveData.setValue(saveResponse);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    SaveResponse saveResponse = new SaveResponse();
                    saveResponse.isError = true;
                    saveResponse.statusMessage = "Failed to save user tags!";
                    completionResponseLiveData.setValue(saveResponse);
                }
            });
        } else {
            SaveResponse saveResponse = new SaveResponse();
            saveResponse.isSaved = true;
            completionResponseLiveData.setValue(saveResponse);
        }


        return completionResponseLiveData;
    }

    public MutableLiveData<SaveResponse> saveFiltersAndCategories(ArrayList<String> categories) {
        final MutableLiveData<SaveResponse> savedMutableLiveData = new MutableLiveData<>();

        HashMap<String, String> categoryMap = new HashMap<>();
        for (String categoryID : categories) {
            categoryMap.put(categoryID, "categoryID");
        }

        HashMap<String, Map<String, String>> map = new HashMap<>();
        map.put("categories", categoryMap);


        firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("preferences").setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                SaveResponse saveResponse = new SaveResponse();

                if (task.isSuccessful()) {
                    saveResponse.isSaved = true;
                    savedMutableLiveData.setValue(saveResponse);
                } else {
                    saveResponse.isError = true;
                    saveResponse.statusMessage = task.getException().getMessage();
                    savedMutableLiveData.setValue(saveResponse);
                }
            }
        });

        return savedMutableLiveData;
    }

    public MutableLiveData<PostsResponse> getAllSelectedCategoricalPosts(int filter) {
        final HashMap<String, Category> categoryIDToCategory = new HashMap<>();
        for (Category category : categories) {
            categoryIDToCategory.put(category.id, category);
        }

        allPosts = new ArrayList<>();
        allPostIDs = new ArrayList<>();

        firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("preferences").child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot categorySnapshot) {
                final PostsResponse postsResponse = new PostsResponse();
                if (categorySnapshot.exists()) {
                    postsResponse.posts = new ArrayList<>();
                    ArrayList<String> userCategories = new ArrayList<>();
                    for (final DataSnapshot category : categorySnapshot.getChildren()) {
                        userCategories.add(category.getKey());
                    }

                    firebaseDatabase.child("content").child("posts").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot post, @Nullable String previousChildName) {
                            boolean hasCommonTags = false, isRecentlyViewed = tagsSingleton.recentlyViewedPosts.contains(post.getKey());
                            boolean hasChance = ((new Random()).nextInt(3) + 1) % 3 == 0;
                            ArrayList<String> tags = new ArrayList<>();

                            for (DataSnapshot tag : post.child("tags").getChildren()) {
                                tags.add(tag.getKey());
                                if (!hasCommonTags && tagsSingleton.tags.contains(tag.getKey())) {
                                    hasCommonTags = true;
                                }
                            }

                            if ((filter == 0 && hasCommonTags) || (filter == 1 && isRecentlyViewed) || (filter == 2 && (hasChance || hasCommonTags)) || filter == 3) {
                                if (userCategories.contains(post.child("category").getValue().toString())) {
                                    String postID = post.getKey();
                                    String postText = post.child("text").getValue().toString();
                                    String link = !post.child("type").getValue().toString().equals("txt") ? post.child("link").getValue().toString() : "";
                                    if (post.child("type").getValue().toString().equals("article") && postText.contains("<img")) {
//                                        link = postText.substring(postText.indexOf("<img src=") + 10, postText.indexOf("alt") - 2);
                                        //link = postText.substring(postText.indexOf("<img src="), postText.indexOf("alt") - 2);

                                        link = StringUtils.extractImageLink(postText);
                                    }

                                    Post currPost = new Post(
                                            post.getKey(),
                                            post.child("type").getValue().toString(),
                                            post.child("name").getValue().toString(),
                                            link,
                                            postText,
                                            (long) post.child("likes").getValue(),
                                            (long) post.child("comments").getValue(),
                                            categoryIDToCategory.get(post.child("category").getValue().toString()),
                                            tags
                                    );


                                    firebaseDatabase.child("likes").child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                           if(snapshot.exists()){
                                               final long likeCount = snapshot.getChildrenCount();
                                               currPost.likes = likeCount;
                                           }

                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                    firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("saved").child(currPost.id).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            currPost.isSaved = snapshot.exists();

                                            firebaseDatabase.child("likes").child(post.getKey()).child(userSingleton.currentUser.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    currPost.isLiked = snapshot.exists();
                                                    allPosts.add(currPost);
                                                    allPostIDs.add(post.getKey());

                                                    postsResponse.posts.clear();
                                                    postsResponse.posts.addAll(allPosts);

                                                    allPostsMutableLiveData.postValue(postsResponse);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                            } else {
                                allPostsMutableLiveData.postValue(postsResponse);
                            }
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot post) {
                            if (allPostIDs.indexOf(post.getKey()) >= 0) {
                                allPosts.remove(allPostIDs.indexOf(post.getKey()));
                                allPostIDs.remove(post.getKey());

                                postsResponse.posts.clear();
                                postsResponse.posts.addAll(allPosts);

                                allPostsMutableLiveData.postValue(postsResponse);
                            }
                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    postsResponse.isError = true;
                    postsResponse.statusMessage = "Please select some interests from the filter menu to view relevant posts.";
                    allPostsMutableLiveData.postValue(postsResponse);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return allPostsMutableLiveData;
    }

    public MutableLiveData<FiltersResponse> getFiltersAndSelectedCategories() {
        final MutableLiveData<FiltersResponse> filtersResponseMutableLiveData = new MutableLiveData<>();

        final HashMap<String, Category> categoryIDToCategory = new HashMap<>();
        for (Category category : categories) {
            categoryIDToCategory.put(category.id, category);
        }

        firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("preferences").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FiltersResponse filtersResponse = new FiltersResponse();
                filtersResponse.allCategories = categories;
                if (snapshot.exists()) {
                    ArrayList<String> selectedCategories = new ArrayList<>();
                    for (DataSnapshot category : snapshot.child("categories").getChildren()) {
                        if (categoryIDToCategory.containsKey(category.getKey())) {
                            selectedCategories.add(category.getKey());
                        }
                    }
                    filtersResponse.selectedCategories = selectedCategories;
                    filtersResponse.filterID = snapshot.child("filterID").exists() ? Integer.parseInt(snapshot.child("filterID").getValue().toString()) : Constants.PREFERRED_FILTER_INDEX_DEFAULT;
                    filtersResponseMutableLiveData.setValue(filtersResponse);
                } else {
                    filtersResponse.selectedCategories = new ArrayList<>();
                    filtersResponseMutableLiveData.setValue(filtersResponse);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return filtersResponseMutableLiveData;
    }

    public MutableLiveData<SaveResponse> setFilterID(String filterID) {
        final MutableLiveData<SaveResponse> filterIDSavedLiveData = new MutableLiveData<>();

        firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("preferences").child("filterID").setValue(filterID).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                SaveResponse saveResponse = new SaveResponse();
                if (task.isSuccessful()) {
                    saveResponse.isSaved = true;
                } else {
                    saveResponse.isError = true;
                }

                filterIDSavedLiveData.setValue(saveResponse);
            }
        });

        return filterIDSavedLiveData;
    }

    public MutableLiveData<ArrayList<Category>> getSelectedCategories() {
        final MutableLiveData<ArrayList<Category>> selectedCategoriesMutableLiveData = new MutableLiveData<>();

        final HashMap<String, Category> categoryIDToCategory = new HashMap<>();
        for (Category category : categories) {
            categoryIDToCategory.put(category.id, category);
        }

        firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("preferences").child("categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Category> selectedCategories = new ArrayList<>();
                if (snapshot.exists()) {
                    for (DataSnapshot category : snapshot.getChildren()) {
                        if (categoryIDToCategory.containsKey(category.getKey())) {
                            selectedCategories.add(categoryIDToCategory.get(category.getKey()));
                        }
                    }
                }

                selectedCategoriesMutableLiveData.setValue(selectedCategories);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return selectedCategoriesMutableLiveData;
    }

    public MutableLiveData<LikeResponse> getPostLikedOrUnlikedLiveData(Post post) {
        final MutableLiveData<LikeResponse> postLikedOrUnlikedLiveData = new MutableLiveData<>();

        firebaseDatabase.child("content").child("posts").child(post.id).child("likes").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() == null) {
                    return Transaction.success(currentData);
                }

                long currentLikes = (long) currentData.getValue();

                if (!post.isLiked) {
                    currentLikes++;
                    post.isLiked = true;
                    post.likes++;
                } else {
                    if (currentLikes > 0) {
                        currentLikes--;
                        post.isLiked = false;
                        post.likes--;
                    }
                }

                currentData.setValue(currentLikes);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed) {
                    if (post.isLiked) {
                        interactionUpdates(post);

                        firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("tags").setValue(tagsSingleton.tags).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("recentlyViewed").setValue(tagsSingleton.recentlyViewed).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        firebaseDatabase.child("likes").child(post.id).child(userSingleton.currentUser.uid).setValue("userID").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                postLikedOrUnlikedLiveData.setValue(new LikeResponse(task.isSuccessful(), post));
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } else {
                        firebaseDatabase.child("likes").child(post.id).child(userSingleton.currentUser.uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                postLikedOrUnlikedLiveData.setValue(new LikeResponse(task.isSuccessful(), post));
                            }
                        });
                    }
                }
            }
        });

        return postLikedOrUnlikedLiveData;
    }

    public MutableLiveData<String> getAllPostLikerString(String postID, boolean isLiked, boolean isLangEn) {
        MutableLiveData<String> allPostLikerLiveData = new MutableLiveData<>();

        firebaseDatabase.child("likes").child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    final String[] likeStr = {isLiked ? "You" : ""};
                    long totalLikes = snapshot.getChildrenCount();

                    if (totalLikes == 0) {
                        allPostLikerLiveData.setValue(isLangEn ? "Be the first to like this" : "اولین نفری باشید که این پست را لایک مکند");
                    } else {
                        if (isLiked) totalLikes--;

                        ArrayList<String> namedUsers = new ArrayList<>();
                        int i = 0, limit = isLiked ? 2 : 3;
                        for (DataSnapshot liker : snapshot.getChildren()) {
                            if (!liker.getKey().equals(userSingleton.currentUser.uid)) {
                                long finalTotalLikes = totalLikes;
                                firebaseDatabase.child("users").child("regularUsers").child(liker.getKey()).child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        namedUsers.add(snapshot.getValue().toString());

                                        if (namedUsers.size() == limit || finalTotalLikes - namedUsers.size() <= 0) {
                                            long remainingLikes = finalTotalLikes - namedUsers.size();

                                            for (int j = 0; j < namedUsers.size(); j++) {
                                                if (j != 0 && j == namedUsers.size() - 1 && remainingLikes == 0) {
                                                    likeStr[0] += " and ";
                                                } else {
                                                    if (!(remainingLikes > 0 && j == namedUsers.size() - 1) && likeStr[0].length() > 0) {
                                                        likeStr[0] += ", ";
                                                    }

                                                }

                                                likeStr[0] += namedUsers.get(j)+", ";
                                            }

                                            if (remainingLikes > 0) {
                                                likeStr[0] += " and " + remainingLikes + " other like this";
                                            } else {
                                                likeStr[0] += " like this";
                                            }

                                            allPostLikerLiveData.setValue(likeStr[0]);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                                i++;
                            }

                            if (i == limit) break;

                        }

                        if (namedUsers.isEmpty()) {
                            allPostLikerLiveData.setValue(isLangEn ? "You like this" : "اینو دوست داری");
                        }
                    }

                } else {
                    allPostLikerLiveData.setValue(isLangEn ? "Be the first to like this" : "اولین نفری باشید که این پست را لایک مکند");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return allPostLikerLiveData;
    }

    public MutableLiveData<CommentsResponse> getPostComments(Post post) {
        MutableLiveData<CommentsResponse> allPostCommentsLiveData = new MutableLiveData<>();


        firebaseDatabase.child("comments").child(post.id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CommentsResponse commentsResponse = new CommentsResponse();
                if (snapshot.exists()) {
                    ArrayList<Comment> comments = new ArrayList<>();
                    final long[] totalCount = {snapshot.getChildrenCount()};
                    for (DataSnapshot comment : snapshot.getChildren()) {
                        Comment currComment = new Comment(post, comment.getKey(), comment.child("userID").getValue().toString(), comment.child("comment").getValue().toString(), (long) comment.child("timestamp").getValue());

                        if (!comment.child("userID").getValue().toString().equals(userSingleton.currentUser.uid)) {
                            firebaseDatabase.child("users").child("regularUsers").child(comment.child("userID").getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot nameSnapshot) {

                                    if (snapshot.exists()) {
                                        User userSnapshot = nameSnapshot.getValue(User.class);
                                        if (userSnapshot != null) {
                                            currComment.userName = userSnapshot.name;
                                            currComment.userPhoto = userSnapshot.profilePhoto;
                                            comments.add(currComment);
                                        }
                                    } else {
                                        totalCount[0]--;
                                    }

                                    if (comments.size() == totalCount[0]) {
                                        commentsResponse.comments = comments;
                                        allPostCommentsLiveData.setValue(commentsResponse);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } else {
                            currComment.userName = "You";
                            comments.add(currComment);

                            if (comments.size() == snapshot.getChildrenCount()) {
                                commentsResponse.comments = comments;
                                allPostCommentsLiveData.setValue(commentsResponse);
                            }
                        }
                    }
                } else {
                    commentsResponse.comments = new ArrayList<>();
                    commentsResponse.statusMessage = "No comments. Be the first to comment on this";
                    allPostCommentsLiveData.setValue(commentsResponse);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CommentsResponse commentsResponse = new CommentsResponse();
                commentsResponse.comments = new ArrayList<>();
                commentsResponse.statusMessage = "Some error occurred. Please try again!";

                allPostCommentsLiveData.setValue(commentsResponse);
            }
        });


        return allPostCommentsLiveData;
    }

    public MutableLiveData<CommentsResponse> commentOnPost(String categoryID, Comment comment) {
        MutableLiveData<CommentsResponse> commentOnPostLiveData = new MutableLiveData<>();

        comment.userID = userSingleton.currentUser.uid;
        comment.id = firebaseDatabase.child("comments").child(comment.post.id).push().getKey();

        firebaseDatabase.child("content").child("posts").child(comment.post.id).child("comments").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() == null) {
                    return Transaction.success(currentData);
                }

                long currentComments = (long) currentData.getValue();

                currentData.setValue(currentComments + 1);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed) {
                    firebaseDatabase.child("comments").child(comment.post.id).child(comment.id).setValue(comment.toJson()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            interactionUpdates(comment.post);

                            firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("tags").setValue(tagsSingleton.tags).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("recentlyViewed").setValue(tagsSingleton.recentlyViewed).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            CommentsResponse commentsResponse = new CommentsResponse();
                                            if (task.isSuccessful()) {
                                                commentsResponse.isError = false;
                                                comment.userName = "You";

                                                commentsResponse.comments = new ArrayList<>();
                                                commentsResponse.comments.add(comment);
                                            } else {
                                                commentsResponse.isError = true;
                                                commentsResponse.statusMessage = "Failed to comment!";
                                            }

                                            commentOnPostLiveData.setValue(commentsResponse);
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            }
        });

        return commentOnPostLiveData;
    }

    public MutableLiveData<CommentsResponse> deleteCommentFromPost(String categoryID, Comment comment) {
        MutableLiveData<CommentsResponse> deleteCommentLiveData = new MutableLiveData<>();

        firebaseDatabase.child("content").child("posts").child(comment.post.id).child("comments").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if (currentData.getValue() == null) {
                    return Transaction.success(currentData);
                }

                long currentComments = (long) currentData.getValue();

                currentData.setValue(currentComments - 1);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (committed) {
                    firebaseDatabase.child("comments").child(comment.post.id).child(comment.id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            deleteCommentLiveData.setValue(new CommentsResponse());
                        }
                    });
                }
            }
        });

        return deleteCommentLiveData;
    }

    public MutableLiveData<PostsResponse> getAllSavedPosts() {
        MutableLiveData<PostsResponse> allSavedPostsLiveData = new MutableLiveData<>();

        final HashMap<String, Category> categoryIDToCategory = new HashMap<>();
        for (Category category : categories) {
            categoryIDToCategory.put(category.id, category);
        }

        firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("saved").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PostsResponse postsResponse = new PostsResponse();
                postsResponse.posts = new ArrayList<>();
                if (snapshot.exists()) {
                    HashMap<String, String> filteredSavedPosts = new HashMap<>();
                    for (DataSnapshot post : snapshot.getChildren()) {
                        if (categoryIDToCategory.containsKey(post.getValue().toString())) {
                            filteredSavedPosts.put(post.getKey(), post.getValue().toString());
                        }
                    }

                    final int[] totalCount = {filteredSavedPosts.size()};
                    if (!filteredSavedPosts.isEmpty()) {
                        for (String postID : filteredSavedPosts.keySet()) {
                            firebaseDatabase.child("content").child("posts").child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                    if (postSnapshot.exists()) {
                                        ArrayList<String> tags = new ArrayList<>();

                                        for (DataSnapshot tag : postSnapshot.child("tags").getChildren()) {
                                            tags.add(tag.getKey());
                                        }

                                        String postText = postSnapshot.child("text").getValue().toString();
                                        String link = !postSnapshot.child("type").getValue().toString().equals("txt") ? postSnapshot.child("link").getValue().toString() : "";
                                        if (postSnapshot.child("type").getValue().toString().equals("article") && postText.contains("<img")) {
                                            //link = postText.substring(postText.indexOf("<img src=") + 10, postText.indexOf("alt") - 2);
                                            link = StringUtils.extractImageLink(postText);
                                        }

                                        Post currPost = new Post(
                                                postSnapshot.getKey(),
                                                postSnapshot.child("type").getValue().toString(),
                                                postSnapshot.child("name").getValue().toString(),
                                                link,
                                                postText,
                                                (long) postSnapshot.child("likes").getValue(),
                                                (long) postSnapshot.child("comments").getValue(),
                                                categoryIDToCategory.get(filteredSavedPosts.get(postID)),
                                                tags
                                        );

                                        firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("saved").child(currPost.id).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                currPost.isSaved = snapshot.exists();

                                                firebaseDatabase.child("likes").child(postID).child(userSingleton.currentUser.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot likesSnapshot) {
                                                        currPost.isLiked = likesSnapshot.exists();
                                                        postsResponse.posts.add(currPost);

                                                        if (postsResponse.posts.size() == totalCount[0])
                                                            allSavedPostsLiveData.postValue(postsResponse);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    } else {
                                        totalCount[0]--;
                                        if (postsResponse.posts.size() == totalCount[0])
                                            allSavedPostsLiveData.postValue(postsResponse);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    } else {
                        allSavedPostsLiveData.postValue(postsResponse);
                    }
                } else {
                    postsResponse.posts = new ArrayList<>();
                    postsResponse.statusMessage = "No Saved Posts!";
                    allSavedPostsLiveData.postValue(postsResponse);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                PostsResponse postsResponse = new PostsResponse();
                postsResponse.posts = new ArrayList<>();
                postsResponse.statusMessage = "No Saved Posts!";
                allSavedPostsLiveData.postValue(postsResponse);
            }
        });

        return allSavedPostsLiveData;
    }

    public MutableLiveData<PostsResponse> getCategoryPosts(String categoryId) {
        final HashMap<String, Category> categoryIDToCategory = new HashMap<>();
        for (Category category : categories) {
            categoryIDToCategory.put(category.id, category);
        }

        //limitToFirst(20)

        MutableLiveData<PostsResponse> categoryPostsLiveData = new MutableLiveData<>();
        firebaseDatabase.child("content").child("posts").orderByChild("category").equalTo(categoryId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                PostsResponse postsResponse = new PostsResponse();
                postsResponse.posts = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    long postsLimit = dataSnapshot.getChildrenCount();
                    for (final DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        ArrayList<String> tags = new ArrayList<>();

                        for (DataSnapshot tag : postSnapshot.child("tags").getChildren()) {
                            tags.add(tag.getKey());
                        }

                        String postText = postSnapshot.child("text").getValue().toString();
                        String link = !postSnapshot.child("type").getValue().toString().equals("txt") ? postSnapshot.child("link").getValue().toString() : "";
                        if (postSnapshot.child("type").getValue().toString().equals("article") && postText.contains("<img")) {
                            //link = postText.substring(postText.indexOf("<img src=") + 10, postText.indexOf("alt") - 2);
                            link = StringUtils.extractImageLink(postText);
                        }

                        final String postID = postSnapshot.getKey();
                        final String categoryId = postSnapshot.child("category").getValue().toString();
                        Post currPost = new Post(
                                postSnapshot.getKey(),
                                postSnapshot.child("type").getValue().toString(),
                                postSnapshot.child("name").getValue().toString(),
                                link,
                                postText,
                                (long) postSnapshot.child("likes").getValue(),
                                (long) postSnapshot.child("comments").getValue(),
                                categoryIDToCategory.get(categoryId),
                                tags
                        );

                        firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("saved").child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                currPost.isSaved = snapshot.exists();

                                firebaseDatabase.child("likes").child(postID).child(userSingleton.currentUser.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot likesSnapshot) {
                                        currPost.isLiked = likesSnapshot.exists();
                                        postsResponse.posts.add(currPost);

                                        if (postsResponse.posts.size() == postsLimit) {
                                            Collections.reverse(postsResponse.posts);
                                            categoryPostsLiveData.setValue(postsResponse);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }


                }else {
                    postsResponse.isError = true;
                    postsResponse.statusMessage = "No Posts Available";
                    categoryPostsLiveData.postValue(postsResponse);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return categoryPostsLiveData;
    }

    public MutableLiveData<SaveResponse> savePost(Post post) {
        MutableLiveData<SaveResponse> savePostLiveData = new MutableLiveData<>();

        Log.d("POST_SAVED", "" + post.isSaved);

        if (post.isSaved) {
            firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("saved").child(post.id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    SaveResponse saveResponse = new SaveResponse();
                    if (task.isSuccessful()) {
                        saveResponse.isSaved = false;
                    } else {
                        saveResponse.isError = true;
                        saveResponse.statusMessage = "Failed to unsave post!";
                    }
                    savePostLiveData.setValue(saveResponse);
                }
            });
        } else {
            firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("saved").child(post.id).setValue(post.category.id).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    SaveResponse saveResponse = new SaveResponse();
                    if (task.isSuccessful()) {
                        saveResponse.isSaved = true;
                    } else {
                        saveResponse.isError = true;
                        saveResponse.statusMessage = "Failed to save post!";
                    }
                    savePostLiveData.setValue(saveResponse);
                }
            });
        }

        return savePostLiveData;
    }

    public void updateFiltersData(Post post) {
        interactionUpdates(post);

        firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("tags").setValue(tagsSingleton.tags).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("recentlyViewed").setValue(tagsSingleton.recentlyViewed);
            }
        });
    }

    private void interactionUpdates(Post post) {
        for (int i = 0, j = tagsSingleton.tags.size(); i < post.tags.size(); i++, j++) {
            String tag = post.tags.get(i);
            if (!tagsSingleton.tags.contains(tag)) {
                if (j < Constants.USER_TAGS_LIMIT) {
                    tagsSingleton.tags.add(tag);
                } else {
                    tagsSingleton.tags.set(j % Constants.USER_TAGS_LIMIT, tag);
                }
            }
        }

        if (!tagsSingleton.recentlyViewedPosts.contains(post.id)) {
            HashMap<String, String> newRecentPostMap = new HashMap<>();
            newRecentPostMap.put("post", post.id);
            newRecentPostMap.put("category", post.category.id);
            if (tagsSingleton.recentlyViewed.size() < Constants.USER_TAGS_LIMIT) {
                tagsSingleton.recentlyViewed.add(newRecentPostMap);
            } else {
                tagsSingleton.recentlyViewed.set(tagsSingleton.recentlyViewed.size() % Constants.USER_TAGS_LIMIT, newRecentPostMap);
            }
            tagsSingleton.recentlyViewedPosts.add(post.id);
        }
    }

    public LiveData<TagsResponse> getTopTaggedPosts() {
        MutableLiveData<TagsResponse> topTaggedPosts = new MutableLiveData<>();

        final HashMap<String, Category> categoryIDToCategory = new HashMap<>();
        for (Category category : categories) {
            categoryIDToCategory.put(category.id, category);
        }

        firebaseDatabase.child("tags/count").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TagsResponse tagsResponse = new TagsResponse();
                tagsResponse.tags = new ArrayList<>();

                if (snapshot.exists()) {
                    ArrayList<String> tags = new ArrayList<>();
                    ArrayList<Integer> tagCounts = new ArrayList<>();
                    for (DataSnapshot tagCount : snapshot.getChildren()) {
                        tags.add(tagCount.getKey());
                        tagCounts.add(Integer.parseInt(tagCount.getValue().toString()));
                    }

                    ArrayList<Integer> sortingMethodReturns = new ArrayList<Integer>();

                    Collections.sort(tagCounts, new Comparator<Integer>() {

                        @Override
                        public int compare(Integer lhs, Integer rhs) {
                            int returning = rhs.compareTo(lhs);
                            sortingMethodReturns.add(returning);
                            return returning;
                        }

                    });


                    final int[] j = {0};

                    Collections.sort(tags, new Comparator<String>() {

                        @Override
                        public int compare(String lhs, String rhs) {
                            // TODO Auto-generated method stub

                            // comparator method will sort the second list also according to
                            // the changes made with list a
                            int returning = sortingMethodReturns.get(j[0]);
                            j[0]++;
                            return returning;
                        }

                    });

                    ArrayList<String> topTenTags = new ArrayList<>();
                    for (int i = tags.size() - 1, k = 0; i >= 0 && k < 10; i--, k++) {
                        topTenTags.add(tags.get(i));
                    }

                    Random rand = new Random();

                    int numberOfElements = 4;

                    ArrayList<String> randomlySelectedTags = new ArrayList<>();

                    for (int i = 0; i < numberOfElements && topTenTags.size() > 0; i++) {
                        int randomIndex = rand.nextInt(topTenTags.size());
                        randomlySelectedTags.add(topTenTags.get(randomIndex));
                        topTenTags.remove(randomIndex);
                    }

                    for (String tagID : randomlySelectedTags) {
                        firebaseDatabase.child("tags/postsAgainstTag/" + tagID).orderByKey().limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String postID = snapshot.getChildren().iterator().next().getKey();

                                    firebaseDatabase.child("content").child("posts").child(postID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot postSnapshot) {
                                            final Object childValue = postSnapshot.child("text").getValue();
                                            String postText = "";

                                            if (childValue != null) {
                                                postText = childValue.toString();
                                            }
                                            String link = !postSnapshot.child("type").getValue().toString().equals("txt") ? postSnapshot.child("link").getValue().toString() : "";
                                            if (postSnapshot.child("type").getValue().toString().equals("article") && postText.contains("<img")) {
                                                // link = postText.substring(postText.indexOf("<img src=") + 10, postText.indexOf("alt") - 2);
                                                link = StringUtils.extractImageLink(postText);
                                            }

                                            HashMap<String, String> tagMap = new HashMap<>();
                                            tagMap.put("tag", tagID);
                                            tagMap.put("image", link);
                                            tagsResponse.tags.add(tagMap);

                                            if (tagsResponse.tags.size() == randomlySelectedTags.size())
                                                topTaggedPosts.postValue(tagsResponse);


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                } else {
                                    tagsResponse.statusMessage = "No Tagged Posts";
                                    topTaggedPosts.setValue(tagsResponse);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                } else {
                    tagsResponse.statusMessage = "No Tagged Posts";
                    topTaggedPosts.setValue(tagsResponse);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return topTaggedPosts;
    }

    public LiveData<PostsResponse> getMostPopularPosts() {
        MutableLiveData<PostsResponse> mostPopularPosts = new MutableLiveData<>();

        final HashMap<String, Category> categoryIDToCategory = new HashMap<>();
        for (Category category : categories) {
            categoryIDToCategory.put(category.id, category);
        }

        firebaseDatabase.child("content").child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot postsSnapshot) {
                ArrayList<String> postIDs = new ArrayList<>();
                for (DataSnapshot post : postsSnapshot.getChildren()) {
                    postIDs.add(post.getKey());
                }

                PostsResponse postsResponse = new PostsResponse();
                postsResponse.posts = new ArrayList<>();

                long postsLimit = postsSnapshot.getChildrenCount() >= 3 ? 3 : postsSnapshot.getChildrenCount();

                Random rand = new Random();
                for (int i = 0; i < 3; i++) {
                    int randIndex = rand.nextInt((postIDs.size() - 1) + 1);

                    String postID = postIDs.get(randIndex);

                    postIDs.remove(randIndex);

                    DataSnapshot postSnapshot = postsSnapshot.child(postID);

                    ArrayList<String> tags = new ArrayList<>();

                    for (DataSnapshot tag : postSnapshot.child("tags").getChildren()) {
                        tags.add(tag.getKey());
                    }

                    String postText = postSnapshot.child("text").getValue().toString();
                    String link = !postSnapshot.child("type").getValue().toString().equals("txt") ? postSnapshot.child("link").getValue().toString() : "";
                    if (postSnapshot.child("type").getValue().toString().equals("article") && postText.contains("<img")) {
                        //link = postText.substring(postText.indexOf("<img src=") + 10, postText.indexOf("alt") - 2);
                        link = StringUtils.extractImageLink(postText);
                    }

                    Post currPost = new Post(
                            postSnapshot.getKey(),
                            postSnapshot.child("type").getValue().toString(),
                            postSnapshot.child("name").getValue().toString(),
                            link,
                            postText,
                            (long) postSnapshot.child("likes").getValue(),
                            (long) postSnapshot.child("comments").getValue(),
                            categoryIDToCategory.get(postSnapshot.child("category").getValue().toString()),
                            tags
                    );

                    firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("saved").child(currPost.id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            currPost.isSaved = snapshot.exists();

                            firebaseDatabase.child("likes").child(postSnapshot.getKey()).child(userSingleton.currentUser.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot likesSnapshot) {
                                    currPost.isLiked = likesSnapshot.exists();

                                    postsResponse.posts.add(currPost);

                                    if (postsResponse.posts.size() == postsLimit) {
                                        mostPopularPosts.setValue(postsResponse);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return mostPopularPosts;
    }

    public LiveData<PostsResponse> getQueriedPosts(String searchTerm) {
        MutableLiveData<PostsResponse> allQueriedPostsLiveData = new MutableLiveData<>();

        final ArrayList<String> categoryIDs = new ArrayList<>();
        final HashMap<String, Category> categoryIDToCategory = new HashMap<>();
        for (Category category : categories) {
            categoryIDToCategory.put(category.id, category);
            categoryIDs.add(category.id);
        }

        String[] searchWords = searchTerm.split(" ");

        PostsResponse postsResponse = new PostsResponse();
        postsResponse.posts = new ArrayList<>();
        String word = searchTerm;

        Log.i(TAG, "Search Term "+searchTerm);
        //+ "\uf8ff"
        //for (String word : searchWords) {
            if (!word.equals("")) {
                firebaseDatabase.child("content").child("posts").orderByChild("words/" + word.toLowerCase(Locale.ENGLISH)).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.getChildrenCount() == 0) {
                            allQueriedPostsLiveData.setValue(postsResponse);
                        }

                        for (DataSnapshot post : snapshot.getChildren()) {
                            ArrayList<String> tags = new ArrayList<>();

                            for (DataSnapshot tag : post.child("tags").getChildren()) {
                                tags.add(tag.getKey());
                            }

                            String postText = post.child("text").getValue().toString();
                            String link = !post.child("type").getValue().toString().equals("txt") ? post.child("link").getValue().toString() : "";
                            if (post.child("type").getValue().toString().equals("article") && postText.contains("<img")) {
                                //link = postText.substring(postText.indexOf("<img src=") + 10, postText.indexOf("alt") - 2);
                                link = StringUtils.extractImageLink(postText);
                            }

                            Post currPost = new Post(
                                    post.getKey(),
                                    post.child("type").getValue().toString(),
                                    post.child("name").getValue().toString(),
                                    link,
                                    postText,
                                    (long) post.child("likes").getValue(),
                                    (long) post.child("comments").getValue(),
                                    categoryIDToCategory.get(post.child("category").getValue().toString()),
                                    tags
                            );

                            firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("saved").child(currPost.id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    currPost.isSaved = snapshot.exists();

                                    firebaseDatabase.child("likes").child(post.getKey()).child(userSingleton.currentUser.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot likesSnapshot) {
                                            currPost.isLiked = likesSnapshot.exists();

                                            if (!postsResponse.posts.contains(currPost)) {
                                                postsResponse.posts.add(currPost);

                                                allQueriedPostsLiveData.setValue(postsResponse);
                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        //}


        return allQueriedPostsLiveData;
    }

    public LiveData<PostsResponse> getTagPosts(String tag) {
        MutableLiveData<PostsResponse> allTagPostsLiveData = new MutableLiveData<>();

        final ArrayList<String> categoryIDs = new ArrayList<>();
        final HashMap<String, Category> categoryIDToCategory = new HashMap<>();
        for (Category category : categories) {
            categoryIDToCategory.put(category.id, category);
            categoryIDs.add(category.id);
        }

        PostsResponse postsResponse = new PostsResponse();
        postsResponse.posts = new ArrayList<>();

        firebaseDatabase.child("content").child("posts").orderByChild("tags/" + tag).equalTo("tagVal").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() == 0) {
                    allTagPostsLiveData.setValue(postsResponse);
                }

                for (DataSnapshot post : snapshot.getChildren()) {
                    ArrayList<String> tags = new ArrayList<>();

                    for (DataSnapshot tag : post.child("tags").getChildren()) {
                        tags.add(tag.getKey());
                    }

                    String postText = post.child("text").getValue().toString();
                    String link = !post.child("type").getValue().toString().equals("txt") ? post.child("link").getValue().toString() : "";
                    if (post.child("type").getValue().toString().equals("article") && postText.contains("<img")) {
                        //link = postText.substring(postText.indexOf("<img src=") + 10, postText.indexOf("alt") - 2);
                        link = StringUtils.extractImageLink(postText);
                        Log.i(TAG, "Tag Link " + link);
                    }

                    Post currPost = new Post(
                            post.getKey(),
                            post.child("type").getValue().toString(),
                            post.child("name").getValue().toString(),
                            link,
                            postText,
                            (long) post.child("likes").getValue(),
                            (long) post.child("comments").getValue(),
                            categoryIDToCategory.get(post.child("category").getValue().toString()),
                            tags
                    );

                    firebaseDatabase.child("users").child("regularUsers").child(userSingleton.currentUser.uid).child("saved").child(currPost.id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            currPost.isSaved = snapshot.exists();

                            firebaseDatabase.child("likes").child(post.getKey()).child(userSingleton.currentUser.uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot likesSnapshot) {
                                    currPost.isLiked = likesSnapshot.exists();

                                    if (!postsResponse.posts.contains(currPost)) {
                                        postsResponse.posts.add(currPost);

                                        allTagPostsLiveData.setValue(postsResponse);
                                    }


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return allTagPostsLiveData;
    }

    public static void sortList(List<?> objectsToOrder, List<?> orderedObjects) {

        HashMap<Object, Integer> indexMap = new HashMap<>();
        int index = 0;
        for (Object object : orderedObjects) {
            indexMap.put(object, index);
            index++;
        }

        Collections.sort(objectsToOrder, new Comparator<Object>() {

            public int compare(Object left, Object right) {

                Integer leftIndex = indexMap.get(left);
                Integer rightIndex = indexMap.get(right);
                if (leftIndex == null) {
                    return -1;
                }
                if (rightIndex == null) {
                    return 1;
                }

                return Integer.compare(leftIndex, rightIndex);
            }
        });
    }
}
