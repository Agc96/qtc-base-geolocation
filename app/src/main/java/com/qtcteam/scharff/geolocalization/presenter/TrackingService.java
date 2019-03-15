package com.qtcteam.scharff.geolocalization.presenter;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.qtcteam.scharff.geolocalization.R;

public class TrackingService extends Service {

    private static final String TAG = "QTC_GEO_SERVICE";
    private static final String ACTION_STOP = "stop";

    @Override
    public IBinder onBind (Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        buildNotification();
        loginToFirebase();
    }

    private void buildNotification () {
        registerReceiver(stopReciever, new IntentFilter(ACTION_STOP));
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

    protected BroadcastReceiver stopReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "recieved stop broadcast");
            // Parar el servicio cuando se haga clic en la notificación
            unregisterReceiver(stopReciever);
            stopSelf();
        }
    };

    private void loginToFirebase () {
        // TODO
    }

    private void requestLocationUpdates () {
        // TODO
    }
}
