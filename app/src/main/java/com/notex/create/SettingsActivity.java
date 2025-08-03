package com.notex.create;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import android.content.Intent;

public class SettingsActivity extends AppCompatActivity {
    
    private SharedPreferences sharedPreferences;
    private Gson gson;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        
        sharedPreferences = getSharedPreferences("NoteXData", Context.MODE_PRIVATE);
        gson = new Gson();
        
        // Set up back button
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // Set up settings click listeners
        findViewById(R.id.export_all_notes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exportAllNotes();
            }
        });
        
        findViewById(R.id.clear_all_data).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showClearDataDialog();
            }
        });
        
        findViewById(R.id.debug_console).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, DebugActivity.class);
                startActivity(intent);
            }
        });
    }
    
    private void exportAllNotes() {
        try {
            // Load all notes
            String notesJson = sharedPreferences.getString("notes", "");
            if (notesJson.isEmpty()) {
                showSnackbar("No notes to export");
                return;
            }
            
            Type type = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
            ArrayList<HashMap<String, Object>> notes = gson.fromJson(notesJson, type);
            
            if (notes.isEmpty()) {
                showSnackbar("No notes to export");
                return;
            }
            
            // Create export directory
            File exportDir = new File(getExternalFilesDir(null), "exports");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            
            // Export all notes to a single file
            File allNotesFile = new File(exportDir, "all_notes_export.txt");
            FileWriter writer = new FileWriter(allNotesFile);
            
            writer.write("NoteX - All Notes Export\n");
            writer.write("Exported on: " + new java.util.Date().toString() + "\n");
            writer.write("Total notes: " + notes.size() + "\n\n");
            writer.write("=" + "=".repeat(50) + "\n\n");
            
            for (HashMap<String, Object> note : notes) {
                writer.write("Title: " + note.get("title").toString() + "\n");
                String notebook = note.get("notebook").toString();
                if (!notebook.isEmpty()) {
                    writer.write("Notebook: " + notebook + "\n");
                }
                writer.write("\n" + note.get("content").toString() + "\n\n");
                writer.write("-" + "-".repeat(30) + "\n\n");
            }
            
            writer.close();
            
            // Share the exported file
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "NoteX - All Notes Export");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "All notes exported to: " + allNotesFile.getAbsolutePath());
            startActivity(Intent.createChooser(shareIntent, "Export All Notes"));
            
            showSnackbar("All notes exported successfully");
        } catch (IOException e) {
            showSnackbar("Failed to export notes");
        }
    }
    
    private void showClearDataDialog() {
        new MaterialAlertDialogBuilder(this)
            .setTitle("Clear All Data")
            .setMessage("This will permanently delete all your notes and notebooks. This action cannot be undone.")
            .setIcon(R.drawable.icon_delete_round)
            .setPositiveButton("Clear All", (dialog, which) -> {
                clearAllData();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
    
    private void clearAllData() {
        // Clear all data from SharedPreferences
        sharedPreferences.edit()
            .remove("notes")
            .remove("notebooks")
            .apply();
            
        showSnackbar("All data cleared successfully");
        
        // Go back to main activity to refresh the UI
        finish();
    }
    
    private void showSnackbar(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.md_theme_light_surfaceContainer))
            .setTextColor(getColor(R.color.md_theme_light_onSurface))
            .show();
    }
}