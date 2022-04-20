package com.steed.top5.io;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.graphics.drawable.Drawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.RequestOptions;
import com.steed.top5.R;

import androidx.annotation.Nullable;






public class ImageLoader {
    private Context context;
    private final String TAG="LoadImage";


    public ImageLoader(Context context) {
        this.context = context;
    }

    /**
     * Load an image from the network
     * @param imageView {@link ImageView} to display the loaded image.
     * @param imageUrl url pointing to the image resource.
     * @param defaultImage default image to show before remote image has been downloaded.
     */
    public void loadImage(final ImageView imageView,String imageUrl,int defaultImage){
        if(!TextUtils.isEmpty(imageUrl)) {
            Glide
                    .with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .placeholder(defaultImage)
                            .fitCenter())
                    .into(imageView);
        }
    }

    public void loadImage(final ImageView imageView, String imageUrl, ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
        Log.i(TAG, imageUrl);
        Glide
                .with(context)
                .load(imageUrl)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);


                        return false;
                    }
                })
                .apply(new RequestOptions()
                        .placeholder(R.mipmap.default_image_holder)
                        .fitCenter())
                .into(imageView);
    }

    /**
     * imageView {@link ImageView} to display the loaded image.
     * @param imageUrl url pointing to the image resource.
     * @param imageView {@link ImageView} to display the loaded image.
     */
    public void loadImage(final ImageView imageView,String imageUrl){
        if(!TextUtils.isEmpty(imageUrl)) {
            Glide
                    .with(context)
                    .load(imageUrl)
                    .apply(new RequestOptions()
                            .placeholder(R.mipmap.default_image_holder)
                            .fitCenter())
                    .into(imageView);


        }
    }

}
