package com.dev.ron;

import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import androidx.fragment.app.Fragment;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.EditText;
import android.graphics.Rect;
import android.widget.Toast;
import android.content.Intent;

public class ZkUdYs extends AppCompatActivity {

    private View indicator;
    private LinearLayout navItemsContainer;
    private int[] navItems = {R.id.nav_log, R.id.nav_commands, R.id.nav_dashboard, R.id.nav_config, R.id.nav_custom};
    private int[] iconIds = {R.id.icon_log, R.id.icon_commands, R.id.icon_dashboard, R.id.icon_config, R.id.icon_custom};
    private int[] textIds = {R.id.text_log, R.id.text_commands, R.id.text_dashboard, R.id.text_config, R.id.text_custom};
    private EditText searchInput;
    private TextView topTitle;
    private ImageView searchBtn, settingsBtn;
    private boolean isSearchActive = false;
    private boolean isKeyboardVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bsksjhdao);
        setContentView(R.layout.leysvjdhkdh);
        
        // Initialize all views
        indicator = findViewById(R.id.indicator);
        navItemsContainer = findViewById(R.id.nav_items_container);
        settingsBtn = findViewById(R.id.top_settings);
        topTitle = findViewById(R.id.top_title);
        searchBtn = findViewById(R.id.top_search);
        searchInput = findViewById(R.id.search_input);
        
        // Initially hide search input
        searchInput.setVisibility(View.GONE);
        
        // Search icon click
        searchBtn.setOnClickListener(v -> openSearch());
        
        // Setup keyboard visibility listener
        setupKeyboardVisibilityListener();
        
        // Wait for layout ready before loading default fragment
        navItemsContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                navItemsContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                updateNavItem(2, false); // Default: Dashboard active
                settingsBtn.setColorFilter(getResources().getColor(R.color.nav_item_unselected));
                loadFragment(new IduJdh()); // Load Dashboard
            }
        });
        
                // Setup navigation buttons
                for (int i = 0; i < navItems.length; i++) {
                    final int position = i;
                    findViewById(navItems[i]).setOnClickListener(v -> {
                        closeSearch();
                        updateNavItem(position, true);
                        settingsBtn.setColorFilter(getResources().getColor(R.color.nav_item_unselected));
                    
                        // Animate indicator comeback only if it's GONE
                        if (indicator.getVisibility() == View.GONE) {
                            indicator.setAlpha(0f);
                            indicator.setTranslationY(20f);
                            indicator.setVisibility(View.VISIBLE);
                            indicator.animate()
                                    .alpha(1f)
                                    .translationY(0f)
                                    .setDuration(300)
                                    .start();
                        }
                    
                        switch (position) {
                            case 0:
                                showSearchButton();
                                loadFragment(new OgsTsns()); // Log
                                searchBtn.setVisibility(View.VISIBLE);
                                topTitle.setText("Commands Log");
                                break;
                            case 1:
                                showSearchButton();
                                loadFragment(new LtDjFkst()); // Commands
                                searchBtn.setVisibility(View.VISIBLE);
                                topTitle.setText("Commands");
                                break;
                            case 2:
                                hideSearchButton();
                                loadFragment(new IduJdh()); // Dashboard
                                topTitle.setText("ROCK");
                                break;
                            case 3:
                                hideSearchButton();
                                loadFragment(new ZjdJehdk()); // Config
                                topTitle.setText("Configuration");
                                break;
                            case 4:
                                showSearchButton();
                                loadFragment(new CcJdhdjd()); // Custom Command
                                topTitle.setText("Custom Commands");
                                searchBtn.setVisibility(View.VISIBLE);
                                break;
                        }
                    });
                }
                
                settingsBtn.setOnClickListener(v -> {
                    loadFragment(new IrhTnQd());
                    topTitle.setText("Settings");
                    resetNavItems();
                    settingsBtn.setColorFilter(getResources().getColor(R.color.purple_500));
                    // Animate indicator hide
                    indicator.animate()
                        .alpha(0f)
                        .translationY(20f)
                        .setDuration(300)
                        .withEndAction(() -> indicator.setVisibility(View.GONE))
                        .start();
                });
    }

    private void setupKeyboardVisibilityListener() {
        final View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootView.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;
            float dp = keypadHeight / getResources().getDisplayMetrics().density;
            isKeyboardVisible = dp > 100; // Threshold for keyboard visibility
        });
    }

    private void loadFragment(Fragment fragment) {
        try {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateNavItem(int position, boolean animate) {
        // Reset all items
        for (int i = 0; i < navItems.length; i++) {
            ImageView icon = findViewById(iconIds[i]);
            TextView text = findViewById(textIds[i]);
            
            if (icon != null) {
                icon.setColorFilter(getResources().getColor(R.color.nav_item_unselected));
                icon.setScaleX(1f);
                icon.setScaleY(1f);
            }
            
            if (text != null) {
                text.setVisibility(View.GONE);
            }
        }

        // Highlight selected item
        ImageView selectedIcon = findViewById(iconIds[position]);
        TextView selectedText = findViewById(textIds[position]);
        
        if (selectedIcon != null) {
            selectedIcon.setColorFilter(getResources().getColor(R.color.purple_500));
        }
        
        if (selectedText != null) {
            selectedText.setVisibility(View.VISIBLE);
        }

        if (animate && selectedIcon != null) {
            // Bounce animation
            TranslateAnimation bounce = new TranslateAnimation(0, 0, 0, -10);
            bounce.setDuration(300);
            bounce.setInterpolator(new BounceInterpolator());
            bounce.setRepeatCount(0);
            selectedIcon.startAnimation(bounce);
        }

        // Move indicator to center of selected item
        View navItem = findViewById(navItems[position]);
        if (navItem != null && indicator != null) {
            float centerX = navItem.getLeft() + navItem.getWidth() / 2f - indicator.getWidth() / 2f;
            indicator.animate()
                    .x(centerX)
                    .setDuration(300)
                    .start();
        }
    }

    private void resetNavItems() {
        for (int i = 0; i < navItems.length; i++) {
            ImageView icon = findViewById(iconIds[i]);
            TextView text = findViewById(textIds[i]);
            
            if (icon != null) {
                icon.setColorFilter(getResources().getColor(R.color.nav_item_unselected));
                icon.setScaleX(1f);
                icon.setScaleY(1f);
            }
            
            if (text != null) {
                text.setVisibility(View.GONE);
            }
        }
    }

    private void openSearch() {
        if (isSearchActive || searchInput == null) return;
        
        try {
            isSearchActive = true;
            
            // Cancel any ongoing animations
            topTitle.animate().cancel();
            searchInput.animate().cancel();

            topTitle.animate()
                .alpha(0f)
                .setDuration(200)
                .withEndAction(() -> topTitle.setVisibility(View.GONE))
                .start();

            settingsBtn.setVisibility(View.GONE);
            searchBtn.setVisibility(View.GONE);
            
            searchInput.setVisibility(View.VISIBLE);
            searchInput.setAlpha(0f);
            searchInput.setTranslationY(-20f);
            searchInput.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(300)
                .start();

            searchInput.requestFocus();
            showKeyboard();
        } catch (Exception e) {
            isSearchActive = false;
            e.printStackTrace();
        }
    }

    private void closeSearch() {
        if (!isSearchActive || searchInput == null) return;
        
        try {
            isSearchActive = false;
            hideKeyboard();
            searchInput.setText("");
            
            searchInput.animate().cancel();
            searchInput.animate()
                .alpha(0f)
                .translationY(-20f)
                .setDuration(200)
                .withEndAction(() -> {
                    searchInput.setVisibility(View.GONE);
                    searchInput.clearFocus();
                })
                .start();

            topTitle.setVisibility(View.VISIBLE);
            settingsBtn.setVisibility(View.VISIBLE);
            searchBtn.setVisibility(View.VISIBLE);

            topTitle.setAlpha(0f);
            topTitle.animate()
                .alpha(1f)
                .setDuration(300)
                .start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null && searchInput != null) {
                imm.showSoftInput(searchInput, InputMethodManager.SHOW_IMPLICIT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null && searchInput != null && searchInput.getWindowToken() != null) {
                imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        if (isSearchActive) {
            if (isKeyboardVisible) {
                hideKeyboard();
            } else {
                closeSearch();
            }
        } else {
            super.onBackPressed();
        }
    }
    private void showSearchButton() {
        if (searchBtn != null) {
            searchBtn.setVisibility(View.VISIBLE);
        }
    }

    private void hideSearchButton() {
        if (searchBtn != null) {
            searchBtn.setVisibility(View.GONE);
            // Also close search if it was open
            closeSearch();
        }
    }
    @Override
    protected void onDestroy() {
        // Clean up any ongoing animations
        if (searchInput != null) {
            searchInput.animate().cancel();
        }
        if (topTitle != null) {
            topTitle.animate().cancel();
        }
        super.onDestroy();
    }
}
