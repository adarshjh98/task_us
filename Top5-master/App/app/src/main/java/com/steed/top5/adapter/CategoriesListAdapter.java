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
import com.steed.top5.databinding.CategoryListSingleItemLayoutBinding;
import com.steed.top5.databinding.ProfileInterestsListSingleItemLayoutBinding;
import com.steed.top5.listener.CategoryListItemClickListener;
import com.steed.top5.pojo.Category;

import java.util.ArrayList;

public class CategoriesListAdapter extends RecyclerView.Adapter<CategoriesListAdapter.CategoryListViewHolder> {

    private Context context;
    private ArrayList<Category> interests;
    private CategoryListItemClickListener categoryListItemClickListener;

    public CategoriesListAdapter(Context context, ArrayList<Category> interests) {
        this.context = context;
        this.interests = interests;
    }

    public void setCategoryListItemClickListener(CategoryListItemClickListener categoryListItemClickListener) {
        this.categoryListItemClickListener = categoryListItemClickListener;
    }

    @NonNull
    @Override
    public CategoriesListAdapter.CategoryListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoryListSingleItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.category_list_single_item_layout, parent, false);
        return new CategoryListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoriesListAdapter.CategoryListViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        final Category category = interests.get(position);

        Drawable backgroundDrawable = DrawableCompat.wrap(holder.binding.categoryImageContainer.getBackground()).mutate();
        DrawableCompat.setTint(backgroundDrawable, Color.parseColor(category.color));

        Glide.with(context).load(category.imgURL).into(holder.binding.categoryImage);
        holder.binding.categoryNameTextView.setText(category.name);
        holder.itemView.setOnClickListener(view -> {categoryListItemClickListener.onItemClicked(category);});
    }

    @Override
    public int getItemCount() {
        return interests.size();
    }

    public class CategoryListViewHolder extends RecyclerView.ViewHolder {

        CategoryListSingleItemLayoutBinding binding;

        public CategoryListViewHolder(@NonNull CategoryListSingleItemLayoutBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
