package com.qtcteam.scharff.geolocalization.view;

import android.Manifest;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.qtcteam.scharff.geolocalization.R;
import com.qtcteam.scharff.geolocalization.presenter.TrackingPresenter;
import com.qtcteam.scharff.geolocalization.utils.Permissions;

public class TrackingActivity extends AppCompatActivity {

    private static final String TAG = "QTC_GEO_TRACKING";

    private static final int REQUEST_PERMISSIONS = 1001;
    private static final String HAS_SERVICE = "HAS_SERVICE";

    private TrackingPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        setTitle(R.string.tracking_title);

        if (savedInstanceState != null) {
            Log.d(TAG, "retrieving from bundle");
            boolean hasService = savedInstanceState.getBoolean(HAS_SERVICE);
            presenter = new TrackingPresenter(this, hasService);
        } else {
            Log.d(TAG, "bundle is null");
            presenter = new TrackingPresenter(this, false);
        }
    }

    public void startTracking (View v) {
        // Verificar que se tengan los permisos necesarios, en caso no se tengan solicitarlos
        if (presenter.isGPSEnabled() && Permissions.checkOrRequest(this,
                REQUEST_PERMISSIONS, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Iniciar el servicio
            presenter.startService();
        }
    }

    public void stopTracking (View v) {
        presenter.stopService();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(HAS_SERVICE, presenter.getHasService());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSIONS:
                if (Permissions.checkGrantResults(permissions, grantResults)) {
                    presenter.startService();
                } else {
                    showPermissionsDialog();
                }
                break;
        }
    }

    private void showPermissionsDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setMessage(R.string.tracking_dlg_allow_permissions)
                .setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Destroying tracking activity");
        presenter.onDestroy();
        super.onDestroy();
    }

}
