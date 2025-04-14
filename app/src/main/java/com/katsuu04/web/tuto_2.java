package com.katsuu04.web;

import static android.app.usage.UsageEvents.Event.NONE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.android.material.color.DynamicColors;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

public class tuto_2 extends AppCompatActivity {
    private int selectedTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int savedTheme = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getInt("selected_theme", NONE);
        selectedTheme = (savedTheme != NONE) ? savedTheme : R.style.Theme_WebApper_dark;
        setTheme(selectedTheme);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        setContentView(R.layout.tuto_2);

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE);
            }
        });

        Button btn = findViewById(R.id.button_example_show);
        Button btn1 = findViewById(R.id.button4);
        btn.setOnClickListener(view -> {
            if (btn1.getVisibility() == View.VISIBLE) {
                btn1.setVisibility(View.INVISIBLE);
            } else {
                btn1.setVisibility(View.VISIBLE);
            }
        });

        btn1.setOnClickListener(v -> {
            Intent intent = new Intent(tuto_2.this, tuto_3.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LOW_PROFILE);
        }
    }
}
