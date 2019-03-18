package com.qtcteam.scharff.geolocalization.utils;

import android.content.Context;
import android.widget.Toast;

public class Utils {

    public static void showMessage (Context context, int stringId) {
        Toast.makeText(context, context.getString(stringId), Toast.LENGTH_SHORT).show();
    }

    public static void showMessage (Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

}
