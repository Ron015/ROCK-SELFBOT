package com.dev.ron;

import android.app.Service;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Handler;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class UptimeService extends Service {

    private static final String CHANNEL_ID = "uptime_channel";
    private static final int NOTIF_ID = 101;
    private Handler handler;
    private Runnable updateRunnable;
    private long startTime;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        startTime = System.currentTimeMillis();

        handler = new Handler();
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                long elapsedMillis = System.currentTimeMillis() - startTime;
                String uptime = formatElapsedTime(elapsedMillis);

                Notification notification = new NotificationCompat.Builder(UptimeService.this, CHANNEL_ID)
                        .setContentTitle("🔥 Rock App Running")
                        .setContentText("Uptime: " + uptime)
                        .setSmallIcon(R.drawable.ic_on)
                        .setOngoing(true)
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .build();

                startForeground(NOTIF_ID, notification);
                handler.postDelayed(this, 1000); // update every second
            }
        };
        handler.post(updateRunnable);
    }

    private String formatElapsedTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Uptime Service Channel",
                NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(updateRunnable);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}