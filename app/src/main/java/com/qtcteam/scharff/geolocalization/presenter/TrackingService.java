package com.qtcteam.scharff.geolocalization.presenter;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.qtcteam.scharff.geolocalization.R;

public class TrackingService extends Service {

    private static final String TAG = "QTC_GEO_SERVICE";
    private static final String FIREBASE_DB_LOCATIONS = "locations";
    private static final String FIREBASE_DB_ORDERS = "orders";
    private static final int LOCATION_INTERVAL = 10000;
    private static final int LOCATION_FAST_INTERVAL = 5000;
    private static final int NOTIFICATION_ID = 1002;

    private FirebaseUser user;
    private FusedLocationProviderClient client;
    private String order;

    @Override
    public IBinder onBind (Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            buildNotification();
            requestLocationUpdates();
        } else {
            Log.w(TAG, "user is null for some reason, aborting...");
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            order = intent.getStringExtra(TrackingPresenter.EXTRA_ORDER);
            if (order == null) {
                Log.w(TAG, "order is null for some reason, aborting...");
                stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void buildNotification () {
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.tracking_ntf_text))
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_tracker);
        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void requestLocationUpdates () {
        LocationRequest request = new LocationRequest()
                .setInterval(LOCATION_INTERVAL)
                .setFastestInterval(LOCATION_FAST_INTERVAL)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        client = LocationServices.getFusedLocationProviderClient(this);
        try {
            client.requestLocationUpdates(request, callback, null);
        } catch (SecurityException ex) {
            Log.d(TAG, "User revoked permission to access location, aborting...");
            stopSelf();
        }
    }

    private LocationCallback callback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult result) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference();

            Location location = result.getLastLocation();
            if (location != null) {
                Log.d(TAG, "location update");
                db.child(FIREBASE_DB_LOCATIONS).child(user.getUid()).setValue(location);
                if (order != null) {
                    db.child(FIREBASE_DB_ORDERS).child(order).setValue(user.getUid());
                }
            } else {
                Log.d(TAG, "location is null");
            }
        }
    };

    @Override
    public void onDestroy() {
        stopForeground(true);
        if (client != null) {
            client.removeLocationUpdates(callback);
        }
        super.onDestroy();
    }

}
