package com.chuangweizong.opencv.activity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.provider.Settings;

import java.util.List;

public class ForwardUtil {

//    public static final String TCT_ACTION_MANAGE_APP_PERMISSIONS = "android.intent.action.tct.MANAGE_APP_PERMISSIONS";
    public static final String TCT_ACTION_MANAGE_APP = "android.intent.action.tct.MANAGE_PERMISSIONS";
    public static final String TCT_EXTRA_PACKAGE_NAME = "android.intent.extra.tct.PACKAGE_NAME";

    public static void gotoSettings(Context context){
        Intent intent;
        if(isIntentExisting(context, TCT_ACTION_MANAGE_APP)){
            //Goto setting application permission
            intent = new Intent(TCT_ACTION_MANAGE_APP);
            intent.putExtra(TCT_EXTRA_PACKAGE_NAME, context.getPackageName());
        }else {
            //Goto settings details
            final Uri packageURI = Uri.parse("package:" + context.getPackageName());
            intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
        }

        context.startActivity(intent);
    }

    public static boolean isIntentExisting(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        List<ResolveInfo> resolveInfo =
                packageManager.queryIntentActivities(intent,
                        PackageManager.MATCH_ALL);
        if (resolveInfo.size() > 0) {
            return true;
        }
        return false;
    }
}
