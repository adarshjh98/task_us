package com.steed.top5.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.steed.top5.R;
import com.steed.top5.databinding.PopularListSingleItemViewBinding;
import com.steed.top5.listener.PopularListItemClickListener;
import com.steed.top5.pojo.Post;

import java.util.ArrayList;

public class PopularListAdapter extends RecyclerView.Adapter<PopularListAdapter.PopularListViewHolder> {

    private Context context;
    private ArrayList<Post> popularItems;
    private PopularListItemClickListener popularListItemClickListener;

    public PopularListAdapter(Context context, ArrayList<Post> popularItems, PopularListItemClickListener popularListItemClickListener) {
        this.context = context;
        this.popularItems = popularItems;
        this.popularListItemClickListener = popularListItemClickListener;
    }

    @NonNull
    @Override
    public PopularListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PopularListSingleItemViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.popular_list_single_item_view, parent, false);
        return new PopularListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final PopularListViewHolder holder, int position) {
        Post post = popularItems.get(position);

        if (post.type.contains("img") || (post.type.equals("article") && !post.link.equals(""))) {
            Glide.with(context).load(post.link).into(holder.binding.itemImage);
        } else if (post.type.contains("vid")) {
            Glide.with(context).asBitmap().load(post.link).into(holder.binding.itemImage);
        }

        holder.binding.itemName.setText(post.type.toLowerCase().contains("txt") ? post.name : "");

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popularListItemClickListener.OnItemClicked(holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return popularItems.size();
    }

    public class PopularListViewHolder extends RecyclerView.ViewHolder {

        PopularListSingleItemViewBinding binding;

        public PopularListViewHolder(@NonNull PopularListSingleItemViewBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
