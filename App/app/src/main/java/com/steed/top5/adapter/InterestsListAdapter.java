package com.steed.top5.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.steed.top5.R;
import com.steed.top5.databinding.InterestsListSingleItemViewBinding;
import com.steed.top5.listener.InterestsListItemClickListener;
import com.steed.top5.pojo.Category;

import java.util.ArrayList;

public class InterestsListAdapter extends RecyclerView.Adapter<InterestsListAdapter.InterestsListViewHolder> {

    private Context context;
    private ArrayList<Category> allInterests;
    private ArrayList<String> selectedInterests;

    private InterestsListItemClickListener interestsListItemClickListener;

    public InterestsListAdapter(Context context, ArrayList<Category> allInterests, ArrayList<String> selectedInterests, InterestsListItemClickListener interestsListItemClickListener) {
        this.context = context;
        this.allInterests = allInterests;
        this.selectedInterests = selectedInterests;
        this.interestsListItemClickListener = interestsListItemClickListener;
    }

    @NonNull
    @Override
    public InterestsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        InterestsListSingleItemViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.interests_list_single_item_view, parent, false);
        return new InterestsListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final InterestsListViewHolder holder, int position) {
        holder.setIsRecyclable(false);

        Category interest = allInterests.get(position);

        Drawable backgroundDrawable = DrawableCompat.wrap(holder.binding.interestTypeImageContainer.getBackground()).mutate();
        DrawableCompat.setTint(backgroundDrawable, Color.parseColor(interest.color));

        Glide.with(context).load(interest.imgURL).into(holder.binding.itemTypeImage);

        if (selectedInterests.contains(interest.id)) {
            holder.binding.checkboxInp.setChecked(true);
        } else {
            holder.binding.checkboxInp.setChecked(false);
        }

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.binding.checkboxInp.isChecked()) {
                    holder.binding.checkboxInp.setChecked(false);
                } else {
                    holder.binding.checkboxInp.setChecked(true);
                }
                interestsListItemClickListener.OnItemClicked(allInterests.get(holder.getLayoutPosition()).id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allInterests.size();
    }

    public class InterestsListViewHolder extends RecyclerView.ViewHolder {

        InterestsListSingleItemViewBinding binding;

        public InterestsListViewHolder(@NonNull InterestsListSingleItemViewBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
