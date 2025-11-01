package com.dev.ron;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

public class BatteryHelper {

    public static void requestIgnoreBatteryOptimization(Activity activity) {
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);

        if (pm != null && !pm.isIgnoringBatteryOptimizations(activity.getPackageName())) {
            Toast.makeText(activity, "Requesting to ignore battery optimization...", Toast.LENGTH_LONG).show();

            Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(intent);
        } else {
            Toast.makeText(activity, "Battery optimization already ignored! ✅", Toast.LENGTH_SHORT).show();
        }
    }
}