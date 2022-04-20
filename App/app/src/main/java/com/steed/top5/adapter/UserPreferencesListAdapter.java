package com.steed.top5.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.steed.top5.R;
import com.steed.top5.databinding.CategoriesListSingleItemViewBinding;
import com.steed.top5.listener.UserPreferencesListItemClickListener;
import com.steed.top5.pojo.Category;

import java.util.ArrayList;

public class UserPreferencesListAdapter extends RecyclerView.Adapter<UserPreferencesListAdapter.CategoriesListViewHolder> {

    private Context context;
    private ArrayList<Category> categories, selected;
    private UserPreferencesListItemClickListener userPreferencesListItemClickListener;

    public UserPreferencesListAdapter(Context context, ArrayList<Category> categories, UserPreferencesListItemClickListener userPreferencesListItemClickListener) {
        this.context = context;
        this.categories = categories;
        this.userPreferencesListItemClickListener = userPreferencesListItemClickListener;

        selected = new ArrayList<>();
    }

    @NonNull
    @Override
    public UserPreferencesListAdapter.CategoriesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CategoriesListSingleItemViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.categories_list_single_item_view, parent, false);
        return new CategoriesListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoriesListViewHolder holder, int position) {
        Category category = categories.get(position);

        holder.binding.categoryName.setText(category.name.toUpperCase());

        Drawable backgroundDrawable = DrawableCompat.wrap(holder.binding.categoryImageContainer.getBackground()).mutate();
        DrawableCompat.setTint(backgroundDrawable, Color.parseColor(category.color));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.binding.categoryImage.setClipToOutline(true);
        }

        holder.binding.categoryImage.setImageDrawable(null);
        Glide.with(context).load(category.imgURL).into(holder.binding.categoryImage);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Category clickedCategory = categories.get(holder.getLayoutPosition());
                if (selected.contains(clickedCategory)) {
                    selected.remove(categories.get(holder.getLayoutPosition()));
                    holder.binding.categoryImageContainerOuter.setBackgroundResource(0);
                } else {
                    selected.add(clickedCategory);
                    holder.binding.categoryImageContainerOuter.setBackgroundResource(R.drawable.category_circle_with_border);
                }
                userPreferencesListItemClickListener.OnItemClicked(holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class CategoriesListViewHolder extends RecyclerView.ViewHolder {

        CategoriesListSingleItemViewBinding binding;

        public CategoriesListViewHolder(@NonNull CategoriesListSingleItemViewBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
