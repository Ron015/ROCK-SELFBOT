package com.dev.ron;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class CcJdhdjd extends Fragment {

    private static final String CMD = "custom_cmd.json";
    private LinearLayout commandsListContainer;
    private EditText cmdsearchBar;
    private TextWatcher searchWatcher;
    private View formOverlay;
    private TextInputEditText nameField, descriptionField, responseField, aliasInput, 
                            allowedUserInput, allowedChannelInput, allowedGuildInput;
    private LinearLayout aliasesContainer, allowedUsersContainer, allowedChannelsContainer, allowedGuildsContainer;
    private Button addCommandBtn;
    private SwitchCompat withPrefixSwitch, allowedGlobalUsersSwitch, replySwitch, deleteCommandTriggerSwitch;
    private AutoCompleteTextView allowedInDropdown;
    private TextView commandConfigTitle;
    private String currentEditingCommand = null;

    public CcJdhdjd() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cckshsjsks, container, false);
        initializeViews(view);
        setupForm();
        setupDropdowns();
        setupButtonListeners(view);
        
        loadCommands();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            cmdsearchBar = getActivity().findViewById(R.id.search_input);
            searchWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    loadCommands(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            };
            cmdsearchBar.addTextChangedListener(searchWatcher);
        }
    }

    private void initializeViews(View view) {
        commandsListContainer = view.findViewById(R.id.commandsListContainer);
        formOverlay = view.findViewById(R.id.formOverlay);
        addCommandBtn = view.findViewById(R.id.add_custom);
        commandConfigTitle = view.findViewById(R.id.CommandConfig);
        
        // Form fields
        nameField = view.findViewById(R.id.nameField);
        descriptionField = view.findViewById(R.id.descriptionField);
        responseField = view.findViewById(R.id.responseField);
        aliasInput = view.findViewById(R.id.aliasInput);
        allowedUserInput = view.findViewById(R.id.allowedUserInput);
        allowedChannelInput = view.findViewById(R.id.allowedChannelInput);
        allowedGuildInput = view.findViewById(R.id.allowedGuildInput);
        
        // Containers
        aliasesContainer = view.findViewById(R.id.aliasesContainer);
        allowedUsersContainer = view.findViewById(R.id.allowedUsersContainer);
        allowedChannelsContainer = view.findViewById(R.id.allowedChannelsContainer);
        allowedGuildsContainer = view.findViewById(R.id.allowedGuildsContainer);
        
        // Switches
        withPrefixSwitch = view.findViewById(R.id.withPrefixSwitch);
        allowedGlobalUsersSwitch = view.findViewById(R.id.allowedGlobalUsersSwitch);
        replySwitch = view.findViewById(R.id.replySwitch);
        deleteCommandTriggerSwitch = view.findViewById(R.id.deleteCommandTriggerSwitch);
        
        // Dropdown
        allowedInDropdown = view.findViewById(R.id.allowedInDropdown);
    }

    private void setupForm() {
        formOverlay.setVisibility(View.GONE);
        withPrefixSwitch.setChecked(true);
        allowedGlobalUsersSwitch.setChecked(true);
        replySwitch.setChecked(true);
        deleteCommandTriggerSwitch.setChecked(false);
    }

    private void setupDropdowns() {
        String[] allowedInOptions = new String[]{"Guild Only", "DM Only", "Both Guild and DM"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                R.layout.dropdown_menu_item,
                allowedInOptions
        );
        allowedInDropdown.setAdapter(adapter);
        allowedInDropdown.setText("Both Guild and DM", false);
    }

    private void setupButtonListeners(View view) {
        addCommandBtn.setOnClickListener(v -> showCommandForm());
        
        view.findViewById(R.id.addAliasButton).setOnClickListener(v -> addAlias());
        view.findViewById(R.id.addUserButton).setOnClickListener(v -> addAllowedUser());
        view.findViewById(R.id.addChannelButton).setOnClickListener(v -> addAllowedChannel());
        view.findViewById(R.id.addGuildButton).setOnClickListener(v -> addAllowedGuild());
        
        view.findViewById(R.id.saveButton).setOnClickListener(v -> createCommand());
        view.findViewById(R.id.cancelButton).setOnClickListener(v -> cancelCommand());
        
        view.findViewById(R.id.responseHelpButton).setOnClickListener(v -> showResponseHelp());
    }

    private void showCommandForm() {
        showCommandForm(null);
    }

    private void showCommandForm(String commandName) {
        // Clear previous inputs
        nameField.setText("");
        descriptionField.setText("");
        responseField.setText("");
        aliasInput.setText("");
        allowedUserInput.setText("");
        allowedChannelInput.setText("");
        allowedGuildInput.setText("");
        
        // Clear containers
        aliasesContainer.removeAllViews();
        allowedUsersContainer.removeAllViews();
        allowedChannelsContainer.removeAllViews();
        allowedGuildsContainer.removeAllViews();
        
        // Reset switches and dropdown
        withPrefixSwitch.setChecked(true);
        allowedGlobalUsersSwitch.setChecked(true);
        replySwitch.setChecked(true);
        deleteCommandTriggerSwitch.setChecked(false);
        allowedInDropdown.setText("Both Guild and DM", false);
        
        // Set mode
        if (commandName != null) {
            // Edit mode
            currentEditingCommand = commandName;
            commandConfigTitle.setText("Edit " + commandName);
            nameField.setEnabled(false);
            populateFormWithCommandData(commandName);
        } else {
            // Add mode
            currentEditingCommand = null;
            commandConfigTitle.setText("Add Command");
            nameField.setEnabled(true);
        }
        
        // Show the form
        formOverlay.setVisibility(View.VISIBLE);
    }

    private void populateFormWithCommandData(String commandName) {
        try {
            JSONObject allCommands = loadExistingCommands();
            JSONObject command = allCommands.getJSONObject(commandName.toLowerCase());
            
            // Basic fields
            nameField.setText(command.optString("name", commandName));
            descriptionField.setText(command.optString("description", ""));
            responseField.setText(command.optString("response", ""));
            
            // Switches
            withPrefixSwitch.setChecked(command.optBoolean("with_prefix", true));
            allowedGlobalUsersSwitch.setChecked(command.optBoolean("allowed_global_users", true));
            replySwitch.setChecked(command.optBoolean("reply", true));
            deleteCommandTriggerSwitch.setChecked(command.optBoolean("delete_command_trigger", false));
            
            // Arrays
            populateContainerFromArray(command.optJSONArray("aliases"), aliasesContainer);
            populateContainerFromArray(command.optJSONArray("allowed_users"), allowedUsersContainer);
            populateContainerFromArray(command.optJSONArray("allowed_channels"), allowedChannelsContainer);
            populateContainerFromArray(command.optJSONArray("allowed_guilds"), allowedGuildsContainer);
            
            // Allowed in dropdown
            JSONArray allowedIn = command.optJSONArray("allowed_in");
            if (allowedIn != null) {
                if (allowedIn.length() == 1) {
                    if (allowedIn.getString(0).equals("guild")) {
                        allowedInDropdown.setText("Guild Only", false);
                    } else {
                        allowedInDropdown.setText("DM Only", false);
                    }
                } else {
                    allowedInDropdown.setText("Both Guild and DM", false);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error loading command data");
        }
    }

    private void populateContainerFromArray(JSONArray array, LinearLayout container) {
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                String item = array.optString(i);
                if (item != null && !item.isEmpty()) {
                    addItemToContainer(item, container);
                }
            }
        }
    }

    public void createCommand() {
        String name = nameField.getText().toString().trim();
        String description = descriptionField.getText().toString().trim();
        String response = responseField.getText().toString().trim();
        
        if (name.isEmpty()) {
            showError("Command name is required");
            return;
        }
        
        if (response.isEmpty()) {
            showError("Command response is required");
            return;
        }
        
        try {
            JSONObject allCommands = loadExistingCommands();
            JSONObject newCommand = buildCommandObject(name, description, response);
            
            if (currentEditingCommand != null && !currentEditingCommand.equalsIgnoreCase(name)) {
                allCommands.remove(currentEditingCommand.toLowerCase());
            }
            
            allCommands.put(name.toLowerCase(), newCommand);
            saveCommandsToFile(allCommands);
            
            formOverlay.setVisibility(View.GONE);
            loadCommands();
            showSuccess(currentEditingCommand != null ? 
                "Command updated successfully" : "Command created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error saving command");
        }
    }

    private JSONObject loadExistingCommands() throws Exception {
        JSONObject allCommands = new JSONObject();
        if (Arrays.asList(requireContext().fileList()).contains(CMD)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(requireContext().openFileInput(CMD)));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();
            allCommands = new JSONObject(jsonBuilder.toString());
        }
        return allCommands;
    }

    private JSONObject buildCommandObject(String name, String description, String response) throws Exception {
        JSONObject newCommand = new JSONObject();
        newCommand.put("name", name);
        newCommand.put("description", description);
        newCommand.put("response", response);
        
        newCommand.put("aliases", getItemsFromContainer(aliasesContainer));
        newCommand.put("allowed_users", getItemsFromContainer(allowedUsersContainer));
        newCommand.put("allowed_channels", getItemsFromContainer(allowedChannelsContainer));
        newCommand.put("allowed_guilds", getItemsFromContainer(allowedGuildsContainer));
        
        newCommand.put("with_prefix", withPrefixSwitch.isChecked());
        newCommand.put("allowed_global_users", allowedGlobalUsersSwitch.isChecked());
        newCommand.put("reply", replySwitch.isChecked());
        newCommand.put("delete_command_trigger", deleteCommandTriggerSwitch.isChecked());
        
        String allowedIn = allowedInDropdown.getText().toString();
        JSONArray allowedInArray = new JSONArray();
        if (allowedIn.equals("Guild Only")) {
            allowedInArray.put("guild");
        } else if (allowedIn.equals("DM Only")) {
            allowedInArray.put("dm");
        } else {
            allowedInArray.put("guild");
            allowedInArray.put("dm");
        }
        newCommand.put("allowed_in", allowedInArray);
        
        return newCommand;
    }

    private JSONArray getItemsFromContainer(LinearLayout container) {
        JSONArray items = new JSONArray();
        for (int i = 0; i < container.getChildCount(); i++) {
            View child = container.getChildAt(i);
            if (child instanceof LinearLayout) {
                TextView textView = (TextView) ((LinearLayout) child).getChildAt(0);
                items.put(textView.getText().toString());
            }
        }
        return items;
    }

    private void saveCommandsToFile(JSONObject commands) throws Exception {
        FileOutputStream fos = requireContext().openFileOutput(CMD, Context.MODE_PRIVATE);
        fos.write(commands.toString().getBytes());
        fos.close();
    }

    public void cancelCommand() {
        formOverlay.setVisibility(View.GONE);
    }

    private void showResponseHelp() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        WebView webView = new WebView(requireContext());
        webView.getSettings().setJavaScriptEnabled(true); // optional if needed
    
        webView.loadUrl("file:///android_asset/variables.html");
    
        builder.setView(webView);
        builder.setPositiveButton("OK", null);
        builder.show();
    }

    private void addAlias() {
        String alias = aliasInput.getText().toString().trim();
        if (!alias.isEmpty()) {
            addItemToContainer(alias, aliasesContainer);
            aliasInput.setText("");
        }
    }
    
    private void addAllowedUser() {
        String userId = allowedUserInput.getText().toString().trim();
        if (!userId.isEmpty()) {
            addItemToContainer(userId, allowedUsersContainer);
            allowedUserInput.setText("");
        }
    }
    
    private void addAllowedChannel() {
        String channelId = allowedChannelInput.getText().toString().trim();
        if (!channelId.isEmpty()) {
            addItemToContainer(channelId, allowedChannelsContainer);
            allowedChannelInput.setText("");
        }
    }
    
    private void addAllowedGuild() {
        String guildId = allowedGuildInput.getText().toString().trim();
        if (!guildId.isEmpty()) {
            addItemToContainer(guildId, allowedGuildsContainer);
            allowedGuildInput.setText("");
        }
    }
    
    private void addItemToContainer(String text, LinearLayout container) {
        Context context = getContext();
        if (context == null) return;
        
        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setPadding(16, 8, 16, 8);
        itemLayout.setBackgroundResource(R.drawable.rounded_tag_bg);
        
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(getResources().getColor(android.R.color.white));
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        
        ImageView deleteIcon = new ImageView(context);
        deleteIcon.setImageResource(R.drawable.ic_close);
        deleteIcon.setColorFilter(getResources().getColor(android.R.color.white));
        deleteIcon.setOnClickListener(v -> container.removeView(itemLayout));
        
        itemLayout.addView(textView);
        itemLayout.addView(deleteIcon);
        
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 8);
        container.addView(itemLayout, params);
    }

    private void loadCommands() {
        loadCommands("");
    }

    private void loadCommands(String filter) {
        commandsListContainer.removeAllViews();

        try {
            Context context = requireContext();
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(CMD)));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();

            JSONObject allCommands = new JSONObject(jsonBuilder.toString());
            Iterator<String> keys = allCommands.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject cmdObj = allCommands.getJSONObject(key);
                String name = cmdObj.optString("name", key);
                String desc = cmdObj.optString("description", "");
                String combined = (name + " " + desc).toLowerCase();
                if (combined.contains(filter.toLowerCase())) {
                    addCommandView(name, desc, key);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addCommandView(String name, String description, String commandKey) {
        Context context = requireContext();

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(30, 30, 30, 30);
        layout.setBackgroundResource(R.drawable.rounded_dropdown_bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 32);
        layout.setLayoutParams(params);

        TextView title = new TextView(context);
        title.setText(name);
        title.setTextSize(18f);
        title.setTypeface(null, Typeface.BOLD);
        title.setTextColor(android.graphics.Color.WHITE);
        title.setPadding(0, 0, 0, 10);
        layout.addView(title);

        TextView descView = new TextView(context);
        descView.setText(description);
        descView.setTextColor(android.graphics.Color.WHITE);
        descView.setVisibility(View.GONE);
        descView.setAlpha(0f);
        layout.addView(descView);

        LinearLayout buttonsLayout = new LinearLayout(context);
        buttonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonsLayout.setVisibility(View.GONE);
        buttonsLayout.setAlpha(0f);

        // Edit Button
        LinearLayout editBtn = new LinearLayout(context);
        editBtn.setOrientation(LinearLayout.HORIZONTAL);
        editBtn.setBackgroundResource(R.drawable.btn_rounded_indigo);
        editBtn.setPadding(20, 12, 20, 12);
        editBtn.setGravity(android.view.Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams editParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        editParams.setMargins(0, 0, 16, 0);
        editBtn.setLayoutParams(editParams);

        ImageView editIcon = new ImageView(context);
        editIcon.setImageResource(R.drawable.ic_pen);
        editIcon.setColorFilter(android.graphics.Color.WHITE);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(40, 40);
        editIcon.setLayoutParams(iconParams);
        editIcon.setPadding(0, 0, 12, 0);
        editBtn.addView(editIcon);

        TextView editText = new TextView(context);
        editText.setText("Edit");
        editText.setTextColor(android.graphics.Color.WHITE);
        editText.setTextSize(14f);
        editBtn.addView(editText);

        editBtn.setOnClickListener(v -> {
            try {
                showCommandForm(commandKey);
            } catch (Exception e) {
                e.printStackTrace();
                showError("Error loading command");
            }
        });

        // Delete Button
        LinearLayout deleteBtn = new LinearLayout(context);
        deleteBtn.setOrientation(LinearLayout.HORIZONTAL);
        deleteBtn.setBackgroundResource(R.drawable.btn_rounded_red);
        deleteBtn.setPadding(20, 12, 20, 12);
        deleteBtn.setGravity(android.view.Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams deleteParams = new LinearLayout.LayoutParams(
                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        deleteBtn.setLayoutParams(deleteParams);

        ImageView deleteIcon = new ImageView(context);
        deleteIcon.setImageResource(R.drawable.ic_delete);
        deleteIcon.setColorFilter(android.graphics.Color.WHITE);
        deleteIcon.setLayoutParams(new LinearLayout.LayoutParams(40, 40));
        deleteIcon.setPadding(0, 0, 12, 0);
        deleteBtn.addView(deleteIcon);

        TextView deleteText = new TextView(context);
        deleteText.setText("Delete");
        deleteText.setTextColor(android.graphics.Color.WHITE);
        deleteText.setTextSize(14f);
        deleteBtn.addView(deleteText);

        deleteBtn.setOnClickListener(v -> showDeleteConfirmationDialog(commandKey));

        buttonsLayout.addView(editBtn);
        buttonsLayout.addView(deleteBtn);
        layout.addView(buttonsLayout);

        title.setOnClickListener(v -> toggleCommandDetails(descView, buttonsLayout));
        commandsListContainer.addView(layout);
    }

    private void showDeleteConfirmationDialog(String commandKey) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_confirm, null);
        TextView dlgTitle = dialogView.findViewById(R.id.deleteTitle);
        TextView message = dialogView.findViewById(R.id.deleteMessage);
        Button btnDelete = dialogView.findViewById(R.id.btnDelete);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        try {
            JSONObject allCommands = loadExistingCommands();
            JSONObject cmdObj = allCommands.getJSONObject(commandKey);
            String name = cmdObj.optString("name", commandKey);
            dlgTitle.setText("Delete \"" + name + "\"?");
        } catch (Exception e) {
            e.printStackTrace();
            dlgTitle.setText("Delete Command?");
        }
        
        message.setText("This command will be permanently removed.");

        final AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();

        Animation enterAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.popup_enter);
        dialogView.startAnimation(enterAnim);

        btnCancel.setOnClickListener(cancelView -> {
            Animation exitAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.popup_exit);
            dialogView.startAnimation(exitAnim);
            dialogView.postDelayed(dialog::dismiss, 180);
        });

        btnDelete.setOnClickListener(deleteView -> {
            Animation exitAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.popup_exit);
            dialogView.startAnimation(exitAnim);
            dialogView.postDelayed(() -> {
                deleteCommandFromJson(commandKey);
                dialog.dismiss();
                loadCommands();
            }, 180);
        });
    }

    private void toggleCommandDetails(TextView descView, LinearLayout buttonsLayout) {
        if (descView.getVisibility() == View.GONE) {
            descView.setVisibility(View.VISIBLE);
            buttonsLayout.setVisibility(View.VISIBLE);
            descView.animate().alpha(1f).setDuration(300).start();
            buttonsLayout.animate().alpha(1f).setDuration(300).start();
        } else {
            descView.animate().alpha(0f).setDuration(300).withEndAction(() -> descView.setVisibility(View.GONE)).start();
            buttonsLayout.animate().alpha(0f).setDuration(300).withEndAction(() -> buttonsLayout.setVisibility(View.GONE)).start();
        }
    }

    private void deleteCommandFromJson(String commandKey) {
        try {
            Context context = requireContext();
            BufferedReader reader = new BufferedReader(new InputStreamReader(context.openFileInput(CMD)));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
            reader.close();

            JSONObject allCommands = new JSONObject(jsonBuilder.toString());
            allCommands.remove(commandKey);

            FileOutputStream fos = context.openFileOutput(CMD, Context.MODE_PRIVATE);
            fos.write(allCommands.toString().getBytes());
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error deleting command", Toast.LENGTH_SHORT).show();
        }
    }


    private void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void showSuccess(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cmdsearchBar != null) {
            cmdsearchBar.removeTextChangedListener(searchWatcher);
        }
    }
}