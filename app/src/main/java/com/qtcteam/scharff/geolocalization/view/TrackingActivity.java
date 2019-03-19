package com.qtcteam.scharff.geolocalization.view;

import android.Manifest;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.qtcteam.scharff.geolocalization.R;
import com.qtcteam.scharff.geolocalization.presenter.TrackingPresenter;
import com.qtcteam.scharff.geolocalization.utils.Permissions;

public class TrackingActivity extends AppCompatActivity {

    private static final String TAG = "QTC_GEO_TRACKING";
    private static final String HAS_SERVICE = "HAS_SERVICE";
    private static final int REQUEST_PERMISSIONS = 1001;

    private TrackingPresenter presenter;
    private EditText mOrder;

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

        mOrder = findViewById(R.id.tracking_input_order);
    }

    public void startTracking (View v) {
        String order = mOrder.getText().toString();
        if (!presenter.validateOrder(order)) return;

        // Verificar que se tengan los permisos necesarios, en caso no se tengan solicitarlos
        if (presenter.isGPSEnabled() && Permissions.checkOrRequest(this,
                REQUEST_PERMISSIONS, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Iniciar el servicio
            presenter.startService(order);
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
                    String order = mOrder.getText().toString();
                    if (presenter.validateOrder(order)) {
                        presenter.startService(order);
                    }
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
