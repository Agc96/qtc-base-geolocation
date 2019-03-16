package com.qtcteam.scharff.geolocalization.presenter;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;
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
import com.qtcteam.scharff.geolocalization.utils.Permissions;

public class TrackingService extends Service {

    private static final String TAG = "QTC_GEO_SERVICE";
    private static final String ACTION_STOP = "stop";
    private static final String FIREBASE_PATH = "locations/%1$s";

    @Override
    public IBinder onBind (Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();
        requestLocationUpdates();
    }

    private void buildNotification () {
        registerReceiver(receiver, new IntentFilter(ACTION_STOP));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(this, 0,
                new Intent(ACTION_STOP), PendingIntent.FLAG_UPDATE_CURRENT);
        // Crear la notificación
        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(getString(R.string.tracking_notif_title))
                .setContentText(getString(R.string.tracking_notif_text))
                .setOngoing(true)
                .setContentIntent(broadcastIntent)
                .setSmallIcon(R.mipmap.ic_launcher);
        startForeground(1, builder.build());
    }

    protected BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "recieved stop broadcast");
            // Parar el servicio cuando se haga clic en la notificación
            unregisterReceiver(receiver);
            stopSelf();
        }
    };

    private void requestLocationUpdates () {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.e(TAG, "user is null, for some reason");
            return;
        }

        LocationRequest request = new LocationRequest()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult result) {
                    DatabaseReference db = FirebaseDatabase.getInstance()
                            .getReference(String.format(FIREBASE_PATH, user.getUid()));
                    Location location = result.getLastLocation();
                    if (location != null) {
                        Log.d(TAG, "location update");
                        db.setValue(location);
                    } else {
                        Log.d(TAG, "location is null");
                    }
                }
            }, null);
        }
    }

}
