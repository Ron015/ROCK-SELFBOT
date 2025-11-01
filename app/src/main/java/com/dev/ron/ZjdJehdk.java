package com.dev.ron;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import android.graphics.drawable.GradientDrawable;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import org.json.JSONException;
import android.text.InputType;
import android.os.AsyncTask;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ZjdJehdk extends Fragment {

    private EditText tokenEdit, prefixEdit, afkMessage;
    private CheckBox afkEnabled, delCmdTrigger;
    private LinearLayout allowedIdContainer, autoReplyContainer, autoReplyUserContainer, autoReplyChannelContainer, copycatUserContainer;
    private EditText allowedIdInput, autoReplyInput, autoReplyUserInput, autoReplyChannelInput, copycatUserInput;

    private final String CONFIG_FILE_NAME = "config.json";

    public ZjdJehdk() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.jehgsjjdj, container, false);
        if (getActivity() != null) {
            getActivity().getWindow().setFlags(
                android.view.WindowManager.LayoutParams.FLAG_SECURE,
                android.view.WindowManager.LayoutParams.FLAG_SECURE
            );
        }
        // Initialize all views
        tokenEdit = view.findViewById(R.id.config_tokenEdit);
        prefixEdit = view.findViewById(R.id.config_prefixEdit);
        afkMessage = view.findViewById(R.id.config_afkMessage);
        afkEnabled = view.findViewById(R.id.config_afkEnabled);
        delCmdTrigger = view.findViewById(R.id.config_cmdtriggerdel);

        allowedIdInput = view.findViewById(R.id.config_allowedIdInput);
        allowedIdContainer = view.findViewById(R.id.config_allowedIdContainer);
        autoReplyInput = view.findViewById(R.id.config_autoReplyInput);
        autoReplyContainer = view.findViewById(R.id.config_autoReplyContainer);
        autoReplyUserInput = view.findViewById(R.id.config_autoReplyUserInput);
        autoReplyUserContainer = view.findViewById(R.id.config_autoReplyUserContainer);
        autoReplyChannelInput = view.findViewById(R.id.config_autoReplyChannelInput);
        autoReplyChannelContainer = view.findViewById(R.id.config_autoReplyChannelContainer);
        copycatUserInput = view.findViewById(R.id.config_copycatUserInput);
        copycatUserContainer = view.findViewById(R.id.config_copycatUserContainer);

        // Set Add Button Clicks
        setAddBtn(view, R.id.config_addAllowedIdBtn, allowedIdInput, allowedIdContainer);
        setAddBtn(view, R.id.config_addAutoReplyBtn, autoReplyInput, autoReplyContainer);
        setAddBtn(view, R.id.config_addAutoReplyUserBtn, autoReplyUserInput, autoReplyUserContainer);
        setAddBtn(view, R.id.config_addAutoReplyChannelBtn, autoReplyChannelInput, autoReplyChannelContainer);
        setAddBtn(view, R.id.config_addCopycatUserBtn, copycatUserInput, copycatUserContainer);

        // Save button
        view.findViewById(R.id.config_saveBtn).setOnClickListener(v -> saveData());

        // Load saved data
        loadData();

        return view;
    }

    private void setAddBtn(View root, int btnId, EditText input, LinearLayout container) {
        root.findViewById(btnId).setOnClickListener(v -> {
            String text = input.getText().toString().trim();
            if (!text.isEmpty()) {
                addItemToLayout(container, text);
                input.setText("");
            }
        });
    }

    private void addItemToLayout(LinearLayout container, String text) {
        LinearLayout itemLayout = new LinearLayout(requireContext());
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(0, 8, 0, 8);

        EditText editText = new EditText(requireContext());
        editText.setText(text);
        editText.setEnabled(false);
        editText.setTextColor(0xFFFFFFFF);
        editText.setBackground(null);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);

        // Create rounded 'X' button with style
        Button removeBtn = new Button(requireContext());
        removeBtn.setText("✕");
        removeBtn.setTextSize(12);
        removeBtn.setTextColor(0xFF9C27B0);
        removeBtn.setPadding(20, 10, 20, 10);

        // Create background drawable programmatically
        GradientDrawable bgDrawable = new GradientDrawable();
        bgDrawable.setColor(0x6c5ce7); // Purple
        bgDrawable.setCornerRadius(100); // Rounded
        bgDrawable.setStroke(2, 0xFF9C27B0); // White border

        removeBtn.setBackground(bgDrawable);

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        btnParams.setMargins(16, 0, 0, 0);
        removeBtn.setLayoutParams(btnParams);

        removeBtn.setOnClickListener(v -> container.removeView(itemLayout));

        itemLayout.addView(editText, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
        itemLayout.addView(removeBtn);
        container.addView(itemLayout);
    }

    private void saveData() {
        try {
            JSONObject data = new JSONObject();
            data.put("token", tokenEdit.getText().toString());
            data.put("prefix", prefixEdit.getText().toString());
            data.put("delete_commands", delCmdTrigger.isChecked());

            data.put("allowedID", getItemsFromLayout(allowedIdContainer));

            JSONObject afk = new JSONObject();
            afk.put("enabled", afkEnabled.isChecked());
            afk.put("message", afkMessage.getText().toString());
            data.put("afk", afk);

            JSONObject autoReply = new JSONObject();
            autoReply.put("users", getItemsFromLayout(autoReplyUserContainer));
            autoReply.put("channels", getItemsFromLayout(autoReplyChannelContainer));
            autoReply.put("messages", getItemsFromLayout(autoReplyContainer));
            data.put("autoreply", autoReply);

            JSONObject copycat = new JSONObject();
            copycat.put("users", getItemsFromLayout(copycatUserContainer));
            data.put("copycat", copycat);

            // Save the data to config.json
            FileOutputStream fos = requireContext().openFileOutput(CONFIG_FILE_NAME, Context.MODE_PRIVATE);
            fos.write(data.toString().getBytes());
            fos.close();


            Toast.makeText(requireContext(), "Saved!", Toast.LENGTH_SHORT).show();
            getUserInfo(requireContext());
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            FileInputStream fis = requireContext().openFileInput(CONFIG_FILE_NAME);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();

            String jsonStr = new String(buffer);
            if (jsonStr.isEmpty()) return;

            JSONObject data = new JSONObject(jsonStr);
            tokenEdit.setText(data.optString("token"));
            prefixEdit.setText(data.optString("prefix"));
            delCmdTrigger.setChecked(data.optBoolean("delete_commands", false));
            addItemsToLayout(allowedIdContainer, data.optJSONArray("allowedID"));

            JSONObject afk = data.optJSONObject("afk");
            if (afk != null) {
                afkEnabled.setChecked(afk.optBoolean("enabled", false));
                afkMessage.setText(afk.optString("message", ""));
            }

            JSONObject autoReply = data.optJSONObject("autoreply");
            if (autoReply != null) {
                addItemsToLayout(autoReplyUserContainer, autoReply.optJSONArray("users"));
                addItemsToLayout(autoReplyChannelContainer, autoReply.optJSONArray("channels"));
                addItemsToLayout(autoReplyContainer, autoReply.optJSONArray("messages"));
            }

            JSONObject copycat = data.optJSONObject("copycat");
            if (copycat != null) {
                addItemsToLayout(copycatUserContainer, copycat.optJSONArray("users"));
            }

        } catch (IOException | JSONException e) {
          e.printStackTrace();
        }
    }

    private JSONArray getItemsFromLayout(LinearLayout container) {
        JSONArray arr = new JSONArray();
        for (int i = 0; i < container.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) container.getChildAt(i);
            EditText et = (EditText) layout.getChildAt(0);
            arr.put(et.getText().toString());
        }
        return arr;
    }

    private void addItemsToLayout(LinearLayout container, JSONArray array) {
        if (array == null) return;
        for (int i = 0; i < array.length(); i++) {
            addItemToLayout(container, array.optString(i));
        }
    }

    public void getUserInfo(Context context) {
    new Thread(() -> {
        try {
            File internalConfig = new File(context.getFilesDir(), "config.json");

            // Step 1: Copy from assets if not already copied
            if (!internalConfig.exists()) {
                try (InputStream is = context.getAssets().open("config.json");
                     FileOutputStream fos = new FileOutputStream(internalConfig)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, length);
                    }
                }
            }

            // Step 2: Read token and existing config
            String userToken = null;
            JSONObject configJson = null;
            try {
                FileInputStream fis = new FileInputStream(internalConfig);
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();

                String jsonStr = new String(buffer, "UTF-8");
                configJson = new JSONObject(jsonStr);
                userToken = configJson.optString("token", null);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            if (userToken == null || userToken.isEmpty()) return;

            // Step 3: Call Discord API
            URL url = new URL("https://discord.com/api/v10/users/@me");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", userToken);

            int responseCode = conn.getResponseCode();

            if (responseCode == 401) {
                // Invalid token — clear it & show toast
                if (configJson != null) {
                    configJson.put("token", "");
                    try (FileOutputStream fos = new FileOutputStream(internalConfig)) {
                        fos.write(configJson.toString(4).getBytes());
                    }
                }

                // Show Toast on UI thread
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> Toast.makeText(context, "Invalid Discord token!", Toast.LENGTH_LONG).show());
                conn.disconnect();
                return;
            }

            if (responseCode != 200) {
                conn.disconnect();
                return;
            }

            // Step 4: Get user info
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) response.append(line);
            reader.close();
            conn.disconnect();

            JSONObject json = new JSONObject(response.toString());
            String userId = json.getString("id");
            String avatarHash = json.getString("avatar");
            String username = json.getString("username");

            // Step 5: Update username AND add user ID to allowedID
            if (configJson != null) {
                configJson.put("UserName", username);
                
                // Add user ID to allowedID array
                JSONArray allowedID = configJson.optJSONArray("allowedID");
                if (allowedID == null) {
                    allowedID = new JSONArray();
                }
                
                // Check if user ID already exists in allowedID
                boolean userIdExists = false;
                for (int i = 0; i < allowedID.length(); i++) {
                    if (userId.equals(allowedID.optString(i))) {
                        userIdExists = true;
                        break;
                    }
                }
                
                // Add user ID if it doesn't exist
                if (!userIdExists) {
                    allowedID.put(userId);
                    configJson.put("allowedID", allowedID);
                }
                
                try (FileOutputStream fos = new FileOutputStream(internalConfig)) {
                    fos.write(configJson.toString(4).getBytes());
                }
            }

            // Step 6: Build avatar URL
            String avatarUrl = avatarHash.startsWith("a_")
                    ? "https://cdn.discordapp.com/avatars/" + userId + "/" + avatarHash + ".gif?size=1024"
                    : "https://cdn.discordapp.com/avatars/" + userId + "/" + avatarHash + ".webp?size=1024";

            // Step 7: Delete old image
            File profileFile = new File(context.getFilesDir(), "profile.webp");
            if (profileFile.exists()) profileFile.delete();

            // Step 8: Download new avatar
            URL imageUrl = new URL(avatarUrl);
            HttpURLConnection imgConn = (HttpURLConnection) imageUrl.openConnection();
            InputStream input = imgConn.getInputStream();

            FileOutputStream output = context.openFileOutput("profile.webp", Context.MODE_PRIVATE);
            byte[] imageBuffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = input.read(imageBuffer)) != -1) {
                output.write(imageBuffer, 0, bytesRead);
            }

            output.close();
            input.close();
            imgConn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null) {
            getActivity().getWindow().clearFlags(
                android.view.WindowManager.LayoutParams.FLAG_SECURE
            );
        }
    }

}