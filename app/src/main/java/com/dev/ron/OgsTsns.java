package com.dev.ron;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.animation.AlphaAnimation;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.annotation.NonNull;
import android.text.TextWatcher;
import android.text.Editable;

public class OgsTsns extends Fragment {

    private LinearLayout logsContainer;
    private JSONArray logArray = new JSONArray();
    private EditText logsearchBar;
    private TextWatcher searchWatcher;

    public OgsTsns() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gtekdek, container, false);

        logsContainer = view.findViewById(R.id.logs_container);
        loadLogs();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Get activity's search bar
        if (getActivity() != null) {
           logsearchBar = ((ZkUdYs) getActivity()).findViewById(R.id.search_input);
            
            // Create text watcher
           searchWatcher = new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    loadLogs(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
           };
           logsearchBar.addTextChangedListener(searchWatcher);
        }
    }

    private void loadLogs() {
        loadLogs("");
    }

    private void loadLogs(String filter) {
        logsContainer.removeAllViews();
        logArray = LyedYdhdUd.getLogs(requireContext()); // File se load!

        try {
            for (int i = 0; i < logArray.length(); i++) {
                JSONObject log = logArray.getJSONObject(i);
                String allData = log.toString().toLowerCase();
                if (allData.contains(filter.toLowerCase())) {
                    addLogView(log, i);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addLogView(JSONObject log, int index) throws JSONException {
        Context context = requireContext();

        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        GradientDrawable bg = new GradientDrawable();
        bg.setCornerRadius(24f);
        bg.setColor(Color.parseColor("#441A237E"));
        bg.setStroke(2, Color.parseColor("#8847B2FF"));
        layout.setBackground(bg);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 32);
        layout.setLayoutParams(params);

        String[] keys = {"command", "username", "server", "channel", "datetime"};
        for (String key : keys) {
            LinearLayout pairLayout = new LinearLayout(context);
            pairLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView label = new TextView(context);
            label.setText(key.toUpperCase() + ": ");
            label.setTextColor(Color.parseColor("#66FF66"));
            label.setTextSize(17f);
            label.setTypeface(Typeface.MONOSPACE);

            TextView value = new TextView(context);
            value.setText(log.optString(key));
            value.setTextColor(Color.parseColor("#FF6666"));
            value.setTextSize(17f);
            value.setTypeface(Typeface.MONOSPACE);

            pairLayout.addView(label);
            pairLayout.addView(value);
            layout.addView(pairLayout);
        }

        ImageButton deleteBtn = new ImageButton(context);
        deleteBtn.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
        deleteBtn.setBackgroundColor(Color.TRANSPARENT);
        deleteBtn.setScaleX(0.8f);
        deleteBtn.setScaleY(0.8f);
        deleteBtn.setColorFilter(Color.parseColor("#FF8888"));

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParams.gravity = Gravity.END;
        deleteBtn.setLayoutParams(btnParams);
        layout.addView(deleteBtn);

        deleteBtn.setOnClickListener(v -> {
            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(300);
            fadeOut.setFillAfter(true);
            layout.startAnimation(fadeOut);
            layout.postDelayed(() -> {
                logsContainer.removeView(layout);
                deleteLog(index);
            }, 300);
        });

        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(300);
        layout.startAnimation(fadeIn);

        logsContainer.addView(layout);
    }

    private void deleteLog(int index) {
        LyedYdhdUd.deleteLog(requireContext(), index); // File se delete!
        loadLogs(); // Refresh screen
    }
    @Override
    public void onPause() {
        super.onPause();
        // Unregister watcher when fragment is not visible
        if (logsearchBar != null) {
            logsearchBar.removeTextChangedListener(searchWatcher);
        }
    }
}