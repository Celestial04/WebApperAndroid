package com.bouillie.web;

import static android.app.usage.UsageEvents.Event.NONE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.boullie.web.R;
import com.google.android.gms.tasks.Task;
import com.google.android.material.color.DynamicColors;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;

public class tuto_3 extends AppCompatActivity {
    private int selectedTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int savedTheme = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getInt("selected_theme", NONE);
        selectedTheme = (savedTheme != NONE) ? savedTheme : R.style.Theme_WebApper_dark;
        setTheme(selectedTheme);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        setContentView(R.layout.tuto_3);

        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE);
            }
        });

        Button button = findViewById(R.id.button4);
        button.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();
            Intent intent = new Intent(tuto_3.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
