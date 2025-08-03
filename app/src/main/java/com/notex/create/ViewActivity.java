package com.notex.create;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

public class ViewActivity extends AppCompatActivity {
	
	private TextView titleText;
	private TextView notebookText;
	private TextView contentText;
	private TextView viewTitleText;
	private SharedPreferences sharedPreferences;
	private Gson gson;
	private ArrayList<HashMap<String, Object>> notes;
	private String viewType;
	private String viewName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view);
		
		// Initialize views
		titleText = findViewById(R.id.note_title);
		notebookText = findViewById(R.id.notebook_name);
		contentText = findViewById(R.id.note_content);
		viewTitleText = findViewById(R.id.view_title);
		
		// Initialize SharedPreferences and Gson
		sharedPreferences = getSharedPreferences("NoteXData", Context.MODE_PRIVATE);
		gson = new Gson();
		
		// Get view type and name
		viewType = getIntent().getStringExtra("type");
		viewName = getIntent().getStringExtra(viewType.equals("note") ? "title" : "name");
		
		// Load data
		loadData();
		
		// Set up back button
		findViewById(R.id.back_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		// Set up edit button
		findViewById(R.id.edit_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (viewType.equals("note")) {
					Intent intent = new Intent(ViewActivity.this, EditActivity.class);
					intent.putExtra("title", viewName);
					startActivity(intent);
					finish();
				}
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadData(); // Refresh data when returning to this activity
	}
	
	private void loadData() {
		// Load notes
		String notesJson = sharedPreferences.getString("notes", "");
		if (!notesJson.isEmpty()) {
			Type type = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
			notes = gson.fromJson(notesJson, type);
		} else {
			notes = new ArrayList<>();
		}
		
		if (viewType.equals("note")) {
			// Display single note
			for (HashMap<String, Object> note : notes) {
				if (note.get("title").toString().equals(viewName)) {
					titleText.setText(note.get("title").toString());
					contentText.setText(note.get("content").toString());
					
					String notebook = note.get("notebook").toString();
					if (notebook.isEmpty()) {
						notebookText.setVisibility(View.GONE);
					} else {
						notebookText.setText("Notebook: " + notebook);
						notebookText.setVisibility(View.VISIBLE);
					}
					
					viewTitleText.setText("Note");
					break;
				}
			}
		} else if (viewType.equals("notebook")) {
			// Display notes in notebook
			viewTitleText.setText(viewName);
			titleText.setVisibility(View.GONE);
			notebookText.setVisibility(View.GONE);
			
			StringBuilder content = new StringBuilder();
			for (HashMap<String, Object> note : notes) {
				String notebook = note.get("notebook").toString();
				if (notebook.equals(viewName)) {
					content.append("# ").append(note.get("title").toString()).append("\n\n");
					content.append(note.get("content").toString()).append("\n\n");
					content.append("---\n\n");
				}
			}
			
			contentText.setText(content.toString());
		}
	}
}
