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
import com.steed.top5.databinding.BrowseCategoriesListSingleItemViewBinding;
import com.steed.top5.listener.BrowseCategoriesListItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;

public class BrowseCategoriesListAdapter extends RecyclerView.Adapter<BrowseCategoriesListAdapter.BrowseCategoriesListViewHolder> {

    private Context context;
    private ArrayList<HashMap<String, String>> items;
    private BrowseCategoriesListItemClickListener browseCategoriesListItemClickListener;


    public BrowseCategoriesListAdapter(Context context, ArrayList<HashMap<String, String>> items, BrowseCategoriesListItemClickListener browseCategoriesListItemClickListener) {
        this.context = context;
        this.items = items;
        this.browseCategoriesListItemClickListener = browseCategoriesListItemClickListener;
    }

    @NonNull
    @Override
    public BrowseCategoriesListAdapter.BrowseCategoriesListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BrowseCategoriesListSingleItemViewBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.browse_categories_list_single_item_view, parent, false);
        return new BrowseCategoriesListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final BrowseCategoriesListAdapter.BrowseCategoriesListViewHolder holder, int position) {
        HashMap<String, String> map = items.get(position);

        holder.binding.itemName.setText(map.get("tag"));

        Glide.with(context).load(map.get("image")).into(holder.binding.image);

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                browseCategoriesListItemClickListener.OnItemClicked(holder.getLayoutPosition());
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class BrowseCategoriesListViewHolder extends RecyclerView.ViewHolder {

        private BrowseCategoriesListSingleItemViewBinding binding;

        public BrowseCategoriesListViewHolder(@NonNull BrowseCategoriesListSingleItemViewBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
