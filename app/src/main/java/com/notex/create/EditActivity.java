package com.notex.create;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class EditActivity extends AppCompatActivity {
    
    private EditText titleEdit;
    private EditText contentEdit;
    private Spinner notebookSpinner;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private ArrayList<HashMap<String, Object>> notebooks;
    private ArrayList<HashMap<String, Object>> notes;
    private String editMode = "new";
    private int editPosition = -1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        
        // Initialize views
        titleEdit = findViewById(R.id.title_edit);
        contentEdit = findViewById(R.id.content_edit);
        notebookSpinner = findViewById(R.id.notebook_spinner);
        
        // Initialize SharedPreferences and Gson
        sharedPreferences = getSharedPreferences("NoteXData", Context.MODE_PRIVATE);
        gson = new Gson();
        
        // Load data
        loadData();
        
        // Set up spinner
        setupSpinner();
        
        // Check if editing existing note
        if (getIntent().hasExtra("title")) {
            editMode = "edit";
            String title = getIntent().getStringExtra("title");
            
            // Find the note
            for (int i = 0; i < notes.size(); i++) {
                HashMap<String, Object> note = notes.get(i);
                if (note.get("title").toString().equals(title)) {
                    editPosition = i;
                    titleEdit.setText(note.get("title").toString());
                    contentEdit.setText(note.get("content").toString());
                    
                    // Set spinner selection
                    String notebook = note.get("notebook").toString();
                    for (int j = 0; j < notebooks.size(); j++) {
                        if (notebooks.get(j).get("name").toString().equals(notebook)) {
                            notebookSpinner.setSelection(j + 1); // +1 because of "None" option
                            break;
                        }
                    }
                    
                    break;
                }
            }
        }
        
        // Set up back button
        findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        // Set up save button
        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }
    
    private void loadData() {
        // Load notebooks
        String notebooksJson = sharedPreferences.getString("notebooks", "");
        if (!notebooksJson.isEmpty()) {
            Type type = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
            notebooks = gson.fromJson(notebooksJson, type);
        } else {
            notebooks = new ArrayList<>();
        }
        
        // Load notes
        String notesJson = sharedPreferences.getString("notes", "");
        if (!notesJson.isEmpty()) {
            Type type = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
            notes = gson.fromJson(notesJson, type);
        } else {
            notes = new ArrayList<>();
        }
    }
    
    private void setupSpinner() {
        ArrayList<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("None");
        
        for (HashMap<String, Object> notebook : notebooks) {
            spinnerItems.add(notebook.get("name").toString());
        }
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        notebookSpinner.setAdapter(adapter);
    }
    
    private void saveNote() {
        String title = titleEdit.getText().toString().trim();
        String content = contentEdit.getText().toString().trim();
        
        if (title.isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }
        
        String notebook = "";
        int spinnerPosition = notebookSpinner.getSelectedItemPosition();
        if (spinnerPosition > 0) { // 0 is "None"
            notebook = notebooks.get(spinnerPosition - 1).get("name").toString();
        }
        
        HashMap<String, Object> note = new HashMap<>();
        note.put("title", title);
        note.put("content", content);
        note.put("notebook", notebook);
        
        if (editMode.equals("edit") && editPosition >= 0) {
            notes.set(editPosition, note);
        } else {
            notes.add(note);
        }
        
        // Update notebook counts
        updateNotebookCounts();
        
        // Save notes
        String json = gson.toJson(notes);
        sharedPreferences.edit().putString("notes", json).apply();
        
        // Save notebooks
        json = gson.toJson(notebooks);
        sharedPreferences.edit().putString("notebooks", json).apply();
        
        Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        finish();
    }
    
    private void updateNotebookCounts() {
        // Reset all counts
        for (HashMap<String, Object> notebook : notebooks) {
            notebook.put("count", 0);
        }
        
        // Count notes in each notebook
        for (HashMap<String, Object> note : notes) {
            String notebookName = note.get("notebook").toString();
            if (!notebookName.isEmpty()) {
                for (HashMap<String, Object> notebook : notebooks) {
                    if (notebook.get("name").toString().equals(notebookName)) {
                        int count = (int) notebook.get("count");
                        notebook.put("count", count + 1);
                        break;
                    }
                }
            }
        }
    }
}