package com.dev.ron;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.content.Intent;
import android.net.Uri;
import androidx.fragment.app.Fragment;
import android.animation.ObjectAnimator;
import android.view.Gravity;
import android.view.animation.DecelerateInterpolator;
import org.json.JSONObject;
import android.util.Base64;
import android.provider.Settings;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class IrhTnQd extends Fragment {

    private String currentVersion;
    private SharedPreferences prefs;
    private View fragmentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.kgtkejdt, container, false);
        prefs = requireContext().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE);
        currentVersion = getAppVersion();
        initViews(fragmentView);
        return fragmentView;
    }

    private void initViews(View view) {
        try {
            TextView footer = view.findViewById(R.id.setting_footer);
            TextView licenseType = view.findViewById(R.id.setting_license_type);
            TextView licenseExpiry = view.findViewById(R.id.setting_license_expiry);
            TextView versionText = view.findViewById(R.id.settings_version_text);

            footer.setText(SekdJdhdJd.getFJdhJejehsh());
            licenseType.setText("License Type: " + prefs.getString("licenseType", "Error"));
            licenseExpiry.setText("Expiry Time: " + prefs.getString("expiry", "Error"));
            versionText.setText("Version: " + currentVersion);

            setupButtonListeners(view);
            updateButtonSizes(view);

        } catch (Exception e) {
            Log.e("InitViews", "Error initializing views", e);
        }
    }

    private void setupButtonListeners(View view) {
        try {
            view.findViewById(R.id.setting_check_update).setOnClickListener(v -> new FetchVersionTask().execute());
            view.findViewById(R.id.setting_clear_logs).setOnClickListener(v -> clearLogs());
            view.findViewById(R.id.setting_feedback).setOnClickListener(v ->
                    showDialog("Send Feedback", "feedback"));
            view.findViewById(R.id.setting_bug).setOnClickListener(v ->
                    showDialog("Report Bug", "bug"));
            view.findViewById(R.id.setting_privacy_policy).setOnClickListener(v -> showPrivacyPolicy());
            view.findViewById(R.id.setting_battery).setOnClickListener(v -> 
                    BatteryHelper.requestIgnoreBatteryOptimization(requireActivity()));

            Button clearSnipeBtn = view.findViewById(R.id.setting_clear_snipe_db);
            Button clearBirthdayBtn = view.findViewById(R.id.setting_clear_birthday_db);
            Button clearRatingBtn = view.findViewById(R.id.setting_clear_ratings_db);

            clearSnipeBtn.setOnClickListener(v -> {
                clearDatabaseFiles("edited_messages.json", "deleted_messages.json", "Snipe");
                updateButtonSizes(view);
            });

            clearBirthdayBtn.setOnClickListener(v -> {
                clearDatabaseFiles("birthdays.json", null, "Birthday");
                updateButtonSizes(view);
            });
            
            clearRatingBtn.setOnClickListener(v -> {
                clearDatabaseFiles("user_ratings.json", null, "Rating");
                updateButtonSizes(view);
            });

        } catch (Exception e) {
            Log.e("ButtonListeners", "Error setting listeners", e);
        }
    }

    private void updateButtonSizes(View view) {
        try {
            Button clearSnipeBtn = view.findViewById(R.id.setting_clear_snipe_db);
            Button clearBirthdayBtn = view.findViewById(R.id.setting_clear_birthday_db);

            String snipeSize = getCombinedFileSize("edited_messages.json", "deleted_messages.json");
            String birthdaySize = getCombinedFileSize("birthdays.json");

            clearSnipeBtn.setText("Clear Snipe DB (" + snipeSize + ")");
            clearBirthdayBtn.setText("Clear Birthday DB (" + birthdaySize + ")");

        } catch (Exception e) {
            Log.e("UpdateSizes", "Error updating file sizes", e);
        }
    }

    private String getCombinedFileSize(String... filenames) {
        File appDataDir = new File(requireContext().getFilesDir(), "database");
        if (!appDataDir.exists()) return "0 KB";

        long totalSize = 0;
        for (String filename : filenames) {
            if (filename == null) continue;
            File file = new File(appDataDir, filename);
            if (file.exists()) {
                totalSize += file.length();
            }
        }
        return formatFileSize(totalSize);
    }

    private String formatFileSize(long size) {
        if (size == 0) return "0 KB";
        if (size < 1024) return size + " B";
        int exp = (int) (Math.log(size) / Math.log(1024));
        char unit = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", size / Math.pow(1024, exp), unit);
    }

    private void clearDatabaseFiles(String primaryFile, String secondaryFile, String dbName) {
        try {
            File appDataDir = new File(requireContext().getFilesDir(), "database");
            if (!appDataDir.exists()) {
                showToast(dbName + " database doesn't exist");
                return;
            }

            boolean success = true;
            success &= deleteFile(new File(appDataDir, primaryFile));
            if (secondaryFile != null) success &= deleteFile(new File(appDataDir, secondaryFile));

            if (success) {
                showToast(dbName + " database cleared successfully");
            } else {
                showToast("Partially cleared " + dbName + " database");
            }
        } catch (Exception e) {
            Log.e("ClearDB", "Error clearing " + dbName + " database", e);
            showToast("Failed to clear " + dbName + " database");
        }
    }

    private boolean deleteFile(File file) {
        if (file.exists()) {
            boolean deleted = file.delete();
            if (!deleted) Log.w("FileDelete", "Failed to delete: " + file.getAbsolutePath());
            return deleted;
        }
        return true;
    }

    private void clearLogs() {
        try {
            LyedYdhdUd.clearLogs(requireContext());
            showToast("Logs Cleared");
        } catch (Exception e) {
            Log.e("ClearLogs", "Error clearing logs", e);
        }
    }

    private void showToast(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialog(String title, String Rtype) {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.oeiskshsfb, null);
        AlertDialog dialog = new AlertDialog.Builder(getContext()).setView(dialogView).create();

        // Set transparent background & animation
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;

        TextView titleView = dialogView.findViewById(R.id.settings_dialog_title);
        EditText input = dialogView.findViewById(R.id.settings_dialog_input);
        Button cancel = dialogView.findViewById(R.id.settings_dialog_cancel);
        Button send = dialogView.findViewById(R.id.settings_dialog_send);

        titleView.setText(title);

        ObjectAnimator animator = ObjectAnimator.ofFloat(dialogView, "translationY", 100f, 0f);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setDuration(300);
        animator.start();

        cancel.setOnClickListener(v -> dialog.dismiss());

        send.setOnClickListener(v -> {
            String message = input.getText().toString().trim();
            if (message.isEmpty()) {
                input.setError("Please write something...");
                return;
            }
            BugFeeSend.sendBugFee(requireContext(), Rtype, message);
            dialog.dismiss();
            Toast.makeText(requireContext(), "Sending...", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void showPrivacyPolicy() {
        try {
            WebView webView = new WebView(requireContext());
            webView.setWebViewClient(new WebViewClient());
            webView.loadUrl("file:///android_asset/privacy_policy.html");

            new AlertDialog.Builder(requireContext())
                    .setTitle("Privacy Policy")
                    .setView(webView)
                    .setPositiveButton("Close", null)
                    .show();
        } catch (Exception e) {
            Log.e("PrivacyPolicy", "Error loading privacy policy", e);
        }
    }

    private class FetchVersionTask extends AsyncTask<Void, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL("https://api.github.com/repos/ron015/ROCK-SELFBOT/releases/latest").openConnection();
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
    
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
    
                return new JSONObject(response.toString());
            } catch (Exception e) {
                Log.e("FetchVersion", "Error fetching version", e);
                return null;
            }
        }
    
        @Override
        protected void onPostExecute(JSONObject response) {
            try {
                if (response == null) throw new Exception("No response");
    
                String latestTag = response.getString("tag_name");  // e.g. v1.1
                String latestVersion = latestTag.replaceFirst("^v", "");  // Remove leading 'v'
                String changelog = response.getString("body");
                String updateUrl = response.getString("html_url");
    
                if (!normalizeVersion(currentVersion).equals(normalizeVersion(latestVersion))) {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Update Available 🚀")
                            .setMessage(String.format(
                                    "Current Version: %s\nLatest Version: %s\n\nChanges:\n%s",
                                    currentVersion, latestVersion, changelog))
                            .setPositiveButton("Update", (d, w) -> {
                                startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse(updateUrl)));
                            })
                            .setNegativeButton("Later", null)
                            .show();
                } else {
                    showToast("✅ You have the latest version");
                }
            } catch (Exception e) {
                showToast("❌ Failed to check for updates");
                Log.e("UpdateCheck", "Error", e);
            }
        }
    
        private String normalizeVersion(String version) {
            return version.replaceFirst("^v", "").trim();
        }
    }
    private String getAppVersion() {
        try {
            return requireContext()
                   .getPackageManager()
                   .getPackageInfo(requireContext().getPackageName(), 0)
                   .versionName;
        } catch (Exception e) {
            Log.e("VersionFetch", "Error fetching app version", e);
            return "Unknown";
        }
    }
}
