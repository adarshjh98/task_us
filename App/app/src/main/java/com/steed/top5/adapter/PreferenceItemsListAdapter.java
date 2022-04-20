package com.steed.top5.adapter;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.steed.top5.databinding.PreferenceItemsListSingleItemLayoutBinding;
import com.steed.top5.listener.PreferenceItemsListItemClickListener;
import com.steed.top5.pojo.Post;
import com.steed.top5.util.Constants;

import java.util.ArrayList;

public class PreferenceItemsListAdapter extends RecyclerView.Adapter<PreferenceItemsListAdapter.PreferenceItemsListViewHolder> {

    private Context context;
    private ArrayList<Post> preferenceItems;
    private PreferenceItemsListItemClickListener preferenceItemsListItemClickListener;

    private static int lastSelectedPos = -1;

    public PreferenceItemsListAdapter(Context context, ArrayList<Post> preferenceItems, PreferenceItemsListItemClickListener preferenceItemsListItemClickListener) {
        this.context = context;
        this.preferenceItems = preferenceItems;
        this.preferenceItemsListItemClickListener = preferenceItemsListItemClickListener;
    }

    @NonNull
    @Override
    public PreferenceItemsListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PreferenceItemsListSingleItemLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.preference_items_list_single_item_layout, parent, false);
        return new PreferenceItemsListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final PreferenceItemsListViewHolder holder, int position) {
        Post post = preferenceItems.get(position);

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFERENCE_SHARED_PREF, Context.MODE_PRIVATE);
        boolean isLangEn = sharedPreferences.getString(Constants.PREFERRED_LANG_PREFERRED, "en").equals("en");

        holder.binding.itemName.setText(post.name);
        holder.binding.likes.setText(post.likes + (isLangEn ? " like" + (post.likes != 1 ? "s" : "") : " لایک"));
        holder.binding.comments.setText(post.comments + (isLangEn ? " comment" + (post.comments != 1 ? "s" : "") : " نظر"));

        if (post.type.contains("img") || (post.type.equals("article") && !post.link.equals(""))) {
            Glide.with(context).load(post.link).into(holder.binding.itemContentImage);
        } else if (post.type.contains("vid")) {
            holder.binding.playBtn.setVisibility(View.VISIBLE);
            Glide.with(context).asBitmap().load(post.link).into(holder.binding.itemContentImage);
        }

        holder.binding.liked.setImageResource(post.isLiked ? R.drawable.ic_fav_true : R.drawable.ic_fav_false);
        holder.binding.saveBtnImg.setImageResource(post.isSaved ? R.drawable.ic_download_true : R.drawable.ic_download_false);

//        holder.binding.itemContentText.setText(post.type.toLowerCase().contains("txt") ? post.text : "");

        Drawable backgroundDrawable = DrawableCompat.wrap(holder.binding.itemTypeImageContainer.getBackground()).mutate();
        DrawableCompat.setTint(backgroundDrawable, Color.parseColor(post.category.color));

        Glide.with(context).load(post.category.imgURL).into(holder.binding.itemTypeImage);

        if (lastSelectedPos == position) {
            holder.binding.itemImageOverlay.setVisibility(View.GONE);
            ViewGroup.LayoutParams topGap = holder.binding.itemImageContainerTopSpace.getLayoutParams();
            topGap.height = 0;
            holder.binding.itemImageContainerTopSpace.setLayoutParams(topGap);
        } else {
            holder.binding.itemImageOverlay.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams topGap = holder.binding.itemImageContainerTopSpace.getLayoutParams();
            topGap.height = 1;
            holder.binding.itemImageContainerTopSpace.setLayoutParams(topGap);
        }

        holder.binding.likesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceItemsListItemClickListener.OnPostLikeContainerClicked(holder.getLayoutPosition());
            }
        });

        holder.binding.commentsContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceItemsListItemClickListener.OnPostCommentsContainerClicked(holder.getLayoutPosition());
            }
        });

        holder.binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferenceItemsListItemClickListener.OnPostSaveBtnClicked(holder.getLayoutPosition());
            }
        });

        holder.binding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastSelectedPos >= 0) {
                    notifyItemChanged(lastSelectedPos);
                }

                if (lastSelectedPos != holder.getLayoutPosition()) {
                    lastSelectedPos = holder.getLayoutPosition();

                    notifyItemChanged(lastSelectedPos);
                }

                preferenceItemsListItemClickListener.OnItemClicked(holder.getLayoutPosition());
            }
        });
    }


    @Override
    public int getItemCount() {
        return preferenceItems.size();
    }

    public class PreferenceItemsListViewHolder extends RecyclerView.ViewHolder {

        PreferenceItemsListSingleItemLayoutBinding binding;

        public PreferenceItemsListViewHolder(@NonNull PreferenceItemsListSingleItemLayoutBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
}
