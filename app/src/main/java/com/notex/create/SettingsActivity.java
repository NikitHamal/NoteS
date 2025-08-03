package com.notex.create;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        // Set up back button
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // Set up settings click listeners
        findViewById(R.id.profile_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Profile settings", Toast.LENGTH_SHORT).show();
            }
        });
        
        findViewById(R.id.sync_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Sync settings", Toast.LENGTH_SHORT).show();
            }
        });
        
        findViewById(R.id.theme_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Theme settings", Toast.LENGTH_SHORT).show();
            }
        });
        
        findViewById(R.id.font_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Font settings", Toast.LENGTH_SHORT).show();
            }
        });
        
        findViewById(R.id.about_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "About NoteX", Toast.LENGTH_SHORT).show();
            }
        });
        
        findViewById(R.id.help_setting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SettingsActivity.this, "Help & Support", Toast.LENGTH_SHORT).show();
            }
        });
    }
}