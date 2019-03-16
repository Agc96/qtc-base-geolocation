package com.qtcteam.scharff.geolocalization.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.qtcteam.scharff.geolocalization.R;
import com.qtcteam.scharff.geolocalization.presenter.TrackingService;
import com.qtcteam.scharff.geolocalization.utils.Permissions;

public class TrackingActivity extends AppCompatActivity {

    private static final String TAG = "QTC_GEO_TRACKING";
    private static final int REQUEST_PERMISSIONS = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        setTitle(R.string.tracking_title);

        // Check if GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm == null) {
            Toast.makeText(this, R.string.tracking_msg_no_gps, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, R.string.tracking_msg_gps_inactive, Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "location manager tests passed");

        // Verificar que se tengan los permisos necesarios, en caso no se tengan
        if (Permissions.checkOrRequest(this, REQUEST_PERMISSIONS,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            startTrackingService();
        }
    }

    private void startTrackingService() {
        Log.d(TAG, "starting service");
        startService(new Intent(this, TrackingService.class));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS && Permissions.checkGrantResults(grantResults)) {
            // Start the service when the permission is granted
            startTrackingService();
        }
    }

}
