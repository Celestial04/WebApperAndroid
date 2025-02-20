package com.bouillie.web;

import static android.app.usage.UsageEvents.Event.NONE;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.boullie.web.R;
import com.google.android.material.color.DynamicColors;

public class tuto_1 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int savedTheme = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getInt("selected_theme", NONE);

        int selectedTheme = (savedTheme != NONE) ? savedTheme : R.style.Theme_WebApper_dark;
        setTheme(selectedTheme);
        DynamicColors.applyToActivitiesIfAvailable(this.getApplication());
        setContentView(R.layout.tuto_1);

        Button btn = findViewById(R.id.button4);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(tuto_1.this, tuto_2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
