package com.steed.top5.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.steed.top5.R;
import com.steed.top5.databinding.InterestsListSingleItemViewBinding;
import com.steed.top5.databinding.ProfileInterestsListSingleItemLayoutBinding;
import com.steed.top5.pojo.Category;

import java.util.ArrayList;

public class ProfileInterestsListAdapter extends RecyclerView.Adapter<ProfileInterestsListAdapter.ProfileInterestsListViewHolder> {

    private Context context;
    private ArrayList<Category> interests;

    public ProfileInterestsListAdapter(Context context, ArrayList<Category> interests) {
        this.context = context;
        this.interests = interests;
    }

    @NonNull
    @Override
    public ProfileInterestsListAdapter.ProfileInterestsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ProfileInterestsListSingleItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.profile_interests_list_single_item_layout, parent, false);
        return new ProfileInterestsListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProfileInterestsListAdapter.ProfileInterestsListViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        Category interest = interests.get(position);

        Drawable backgroundDrawable = DrawableCompat.wrap(holder.binding.interestTypeImageContainer.getBackground()).mutate();
        DrawableCompat.setTint(backgroundDrawable, Color.parseColor(interest.color));

        Glide.with(context).load(interest.imgURL).into(holder.binding.itemTypeImage);
    }

    @Override
    public int getItemCount() {
        return interests.size();
    }

    public class ProfileInterestsListViewHolder extends RecyclerView.ViewHolder {

        ProfileInterestsListSingleItemLayoutBinding binding;

        public ProfileInterestsListViewHolder(@NonNull ProfileInterestsListSingleItemLayoutBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
