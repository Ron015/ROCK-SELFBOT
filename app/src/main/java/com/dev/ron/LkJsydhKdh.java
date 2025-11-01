package com.dev.ron;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import okhttp3.*;

import org.json.JSONObject;

import java.io.IOException;

public class LkJsydhKdh extends AppCompatActivity {

    EditText keyInput;
    Button verifyBtn;
    ProgressDialog dialog;
    SharedPreferences prefs;
    private final OkHttpClient client = NetworkClient.getClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lsjyyrhye);

        keyInput = findViewById(R.id.licenseKeyInput);
        verifyBtn = findViewById(R.id.verifyBtn);
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
        verifyBtn.setOnClickListener(v -> {
            try {
                String enteredKey = keyInput.getText().toString().trim();
                if (enteredKey.isEmpty()) {
                    Toast.makeText(LkJsydhKdh.this, "Please enter a license key", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.setMessage("Verifying key...");
                dialog.show();

                String deviceId = android.util.Base64.encodeToString(
                        Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID).getBytes(),
                        android.util.Base64.NO_WRAP);

                sendVerificationRequest(enteredKey, deviceId);
            } catch (Exception e) {
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendVerificationRequest(String key, String deviceId) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("key", key);
            jsonBody.put("device_id", deviceId);
        } catch (Exception e) {
            e.printStackTrace();
            dialog.dismiss();
            Toast.makeText(this, "JSON error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody body = RequestBody.create(jsonBody.toString(), MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(AdnhdEjdjsS.decrypt(SekdJdhdJd.AksjsKsjdjJshdjs(), LkJsydhKdh.this))
                .post(body)
                .header("User-Agent", SekdJdhdJd.getAjdhKsH())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    dialog.dismiss();
                    Toast.makeText(LkJsydhKdh.this, "Network error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String encryptedResponse = response.body().string();

                try {
                    String decrypted = DeKfhDjjjddh.decrypt(encryptedResponse, LkJsydhKdh.this);

                    if (decrypted == null) {
                        throw new Exception("Something went wrong");
                    }

                    JSONObject json = new JSONObject(decrypted);

                    boolean success = json.optBoolean("success", false);
                    int code = json.optInt("code", -1);
                    String message = json.optString("message", "No message");

                    runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(LkJsydhKdh.this, "Server: " + message, Toast.LENGTH_LONG).show();

                        if (success && (code == 705 || code == 706)) {
                            String expiry = "";
                            String type = "";
                            try {
                                JSONObject data = json.getJSONObject("data");
                                expiry = data.optString("expiry", "");
                                type = data.optString("type", "");
                            } catch (Exception ignored) {}

                            prefs.edit()
                                    .putBoolean("isKeyValid", true)
                                    .putString("license_keys", key)
                                    .putString("expiry", expiry)
                                    .putString("licenseType", type)
                                    .apply();

                            startActivity(new Intent(LkJsydhKdh.this, ZkUdYs.class));
                            finish();
                        }
                    });

                } catch (Exception e) {
                    runOnUiThread(() -> {
                        dialog.dismiss();
                        Toast.makeText(LkJsydhKdh.this, "Try Again", Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }
}