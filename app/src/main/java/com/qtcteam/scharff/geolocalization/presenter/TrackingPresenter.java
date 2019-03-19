package com.qtcteam.scharff.geolocalization.presenter;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.qtcteam.scharff.geolocalization.R;
import com.qtcteam.scharff.geolocalization.utils.Utils;
import com.qtcteam.scharff.geolocalization.view.TrackingActivity;

public class TrackingPresenter implements Presenter {

    private final static String TAG = "QTC_GEO_TRACKING_PRES";
    /* package */ final static String EXTRA_ORDER = "ORDER";

    private TrackingActivity view;
    private boolean hasService;

    public TrackingPresenter (TrackingActivity activity, boolean hasService) {
        this.view = activity;
        this.hasService = hasService;
    }

    public boolean isGPSEnabled () {
        LocationManager lm = (LocationManager) view.getSystemService(Context.LOCATION_SERVICE);
        if (lm == null) {
            Utils.showMessage(view, R.string.tracking_msg_no_gps);
            return false;
        }
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Utils.showMessage(view, R.string.tracking_msg_gps_inactive);
            return false;
        }
        Log.d(TAG, "location manager tests passed");
        return true;
    }

    public boolean validateOrder (String order) {
        if (order == null || order.isEmpty()) {
            Utils.showMessage(view, R.string.tracking_msg_order_empty);
            return false;
        }
        return true;
    }

    public void startService (String order) {
        if (hasService) {
            Log.d(TAG, "view is null or service is already in execution");
            return;
        }
        try {
            Log.d(TAG, "starting service");
            Intent intent = new Intent(view, TrackingService.class);
            intent.putExtra(EXTRA_ORDER, order);
            // Iniciar el servicio y actualizar hasService si se inició correctamente
            hasService = (view.startService(intent) != null);
        } catch (SecurityException ex) {
            Log.d(TAG, "lacking permissions to start service, or service not found");
        }
    }

    public boolean getHasService () {
        return hasService;
    }

    public void stopService () {
        if (!hasService) {
            Log.d(TAG, "view is null or no service is executed");
            return;
        }
        try {
            Log.d(TAG, "stopping service");
            // Detener el servicio y actualizar hasService si se inició correctamente
            hasService = !view.stopService(new Intent(view, TrackingService.class));
        } catch (SecurityException ex) {
            Log.d(TAG, "lacking permissions to stop service, or service not found");
        }
    }

    @Override
    public void onDestroy() {
        this.view = null;
    }

}
