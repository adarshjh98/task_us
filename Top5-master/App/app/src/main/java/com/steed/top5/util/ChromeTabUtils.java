package com.steed.top5.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;


import androidx.browser.customtabs.CustomTabsIntent;

import com.steed.top5.R;


public class ChromeTabUtils {
    private  String TAG= "ChromeTabUtils";
    private final String chromePackageName = "com.android.chrome";
    private Context mContext;

    public ChromeTabUtils(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Open a url in Chrome tabs.
     * @param url
     */
    public void openUrl(String url){
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

        // Changes the background color for the omnibox. colorInt is an int
        // that specifies a Color.
        builder.setToolbarColor(mContext.getResources().getColor(R.color.colorPrimary));
        CustomTabsIntent customTabsIntent = builder.build();



        if(isChromeInstalled()){
            customTabsIntent.intent.setPackage(chromePackageName);
        }
        customTabsIntent.launchUrl(mContext, Uri.parse(url));
    }

    private boolean isChromeInstalled(){
        PackageManager packageManager = mContext.getPackageManager();
        Intent intent =new Intent();
        intent.setPackage(chromePackageName);
        if (intent.resolveActivity(packageManager) != null) {
            return true;
        }

        return false;
    }

}