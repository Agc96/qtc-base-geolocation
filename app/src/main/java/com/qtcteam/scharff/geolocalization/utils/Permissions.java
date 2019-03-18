package com.qtcteam.scharff.geolocalization.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class Permissions {

    private static final String TAG = "QTC_GEO_PERMISSIONS";

    public static boolean checkOrRequest (Activity activity, int requestCode, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                // Log.d(TAG, "needs permissions, requesting");
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
                return false;
            }
        }
        // Log.d(TAG, "all permissions were already approved");
        return true;
    }

    public static boolean checkGrantResults (String[] permissions, int[] grantResults) {
        if (permissions.length == 0 || grantResults.length == 0) {
            // Log.d(TAG, "user cancelled permissions");
            return false;
        }
        for (int grantResult : grantResults) {
            if (grantResult != PackageManager.PERMISSION_GRANTED) {
                // Log.d(TAG, String.format("permission %s was not granted", permissions[i]));
                return false;
            }
        }
        // Log.d(TAG, "all permissions were granted");
        return true;
    }

}
