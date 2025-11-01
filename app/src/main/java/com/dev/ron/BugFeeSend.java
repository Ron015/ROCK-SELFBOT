package com.dev.ron;

import android.content.Context;
import android.util.Base64;
import android.widget.Toast;

import androidx.annotation.NonNull;

import okhttp3.*;

import org.json.JSONObject;

import java.io.IOException;

public class BugFeeSend {

    public static void sendBugFee(Context context, String type, String userMessage) {

        if (!isInternetAvailable(context)) {
            Toast.makeText(context, "No internet connection!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get license key from SharedPreferences
        String key = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
                .getString("license_keys", null);

        if (key == null) {
            // No key found, silently fail (as per your no-debug rule)
            return;
        }

        // Encode device ID
        String deviceId = Base64.encodeToString(
                android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID).getBytes(),
                Base64.NO_WRAP
        );

        OkHttpClient client = NetworkClient.getClient();

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("key", key);
            jsonBody.put("device_id", deviceId);
            jsonBody.put(type, true);
            jsonBody.put("message", userMessage);

            RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

            Request request = new Request.Builder()
                    .url(AdnhdEjdjsS.decrypt(SekdJdhdJd.AksjsKsjdjJshdjs(), context))
                    .post(body)
                    .header("User-Agent", SekdJdhdJd.getAjdhKsH())
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    // No Toast or debug message
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String encryptedResponse = response.body().string();

                    try {
                        String decrypted = DeKfhDjjjddh.decrypt(encryptedResponse, context);
                        JSONObject json = new JSONObject(decrypted);
                        String message = json.optString("message", "No message");

                        runOnUiThread(context, () -> {
                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                        });

                    } catch (Exception e) {
                        // Silently ignore decryption failure
                    }
                }
            });

        } catch (Exception e) {
            // Silently ignore request building error
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