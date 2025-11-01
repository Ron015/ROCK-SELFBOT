package com.dev.ron;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import androidx.core.content.ContextCompat;
import android.app.PendingIntent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;

public class IduJdh extends Fragment {

    private static final String TAG = "IduJdh";
    private static final String CONFIG_FILE_NAME = "config.json";
    private static final String DEFAULT_USERNAME = "User";
    private static final String STATUS_ONLINE = "Online";
    private static final String STATUS_OFFLINE = "Offline";

    // UI Components
    private Button btnStart, btnStop;
    private TextView uptimeValue, profileStatus, consoleOutput, UserName;
    private ScrollView consoleScroll;
    private CircleImageView profileImage;

    // System Components
    private Handler handler;
    private Runnable uptimeRunnable;
    private Typeface glitchFont;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
    private SharedPreferences sharedPrefs;
    private long startTime = 0;
    private StringBuilder logBuffer = new StringBuilder();
    private BroadcastReceiver statusReceiver;
    
    private final Handler logHandler = new Handler();
    private final int logRefreshInterval = 1000;
    
    private final Runnable logUpdater = new Runnable() {
        @Override
        public void run() {
            try {
                String updatedLogs = LyedYdhdUd.getConsoleLogs(requireContext());
                if (updatedLogs != null && !updatedLogs.equals(logBuffer.toString())) {
                    logBuffer = new StringBuilder(updatedLogs);
                    consoleOutput.setText(logBuffer.toString());
                }
            } catch (Exception e) {
                // No debug or toast as per Ron Bhai's rule 😎
            }
    
            // Repeat after interval
            logHandler.postDelayed(this, logRefreshInterval);
        }
    };
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            sharedPrefs = requireContext().getSharedPreferences("BotPrefs", Context.MODE_PRIVATE);
            glitchFont = Typeface.createFromAsset(requireContext().getAssets(), "fonts/glitchfont.ttf");
            setupStatusReceiver();
        } catch (Exception e) {
            Log.e(TAG, "Initialization failed", e);
        }
    }

    private void setupStatusReceiver() {
        statusReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleStatusUpdate();
            }
        };
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(statusReceiver, new IntentFilter("SERVICE_STATUS_UPDATE"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.yetjejstbd, container, false);
        initializeViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            setupConsole();
            loadUserProfile();
            LyedYdhdUd.trimConsoleLogFile(requireContext());
            loadConsoleLogs();
            logHandler.post(logUpdater);
            setupButtonListeners();
            updateUIFromStatus(getBotStatus());
            
            if (STATUS_ONLINE.equals(getBotStatus())) {
                startTime = sharedPrefs.getLong("StartTime", System.currentTimeMillis());
                startUptimeCounter();
            }
        } catch (Exception e) {
            logError("Setup failed", e);
        }
    }

    private void initializeViews(View view) {
        try {
            btnStart = view.findViewById(R.id.btn_start);
            btnStop = view.findViewById(R.id.btn_stop);
            uptimeValue = view.findViewById(R.id.uptime_value);
            profileStatus = view.findViewById(R.id.profile_status);
            consoleOutput = view.findViewById(R.id.console_output);
            consoleScroll = view.findViewById(R.id.console_scroll);
            UserName = view.findViewById(R.id.profile_name);
            profileImage = view.findViewById(R.id.profile_image);
            
            // Set initial button states
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
        } catch (Exception e) {
            logError("View initialization failed", e);
        }
    }

    private void setupConsole() {
        try {
            consoleOutput.setTypeface(glitchFont);
            consoleOutput.setMovementMethod(new ScrollingMovementMethod());
    
            // 🔽 Scroll to bottom after setup
            consoleOutput.post(() -> {
                int scrollAmount = consoleOutput.getLayout().getLineTop(consoleOutput.getLineCount()) - consoleOutput.getHeight();
                consoleOutput.scrollTo(0, Math.max(scrollAmount, 0));
            });
    
        } catch (Exception e) {
            logError("Console setup failed", e);
        }
    }

    private void loadUserProfile() {
        try {
            loadProfileImage();
            loadUserName();
        } catch (Exception e) {
            logError("Profile loading failed", e);
            UserName.setText(DEFAULT_USERNAME);
            profileImage.setImageResource(R.drawable.profile_placeholder);
        }
    }

    private void loadProfileImage() {
        String[] imageFiles = {"profile.webp", "profile.gif"};
        boolean imageLoaded = false;

        for (String imageFile : imageFiles) {
            try (FileInputStream imgStream = requireContext().openFileInput(imageFile)) {
                Bitmap bitmap = BitmapFactory.decodeStream(imgStream);
                if (bitmap != null) {
                    profileImage.setImageBitmap(bitmap);
                    imageLoaded = true;
                    break;
                }
            } catch (IOException e) {
                // Try next image
            }
        }

        if (!imageLoaded) {
            profileImage.setImageResource(R.drawable.profile_placeholder);
        }
    }

    private void loadUserName() {
        String savedName = DEFAULT_USERNAME;
        try {
            File configFile = new File(requireContext().getFilesDir(), CONFIG_FILE_NAME);
            
            if (!configFile.exists()) {
                createDefaultConfig();
            }

            try (FileInputStream fis = requireContext().openFileInput(CONFIG_FILE_NAME)) {
                int size = fis.available();
                byte[] buffer = new byte[size];
                fis.read(buffer);
                String jsonString = new String(buffer, "UTF-8");
                JSONObject jsonObject = new JSONObject(jsonString);
                savedName = jsonObject.optString("UserName", DEFAULT_USERNAME);
            }
        } catch (Exception e) {
            logError("Failed to load username", e);
            savedName = DEFAULT_USERNAME;
        }

        UserName.setText(savedName);
    }

    private void createDefaultConfig() throws IOException, JSONException {
        JSONObject defaultConfig = new JSONObject();
        defaultConfig.put("UserName", DEFAULT_USERNAME);
        
        try (FileOutputStream fos = requireContext().openFileOutput(CONFIG_FILE_NAME, Context.MODE_PRIVATE)) {
            fos.write(defaultConfig.toString().getBytes());
        }
    }

    private void loadConsoleLogs() {
        try {
            String savedLogs = LyedYdhdUd.getConsoleLogs(requireContext());
            logBuffer = new StringBuilder(savedLogs != null ? savedLogs : "");
            consoleOutput.setText(logBuffer.toString());
            startTime = sharedPrefs.getLong("StartTime", 0);
        } catch (Exception e) {
            logError("Failed to load console logs", e);
            logBuffer = new StringBuilder();
        }
    }

    private void handleStatusUpdate() {
        try {
            String status = getBotStatus();
            updateUIFromStatus(status);
            if (STATUS_ONLINE.equals(status)) {
                startTime = sharedPrefs.getLong("StartTime", System.currentTimeMillis());
                startUptimeCounter();
            } else {
                stopUptimeCounter();
                try {
                    clearLogs();
                    QkdidSjsj.StopS(requireContext());  // ✅ No object needed
                } catch (Exception e) {
                    logError("Stop service failed", e);
                }
            }
        } catch (Exception e) {
            logError("Status update failed", e);
        }
    }

    private void setupButtonListeners() {
        btnStart.setOnClickListener(v -> {
            try {
                handleStartButton();
            } catch (Exception e) {
                logError("Start button failed", e);
            }
        });

        btnStop.setOnClickListener(v -> {
            try {
                handleStopButton();
            } catch (Exception e) {
                logError("Stop button failed", e);
            }
        });
    }

    private void handleStartButton() {
        if (STATUS_ONLINE.equals(getBotStatus())) {
            logToConsole("Bot already ONLINE");
            updateUIFromStatus(STATUS_ONLINE);
            return;
        }

        if (isInternetAvailable(requireContext())) {
            startTime = System.currentTimeMillis();
            saveStartTime(startTime);
            setBotStatus(STATUS_ONLINE);

            try {
                updateUIFromStatus(STATUS_ONLINE);
                logToConsole("Service STARTED");
                Intent intent = new Intent(getActivity(), UptimeService.class);
                ContextCompat.startForegroundService(requireContext(), intent);
                
                try {
                    QkdidSjsj.StartS(requireContext()); // ✅ No object needed
                } catch (Exception e) {
                    logError("Start service helper failed", e);
                }

            } catch (SecurityException e) {
                logError("Security exception starting service", e);
                showToast("Permission denied");
            } catch (Exception e) {
                logError("Failed to start service", e);
                showToast("Service start failed");
            }
        } else {
            showToast("Check your internet connection");
            logToConsole("No internet connection available");
        }
    }

    private void handleStopButton() {
        if (STATUS_OFFLINE.equals(getBotStatus())) {
            logToConsole("Bot already OFFLINE");
            updateUIFromStatus(STATUS_OFFLINE);
            return;
        }

        setBotStatus(STATUS_OFFLINE);
        saveStartTime(0);
        
        try {
            Intent intent = new Intent(getActivity(), UptimeService.class);
            requireActivity().stopService(intent);
            updateUIFromStatus(STATUS_OFFLINE);
            
            try {
                clearLogs();
                QkdidSjsj.StopS(requireContext());  // ✅ No object needed
            } catch (Exception e) {
                logError("Stop service helper failed", e);
            }

        } catch (Exception e) {
            logError("Failed to stop service", e);
            showToast("Service stop failed");
        }
    }

    private void startUptimeCounter() {
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        
        if (uptimeRunnable != null) {
            handler.removeCallbacks(uptimeRunnable);
        }

        uptimeRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (STATUS_ONLINE.equals(getBotStatus())) {
                        long millis = System.currentTimeMillis() - startTime;
                        int seconds = (int) (millis / 1000);
                        int minutes = seconds / 60;
                        int hours = minutes / 60;
                        seconds %= 60;
                        minutes %= 60;

                        String uptimeText = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                        uptimeValue.setText(uptimeText);
                        handler.postDelayed(this, 1000);
                    }
                } catch (Exception e) {
                    logError("Uptime counter failed", e);
                }
            }
        };
        handler.post(uptimeRunnable);
    }

    private void stopUptimeCounter() {
        try {
            if (handler != null && uptimeRunnable != null) {
                handler.removeCallbacks(uptimeRunnable);
            }
            uptimeValue.setText("00:00:00");
        } catch (Exception e) {
            logError("Failed to stop uptime counter", e);
        }
    }

    public void logToConsole(String message) {
        try {
            String timestamp = timeFormat.format(new Date());
            String fullMessage = "> [" + timestamp + "] " + message + "\n";
            logBuffer.append(fullMessage);
            consoleOutput.setText(logBuffer.toString());
            LyedYdhdUd.saveConsoleLogs(logBuffer.toString(), requireContext());
            consoleScroll.post(() -> {
                try {
                    consoleScroll.fullScroll(View.FOCUS_DOWN);
                } catch (Exception e) {
                    logError("Scroll failed", e);
                }
            });
        } catch (Exception e) {
            logError("Logging failed", e);
        }
    }

    private void clearLogs() {
        try {
            logBuffer = new StringBuilder();
            consoleOutput.setText(logBuffer.toString());
            LyedYdhdUd.clearConsoleLogs(requireContext());
        } catch (Exception e) {
            logError("Failed to clear logs", e);
        }
    }

    private void setBotStatus(String status) {
        try {
            sharedPrefs.edit().putString("BotStatus", status).apply();
            LocalBroadcastManager.getInstance(requireContext())
                    .sendBroadcast(new Intent("SERVICE_STATUS_UPDATE"));
        } catch (Exception e) {
            logError("Failed to save bot status", e);
        }
    }

    private String getBotStatus() {
        try {
            return sharedPrefs.getString("BotStatus", STATUS_OFFLINE);
        } catch (Exception e) {
            logError("Failed to get bot status", e);
            return STATUS_OFFLINE;
        }
    }

    private void saveStartTime(long time) {
        try {
            sharedPrefs.edit().putLong("StartTime", time).apply();
        } catch (Exception e) {
            logError("Failed to save start time", e);
        }
    }

    private void updateUIFromStatus(String status) {
        try {
            if (STATUS_ONLINE.equals(status)) {
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                profileStatus.setText(STATUS_ONLINE);
                profileStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark));
            } else {
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
                profileStatus.setText(STATUS_OFFLINE);
                profileStatus.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark));
                uptimeValue.setText("00:00:00");
            }
        } catch (Exception e) {
            logError("UI update failed", e);
        }
    }

    private boolean isInternetAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    NetworkCapabilities nc = cm.getNetworkCapabilities(cm.getActiveNetwork());
                    return nc != null && nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
                } else {
                    // Deprecated but works for older versions
                    return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
                }
            }
            return false;
        } catch (Exception e) {
            logError("Internet check failed", e);
            return false;
        }
    }

    private void logError(String message, Exception e) {
        Log.e(TAG, message, e);
        logToConsole("ERROR: " + message + " - " + e.getMessage());
    }

    private void showToast(String message) {
        try {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Failed to show toast", e);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (handler != null && uptimeRunnable != null) {
                handler.removeCallbacks(uptimeRunnable);
            }
            logHandler.removeCallbacks(logUpdater);
        } catch (Exception e) {
            logError("Cleanup failed", e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logHandler.removeCallbacks(logUpdater);
        try {
            LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(statusReceiver);
        } catch (Exception e) {
            logError("Receiver unregister failed", e);
        }
    }
}