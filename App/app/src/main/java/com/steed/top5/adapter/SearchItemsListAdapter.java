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
import com.steed.top5.databinding.SearchItemsListSingleItemLayoutBinding;
import com.steed.top5.listener.SearchItemsListItemClickListener;
import com.steed.top5.pojo.Post;

import java.util.ArrayList;

public class SearchItemsListAdapter extends RecyclerView.Adapter<SearchItemsListAdapter.SearchItemsListViewHolder> {

    private Context context;
    private ArrayList<Post> searchItems;
    private SearchItemsListItemClickListener searchItemsListItemClickListener;

    private static int lastSelectedPos = -1;

    public SearchItemsListAdapter(Context context, ArrayList<Post> searchItems, SearchItemsListItemClickListener searchItemsListItemClickListener) {
        this.context = context;
        this.searchItems = searchItems;
        this.searchItemsListItemClickListener = searchItemsListItemClickListener;
    }

    @Override
    public SearchItemsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchItemsListSingleItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.search_items_list_single_item_layout, parent, false);
        return new SearchItemsListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchItemsListViewHolder holder, int position) {
        Post post = searchItems.get(position);

        holder.binding.itemName.setText(post.name);

        if (post.type.contains("img") || (post.type.equals("article") && !post.link.equals(""))) {
            Glide.with(context).load(post.link).into(holder.binding.itemContentImage);
        } else if (post.type.contains("vid")) {
            holder.binding.playBtn.setVisibility(View.VISIBLE);
            Glide.with(context).asBitmap().load(post.link).into(holder.binding.itemContentImage);
        }

//        holder.binding.itemText.setText(post.type.toLowerCase().contains("txt") ? post.text : "");

        Drawable backgroundDrawable = DrawableCompat.wrap(holder.binding.itemTypeImageContainer.getBackground()).mutate();
        DrawableCompat.setTint(backgroundDrawable, Color.parseColor(post.category.color));

        Glide.with(context).load(post.category.imgURL).into(holder.binding.itemTypeImage);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchItemsListItemClickListener.OnItemClicked(holder.getLayoutPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    public class SearchItemsListViewHolder extends RecyclerView.ViewHolder {

        SearchItemsListSingleItemLayoutBinding binding;

        public SearchItemsListViewHolder(@NonNull SearchItemsListSingleItemLayoutBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
