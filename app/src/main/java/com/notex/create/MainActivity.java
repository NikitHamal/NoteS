package com.notex.create;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import android.content.Intent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import android.view.MenuItem;
import android.widget.PopupMenu;
import androidx.appcompat.app.AlertDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import androidx.cardview.widget.CardView;

public class MainActivity extends AppCompatActivity {
	
	private SharedPreferences sharedPreferences;
	private LinearLayout notebooksContainer;
	private LinearLayout notesContainer;
	private LinearLayout emptyNotebooksView;
	private LinearLayout emptyNotesView;
	private ArrayList<HashMap<String, Object>> notebooks;
	private ArrayList<HashMap<String, Object>> notes;
	private Gson gson;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		// Initialize views
		notebooksContainer = findViewById(R.id.notebooks_container);
		notesContainer = findViewById(R.id.notes_container);
		emptyNotebooksView = findViewById(R.id.empty_notebooks_view);
		emptyNotesView = findViewById(R.id.empty_notes_view);
		
		// Initialize SharedPreferences and Gson
		sharedPreferences = getSharedPreferences("NoteXData", Context.MODE_PRIVATE);
		gson = new Gson();
		
		// Set up FAB
		FloatingActionButton fab = findViewById(R.id.fab_add);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, EditActivity.class);
				startActivity(intent);
			}
		});
		
		// Set up menu button
		findViewById(R.id.menu_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
				startActivity(intent);
			}
		});
		
		// Set up search
		setupSearch();
		
		// Search background is now handled by XML drawable
		
		// Load data
		loadData();
	}
	
	private void setupSearch() {
		EditText searchEdit = findViewById(R.id.search_edit);
		searchEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				filterNotesAndNotebooks(s.toString());
			}
			
			@Override
			public void afterTextChanged(Editable s) {}
		});
	}
	
	private void filterNotesAndNotebooks(String query) {
		// Filter notebooks
		ArrayList<HashMap<String, Object>> filteredNotebooks = new ArrayList<>();
		for (HashMap<String, Object> notebook : notebooks) {
			if (notebook.get("name").toString().toLowerCase().contains(query.toLowerCase())) {
				filteredNotebooks.add(notebook);
			}
		}
		displayFilteredNotebooks(filteredNotebooks);
		
		// Filter notes
		ArrayList<HashMap<String, Object>> filteredNotes = new ArrayList<>();
		for (HashMap<String, Object> note : notes) {
			if ((note.get("notebook") == null || note.get("notebook").toString().isEmpty()) &&
			(note.get("title").toString().toLowerCase().contains(query.toLowerCase()) ||
			note.get("content").toString().toLowerCase().contains(query.toLowerCase()))) {
				filteredNotes.add(note);
			}
		}
		displayFilteredNotes(filteredNotes);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		loadData(); // Refresh data when returning to this activity
	}
	
	private void loadData() {
		// Load notebooks
		String notebooksJson = sharedPreferences.getString("notebooks", "");
		if (notebooksJson.isEmpty()) {
			// Create default notebooks if none exist
			notebooks = new ArrayList<>();
			
			HashMap<String, Object> welcome = new HashMap<>();
			welcome.put("name", "Welcome");
			welcome.put("count", 1);
			notebooks.add(welcome);
			
			// Save default notebooks
			saveNotebooks();
		} else {
			Type type = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
			notebooks = gson.fromJson(notebooksJson, type);
		}
		
		// Load notes
		String notesJson = sharedPreferences.getString("notes", "");
		if (notesJson.isEmpty()) {
			// Create welcome note if none exist
			notes = new ArrayList<>();
			
			HashMap<String, Object> welcomeNote = new HashMap<>();
			welcomeNote.put("title", "Welcome");
			welcomeNote.put("content", "Welcome to NoteX!\n\nThank you for choosing this app. We hope you enjoy using it for all your note-taking needs.\n\nStart by creating your first note using the + button below!");
			welcomeNote.put("notebook", "Welcome");
			notes.add(welcomeNote);
			
			// Save default notes
			saveNotes();
		} else {
			Type type = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
			notes = gson.fromJson(notesJson, type);
		}
		
		// Display notebooks and notes
		displayNotebooks();
		displayNotes();
	}
	
	private void saveNotebooks() {
		String json = gson.toJson(notebooks);
		sharedPreferences.edit().putString("notebooks", json).apply();
	}
	
	private void saveNotes() {
		String json = gson.toJson(notes);
		sharedPreferences.edit().putString("notes", json).apply();
	}
	
	private void displayNotebooks() {
		displayFilteredNotebooks(notebooks);
	}
	
	private void displayNotes() {
		ArrayList<HashMap<String, Object>> unassignedNotes = new ArrayList<>();
		for (HashMap<String, Object> note : notes) {
			if (note.get("notebook") == null || note.get("notebook").toString().isEmpty()) {
				unassignedNotes.add(note);
			}
		}
		displayFilteredNotes(unassignedNotes);
	}
	
	private void displayFilteredNotebooks(ArrayList<HashMap<String, Object>> filteredNotebooks) {
		notebooksContainer.removeAllViews();
		
		if (filteredNotebooks.isEmpty()) {
			emptyNotebooksView.setVisibility(View.VISIBLE);
			notebooksContainer.setVisibility(View.GONE);
		} else {
			emptyNotebooksView.setVisibility(View.GONE);
			notebooksContainer.setVisibility(View.VISIBLE);
			
			for (final HashMap<String, Object> notebook : filteredNotebooks) {
				View notebookView = LayoutInflater.from(this).inflate(R.layout.item_notebook, notebooksContainer, false);
				
				TextView nameText = notebookView.findViewById(R.id.notebook_name);
				TextView countText = notebookView.findViewById(R.id.notebook_count);
				ImageView moreButton = notebookView.findViewById(R.id.more_button);
				
				nameText.setText(notebook.get("name").toString());
				countText.setText(String.format("%d notes", ((Number)notebook.get("count")).intValue()));
				
				moreButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						OptionsBottomSheet bottomSheet = new OptionsBottomSheet(
						notebook.get("name").toString(),
						false, // isNote = false
						new OptionsBottomSheet.OptionsListener() {
							@Override
							public void onEditSelected() {
								// Not used for notebooks
							}
							
							@Override
							public void onRenameSelected(String newName) {
								notebook.put("name", newName);
								saveNotebooks();
								loadData(); // Refresh UI
							}
							
							@Override
							public void onDeleteSelected() {
								// Confirm deletion first
								new MaterialAlertDialogBuilder(MainActivity.this)
								.setTitle("Delete Notebook")
								.setMessage("Are you sure you want to delete this notebook? All notes inside will be moved to unassigned.")
								.setPositiveButton("Delete", (dialog, which) -> {
									// Move all notes to unassigned
									for (HashMap<String, Object> note : notes) {
										if (note.get("notebook").toString().equals(notebook.get("name").toString())) {
											note.put("notebook", "");
										}
									}
									notebooks.remove(notebook);
									saveNotes();
									saveNotebooks();
									loadData();
								})
								.setNegativeButton("Cancel", null)
								.show();
							}
							
							@Override
							public void onExportSelected() {
								exportNotebook(notebook.get("name").toString());
							}
							
							@Override
							public void onShareSelected() {
								// Implement share functionality
								Toast.makeText(MainActivity.this, "Share notebook: " + notebook.get("name"), Toast.LENGTH_SHORT).show();
							}
						});
						bottomSheet.show(getSupportFragmentManager(), "OptionsBottomSheet");
					}
				});
				
				notebookView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this, ViewActivity.class);
						intent.putExtra("type", "notebook");
						intent.putExtra("name", notebook.get("name").toString());
						startActivity(intent);
					}
				});
				
				notebooksContainer.addView(notebookView);
			}
		}
	}
	
	private void displayFilteredNotes(ArrayList<HashMap<String, Object>> filteredNotes) {
		notesContainer.removeAllViews();
		
		if (filteredNotes.isEmpty()) {
			emptyNotesView.setVisibility(View.VISIBLE);
			notesContainer.setVisibility(View.GONE);
		} else {
			emptyNotesView.setVisibility(View.GONE);
			notesContainer.setVisibility(View.VISIBLE);
			
			for (final HashMap<String, Object> note : filteredNotes) {
				View noteView = LayoutInflater.from(this).inflate(R.layout.item_note, notesContainer, false);
				
				TextView titleText = noteView.findViewById(R.id.note_title);
				ImageView moreButton = noteView.findViewById(R.id.more_button);
				
				titleText.setText(note.get("title").toString());
				
				moreButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						OptionsBottomSheet bottomSheet = new OptionsBottomSheet(
						note.get("title").toString(),
						true, // isNote = true
						new OptionsBottomSheet.OptionsListener() {
							@Override
							public void onEditSelected() {
								Intent intent = new Intent(MainActivity.this, EditActivity.class);
								intent.putExtra("title", note.get("title").toString());
								startActivity(intent);
							}
							
							@Override
							public void onRenameSelected(String newName) {
								note.put("title", newName);
								saveNotes();
								loadData(); // Refresh UI
							}
							
							@Override
							public void onDeleteSelected() {
								new MaterialAlertDialogBuilder(MainActivity.this)
								.setTitle("Delete Note")
								.setMessage("Are you sure you want to delete this note?")
								.setPositiveButton("Delete", (dialog, which) -> {
									notes.remove(note);
									saveNotes();
									loadData();
								})
								.setNegativeButton("Cancel", null)
								.show();
							}
							
							@Override
							public void onExportSelected() {
								exportNote(note);
							}
							
							@Override
							public void onShareSelected() {
								// Implement share functionality
								Intent shareIntent = new Intent(Intent.ACTION_SEND);
								shareIntent.setType("text/plain");
								shareIntent.putExtra(Intent.EXTRA_SUBJECT, note.get("title").toString());
								shareIntent.putExtra(Intent.EXTRA_TEXT, note.get("content").toString());
								startActivity(Intent.createChooser(shareIntent, "Share Note"));
							}
						});
						bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
					}
				});
				
				noteView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this, ViewActivity.class);
						intent.putExtra("type", "note");
						intent.putExtra("title", note.get("title").toString());
						startActivity(intent);
					}
				});
				
				notesContainer.addView(noteView);
			}
		}
	}
	
	private void exportNote(HashMap<String, Object> note) {
		try {
			String fileName = note.get("title").toString().replaceAll("[^a-zA-Z0-9]", "_") + ".txt";
			File downloadsDir = new File(getExternalFilesDir(null), "exports");
			if (!downloadsDir.exists()) {
				downloadsDir.mkdirs();
			}
			
			File file = new File(downloadsDir, fileName);
			FileWriter writer = new FileWriter(file);
			
			writer.write("Title: " + note.get("title").toString() + "\n");
			String notebook = note.get("notebook").toString();
			if (!notebook.isEmpty()) {
				writer.write("Notebook: " + notebook + "\n");
			}
			writer.write("\n" + note.get("content").toString());
			writer.close();
			
			// Share the exported file
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, note.get("title").toString());
			shareIntent.putExtra(Intent.EXTRA_TEXT, "Note exported to: " + file.getAbsolutePath());
			startActivity(Intent.createChooser(shareIntent, "Export Note"));
			
			showSnackbar("Note exported successfully");
		} catch (IOException e) {
			showSnackbar("Failed to export note");
		}
	}
	
	private void exportNotebook(String notebookName) {
		try {
			String fileName = notebookName.replaceAll("[^a-zA-Z0-9]", "_") + "_notebook.txt";
			File downloadsDir = new File(getExternalFilesDir(null), "exports");
			if (!downloadsDir.exists()) {
				downloadsDir.mkdirs();
			}
			
			File file = new File(downloadsDir, fileName);
			FileWriter writer = new FileWriter(file);
			
			writer.write("Notebook: " + notebookName + "\n");
			writer.write("Exported on: " + new java.util.Date().toString() + "\n\n");
			
			int noteCount = 0;
			for (HashMap<String, Object> note : notes) {
				if (note.get("notebook").toString().equals(notebookName)) {
					writer.write("=== " + note.get("title").toString() + " ===\n");
					writer.write(note.get("content").toString() + "\n\n");
					noteCount++;
				}
			}
			
			if (noteCount == 0) {
				writer.write("No notes found in this notebook.\n");
			}
			
			writer.close();
			
			// Share the exported file
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Notebook: " + notebookName);
			shareIntent.putExtra(Intent.EXTRA_TEXT, "Notebook exported to: " + file.getAbsolutePath());
			startActivity(Intent.createChooser(shareIntent, "Export Notebook"));
			
			showSnackbar("Notebook exported successfully");
		} catch (IOException e) {
			showSnackbar("Failed to export notebook");
		}
	}
	
	private void showSnackbar(String message) {
		View rootView = findViewById(android.R.id.content);
		Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
			.setBackgroundTint(getColor(R.color.md_theme_light_surfaceContainer))
			.setTextColor(getColor(R.color.md_theme_light_onSurface))
			.show();
	}
	
}
