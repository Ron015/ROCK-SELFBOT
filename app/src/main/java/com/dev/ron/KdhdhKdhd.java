package com.dev.ron;

import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;
import android.content.SharedPreferences;
import okhttp3.*;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

public class KdhdhKdhd {
    public interface LicenseCallback {
        void onResult(boolean isValid, String licenseType);
    }

    public static void checkLicense(Context context, LicenseCallback callback) {

        if (!isInternetAvailable(context)) {
            callback.onResult(false, null);
            return;
        }

        String key = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .getString("license_keys", null);
        SharedPreferences prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);

        if (key == null) {
            Toast.makeText(context, "No previous login found!", Toast.LENGTH_SHORT).show();
            callback.onResult(false, null);
            return;
        }

        Toast.makeText(context, "Found stored key", Toast.LENGTH_SHORT).show();

        String deviceId = Base64.encodeToString(
                android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID).getBytes(),
                Base64.NO_WRAP
        );

        OkHttpClient client = NetworkClient.getClient();

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("key", key);
            jsonBody.put("device_id", deviceId);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

            

            Request request = new Request.Builder()
                    .url(AdnhdEjdjsS.decrypt(SekdJdhdJd.AksjsKsjdjJshdjs(), context))
                    .post(body)
                    .header("User-Agent", SekdJdhdJd.getAjdhKsH())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(context, () -> {
                        Toast.makeText(context, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        callback.onResult(false, null);
                    });
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String encryptedResponse = response.body().string();
                    
                    try {
                        String decrypted = DeKfhDjjjddh.decrypt(encryptedResponse, context);

                        JSONObject json = new JSONObject(decrypted);
                        boolean success = json.optBoolean("success", false);
                        int code = json.optInt("code", -1);
                        String message = json.optString("message", "No message");

                        runOnUiThread(context, () -> {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();

                            if (success && (code == 705 || code == 706)) {
                            String expiry = "";
                            String type = "";
                            try {
                                JSONObject data = json.getJSONObject("data");
                                expiry = data.optString("expiry", "");
                                type = data.optString("type", "");
                            } catch (Exception ignored) {}
                            callback.onResult(true, type);
                            prefs.edit()
                                    .putBoolean("isKeyValid", true)
                                    .putString("license_keys", key)
                                    .putString("expiry", expiry)
                                    .putString("licenseType", type)
                                    .apply();
                            
                        } else {
                                callback.onResult(false, null);
                            }
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(context, () -> {
                            callback.onResult(false, null);
                        });
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Error preparing request: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            callback.onResult(false, null);
        }
    }

    private static void runOnUiThread(Context context, Runnable runnable) {
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).runOnUiThread(runnable);
        } else {
            runnable.run();
        }
    }

    public static boolean isInternetAvailable(Context context) {
        android.net.ConnectivityManager cm = (android.net.ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            android.net.NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return nc != null && nc.hasCapability(android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }
}