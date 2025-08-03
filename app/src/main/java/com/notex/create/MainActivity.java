package com.notex.create;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
	
	private static final String TAG = "MainActivity";
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
		
		try {
			setContentView(R.layout.main);
			
			// Initialize views with null checks
			initializeViews();
			
			// Initialize SharedPreferences and Gson
			sharedPreferences = getSharedPreferences("NoteXData", Context.MODE_PRIVATE);
			gson = new Gson();
			
			// Set up FAB
			setupFAB();
			
			// Set up menu button
			setupMenuButton();
			
			// Set up search
			setupSearch();
			
			// Load data with exception handling
			loadData();
			
		} catch (Exception e) {
			Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
			showErrorDialog("Failed to initialize app", e.getMessage());
		}
		
		// Check for previous crashes
		checkForPreviousCrashes();
	}
	
	private void initializeViews() {
		try {
			notebooksContainer = findViewById(R.id.notebooks_container);
			notesContainer = findViewById(R.id.notes_container);
			emptyNotebooksView = findViewById(R.id.empty_notebooks_view);
			emptyNotesView = findViewById(R.id.empty_notes_view);
			
			if (notebooksContainer == null || notesContainer == null || 
				emptyNotebooksView == null || emptyNotesView == null) {
				throw new IllegalStateException("Required views not found");
			}
		} catch (Exception e) {
			Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
			throw e;
		}
	}
	
	private void setupFAB() {
		try {
			FloatingActionButton fab = findViewById(R.id.fab_add);
			if (fab != null) {
				fab.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(MainActivity.this, EditActivity.class);
						startActivity(intent);
					}
				});
			}
		} catch (Exception e) {
			Log.e(TAG, "Error setting up FAB: " + e.getMessage(), e);
		}
	}
	
	private void setupMenuButton() {
		try {
			View menuButton = findViewById(R.id.menu_button);
			if (menuButton != null) {
				menuButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						showMenuOptions();
					}
				});
			}
		} catch (Exception e) {
			Log.e(TAG, "Error setting up menu button: " + e.getMessage(), e);
		}
	}
	
	private void showMenuOptions() {
		try {
			android.widget.PopupMenu popup = new android.widget.PopupMenu(this, findViewById(R.id.menu_button));
			popup.getMenu().add("Settings");
			popup.getMenu().add("Debug Console");
			
			popup.setOnMenuItemClickListener(new android.widget.PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(android.view.MenuItem item) {
					if (item.getTitle().equals("Settings")) {
						Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
						startActivity(intent);
						return true;
					} else if (item.getTitle().equals("Debug Console")) {
						Intent intent = new Intent(MainActivity.this, DebugActivity.class);
						startActivity(intent);
						return true;
					}
					return false;
				}
			});
			
			popup.show();
		} catch (Exception e) {
			Log.e(TAG, "Error showing menu options: " + e.getMessage(), e);
			// Fallback to settings
			Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
			startActivity(intent);
		}
	}
	
	private void setupSearch() {
		try {
			EditText searchEdit = findViewById(R.id.search_edit);
			if (searchEdit != null) {
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
		} catch (Exception e) {
			Log.e(TAG, "Error setting up search: " + e.getMessage(), e);
		}
	}
	
	private void filterNotesAndNotebooks(String query) {
		try {
			if (notebooks == null || notes == null) {
				return;
			}
			
			// Filter notebooks
			ArrayList<HashMap<String, Object>> filteredNotebooks = new ArrayList<>();
			for (HashMap<String, Object> notebook : notebooks) {
				if (notebook != null && notebook.get("name") != null && 
					notebook.get("name").toString().toLowerCase().contains(query.toLowerCase())) {
					filteredNotebooks.add(notebook);
				}
			}
			displayFilteredNotebooks(filteredNotebooks);
			
			// Filter notes
			ArrayList<HashMap<String, Object>> filteredNotes = new ArrayList<>();
			for (HashMap<String, Object> note : notes) {
				if (note != null && note.get("title") != null && note.get("content") != null) {
					String notebook = note.get("notebook") != null ? note.get("notebook").toString() : "";
					if (notebook.isEmpty() &&
						(note.get("title").toString().toLowerCase().contains(query.toLowerCase()) ||
						note.get("content").toString().toLowerCase().contains(query.toLowerCase()))) {
						filteredNotes.add(note);
					}
				}
			}
			displayFilteredNotes(filteredNotes);
		} catch (Exception e) {
			Log.e(TAG, "Error filtering data: " + e.getMessage(), e);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		try {
			loadData(); // Refresh data when returning to this activity
		} catch (Exception e) {
			Log.e(TAG, "Error in onResume: " + e.getMessage(), e);
		}
	}
	
	private void loadData() {
		try {
			// Initialize collections if null
			if (notebooks == null) {
				notebooks = new ArrayList<>();
			}
			if (notes == null) {
				notes = new ArrayList<>();
			}
			
			// Load notebooks
			String notebooksJson = sharedPreferences.getString("notebooks", "");
			if (notebooksJson.isEmpty()) {
				// Create default notebooks if none exist
				notebooks.clear();
				
				HashMap<String, Object> welcome = new HashMap<>();
				welcome.put("name", "Welcome");
				welcome.put("count", 1);
				notebooks.add(welcome);
				
				// Save default notebooks
				saveNotebooks();
			} else {
				try {
					Type type = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
					ArrayList<HashMap<String, Object>> loadedNotebooks = gson.fromJson(notebooksJson, type);
					if (loadedNotebooks != null) {
						notebooks = loadedNotebooks;
					}
				} catch (Exception e) {
					Log.e(TAG, "Error parsing notebooks JSON: " + e.getMessage(), e);
					// Reset to default if parsing fails
					notebooks.clear();
					HashMap<String, Object> welcome = new HashMap<>();
					welcome.put("name", "Welcome");
					welcome.put("count", 1);
					notebooks.add(welcome);
					saveNotebooks();
				}
			}
			
			// Load notes
			String notesJson = sharedPreferences.getString("notes", "");
			if (notesJson.isEmpty()) {
				// Create welcome note if none exist
				notes.clear();
				
				HashMap<String, Object> welcomeNote = new HashMap<>();
				welcomeNote.put("title", "Welcome");
				welcomeNote.put("content", "Welcome to NoteX!\n\nThank you for choosing this app. We hope you enjoy using it for all your note-taking needs.\n\nStart by creating your first note using the + button below!");
				welcomeNote.put("notebook", "Welcome");
				notes.add(welcomeNote);
				
				// Save default notes
				saveNotes();
			} else {
				try {
					Type type = new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType();
					ArrayList<HashMap<String, Object>> loadedNotes = gson.fromJson(notesJson, type);
					if (loadedNotes != null) {
						notes = loadedNotes;
					}
				} catch (Exception e) {
					Log.e(TAG, "Error parsing notes JSON: " + e.getMessage(), e);
					// Reset to default if parsing fails
					notes.clear();
					HashMap<String, Object> welcomeNote = new HashMap<>();
					welcomeNote.put("title", "Welcome");
					welcomeNote.put("content", "Welcome to NoteX!\n\nThank you for choosing this app. We hope you enjoy using it for all your note-taking needs.\n\nStart by creating your first note using the + button below!");
					welcomeNote.put("notebook", "Welcome");
					notes.add(welcomeNote);
					saveNotes();
				}
			}
			
			// Display notebooks and notes
			displayNotebooks();
			displayNotes();
			
		} catch (Exception e) {
			Log.e(TAG, "Error loading data: " + e.getMessage(), e);
			showErrorDialog("Failed to load data", e.getMessage());
		}
	}
	
	private void saveNotebooks() {
		try {
			if (notebooks != null) {
				String json = gson.toJson(notebooks);
				sharedPreferences.edit().putString("notebooks", json).apply();
			}
		} catch (Exception e) {
			Log.e(TAG, "Error saving notebooks: " + e.getMessage(), e);
		}
	}
	
	private void saveNotes() {
		try {
			if (notes != null) {
				String json = gson.toJson(notes);
				sharedPreferences.edit().putString("notes", json).apply();
			}
		} catch (Exception e) {
			Log.e(TAG, "Error saving notes: " + e.getMessage(), e);
		}
	}
	
	private void displayNotebooks() {
		try {
			displayFilteredNotebooks(notebooks);
		} catch (Exception e) {
			Log.e(TAG, "Error displaying notebooks: " + e.getMessage(), e);
		}
	}
	
	private void displayNotes() {
		try {
			ArrayList<HashMap<String, Object>> unassignedNotes = new ArrayList<>();
			if (notes != null) {
				for (HashMap<String, Object> note : notes) {
					if (note != null && (note.get("notebook") == null || note.get("notebook").toString().isEmpty())) {
						unassignedNotes.add(note);
					}
				}
			}
			displayFilteredNotes(unassignedNotes);
		} catch (Exception e) {
			Log.e(TAG, "Error displaying notes: " + e.getMessage(), e);
		}
	}
	
	private void displayFilteredNotebooks(ArrayList<HashMap<String, Object>> filteredNotebooks) {
		try {
			if (notebooksContainer == null) return;
			
			notebooksContainer.removeAllViews();
			
			if (filteredNotebooks == null || filteredNotebooks.isEmpty()) {
				if (emptyNotebooksView != null) {
					emptyNotebooksView.setVisibility(View.VISIBLE);
				}
				notebooksContainer.setVisibility(View.GONE);
			} else {
				if (emptyNotebooksView != null) {
					emptyNotebooksView.setVisibility(View.GONE);
				}
				notebooksContainer.setVisibility(View.VISIBLE);
				
				for (final HashMap<String, Object> notebook : filteredNotebooks) {
					if (notebook == null) continue;
					
					View notebookView = LayoutInflater.from(this).inflate(R.layout.item_notebook, notebooksContainer, false);
					
					TextView nameText = notebookView.findViewById(R.id.notebook_name);
					TextView countText = notebookView.findViewById(R.id.notebook_count);
					ImageView moreButton = notebookView.findViewById(R.id.more_button);
					
					if (nameText != null && notebook.get("name") != null) {
						nameText.setText(notebook.get("name").toString());
					}
					
					if (countText != null && notebook.get("count") != null) {
						try {
							countText.setText(String.format("%d notes", ((Number)notebook.get("count")).intValue()));
						} catch (Exception e) {
							countText.setText("0 notes");
						}
					}
					
					if (moreButton != null) {
						moreButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								showNotebookOptions(notebook);
							}
						});
					}
					
					notebookView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(MainActivity.this, ViewActivity.class);
							intent.putExtra("type", "notebook");
							intent.putExtra("name", notebook.get("name") != null ? notebook.get("name").toString() : "");
							startActivity(intent);
						}
					});
					
					notebooksContainer.addView(notebookView);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Error displaying filtered notebooks: " + e.getMessage(), e);
		}
	}
	
	private void showNotebookOptions(final HashMap<String, Object> notebook) {
		try {
			OptionsBottomSheet bottomSheet = new OptionsBottomSheet(
				notebook.get("name") != null ? notebook.get("name").toString() : "",
				false, // isNote = false
				new OptionsBottomSheet.OptionsListener() {
					@Override
					public void onEditSelected() {
						// Not used for notebooks
					}
					
					@Override
					public void onRenameSelected(String newName) {
						if (notebook != null) {
							notebook.put("name", newName);
							saveNotebooks();
							loadData(); // Refresh UI
						}
					}
					
					@Override
					public void onDeleteSelected() {
						// Confirm deletion first
						new MaterialAlertDialogBuilder(MainActivity.this)
						.setTitle("Delete Notebook")
						.setMessage("Are you sure you want to delete this notebook? All notes inside will be moved to unassigned.")
						.setPositiveButton("Delete", (dialog, which) -> {
							try {
								// Move all notes to unassigned
								if (notes != null) {
									for (HashMap<String, Object> note : notes) {
										if (note != null && note.get("notebook") != null && 
											note.get("notebook").toString().equals(notebook.get("name").toString())) {
											note.put("notebook", "");
										}
									}
								}
								notebooks.remove(notebook);
								saveNotes();
								saveNotebooks();
								loadData();
							} catch (Exception e) {
								Log.e(TAG, "Error deleting notebook: " + e.getMessage(), e);
							}
						})
						.setNegativeButton("Cancel", null)
						.show();
					}
					
					@Override
					public void onExportSelected() {
						exportNotebook(notebook.get("name") != null ? notebook.get("name").toString() : "");
					}
					
					@Override
					public void onShareSelected() {
						// Implement share functionality
						Toast.makeText(MainActivity.this, "Share notebook: " + (notebook.get("name") != null ? notebook.get("name").toString() : ""), Toast.LENGTH_SHORT).show();
					}
				});
			bottomSheet.show(getSupportFragmentManager(), "OptionsBottomSheet");
		} catch (Exception e) {
			Log.e(TAG, "Error showing notebook options: " + e.getMessage(), e);
		}
	}
	
	private void displayFilteredNotes(ArrayList<HashMap<String, Object>> filteredNotes) {
		try {
			if (notesContainer == null) return;
			
			notesContainer.removeAllViews();
			
			if (filteredNotes == null || filteredNotes.isEmpty()) {
				if (emptyNotesView != null) {
					emptyNotesView.setVisibility(View.VISIBLE);
				}
				notesContainer.setVisibility(View.GONE);
			} else {
				if (emptyNotesView != null) {
					emptyNotesView.setVisibility(View.GONE);
				}
				notesContainer.setVisibility(View.VISIBLE);
				
				for (final HashMap<String, Object> note : filteredNotes) {
					if (note == null) continue;
					
					View noteView = LayoutInflater.from(this).inflate(R.layout.item_note, notesContainer, false);
					
					TextView titleText = noteView.findViewById(R.id.note_title);
					ImageView moreButton = noteView.findViewById(R.id.more_button);
					
					if (titleText != null && note.get("title") != null) {
						titleText.setText(note.get("title").toString());
					}
					
					if (moreButton != null) {
						moreButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								showNoteOptions(note);
							}
						});
					}
					
					noteView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(MainActivity.this, ViewActivity.class);
							intent.putExtra("type", "note");
							intent.putExtra("title", note.get("title") != null ? note.get("title").toString() : "");
							startActivity(intent);
						}
					});
					
					notesContainer.addView(noteView);
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Error displaying filtered notes: " + e.getMessage(), e);
		}
	}
	
	private void showNoteOptions(final HashMap<String, Object> note) {
		try {
			OptionsBottomSheet bottomSheet = new OptionsBottomSheet(
				note.get("title") != null ? note.get("title").toString() : "",
				true, // isNote = true
				new OptionsBottomSheet.OptionsListener() {
					@Override
					public void onEditSelected() {
						Intent intent = new Intent(MainActivity.this, EditActivity.class);
						intent.putExtra("title", note.get("title") != null ? note.get("title").toString() : "");
						startActivity(intent);
					}
					
					@Override
					public void onRenameSelected(String newName) {
						if (note != null) {
							note.put("title", newName);
							saveNotes();
							loadData(); // Refresh UI
						}
					}
					
					@Override
					public void onDeleteSelected() {
						new MaterialAlertDialogBuilder(MainActivity.this)
						.setTitle("Delete Note")
						.setMessage("Are you sure you want to delete this note?")
						.setPositiveButton("Delete", (dialog, which) -> {
							try {
								if (notes != null) {
									notes.remove(note);
									saveNotes();
									loadData();
								}
							} catch (Exception e) {
								Log.e(TAG, "Error deleting note: " + e.getMessage(), e);
							}
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
						shareIntent.putExtra(Intent.EXTRA_SUBJECT, note.get("title") != null ? note.get("title").toString() : "");
						shareIntent.putExtra(Intent.EXTRA_TEXT, note.get("content") != null ? note.get("content").toString() : "");
						startActivity(Intent.createChooser(shareIntent, "Share Note"));
					}
				});
			bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
		} catch (Exception e) {
			Log.e(TAG, "Error showing note options: " + e.getMessage(), e);
		}
	}
	
	private void exportNote(HashMap<String, Object> note) {
		try {
			if (note == null || note.get("title") == null) {
				showSnackbar("Invalid note data");
				return;
			}
			
			String fileName = note.get("title").toString().replaceAll("[^a-zA-Z0-9]", "_") + ".txt";
			File downloadsDir = new File(getExternalFilesDir(null), "exports");
			if (!downloadsDir.exists()) {
				downloadsDir.mkdirs();
			}
			
			File file = new File(downloadsDir, fileName);
			FileWriter writer = new FileWriter(file);
			
			writer.write("Title: " + note.get("title").toString() + "\n");
			String notebook = note.get("notebook") != null ? note.get("notebook").toString() : "";
			if (!notebook.isEmpty()) {
				writer.write("Notebook: " + notebook + "\n");
			}
			writer.write("\n" + (note.get("content") != null ? note.get("content").toString() : ""));
			writer.close();
			
			// Share the exported file
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, note.get("title").toString());
			shareIntent.putExtra(Intent.EXTRA_TEXT, "Note exported to: " + file.getAbsolutePath());
			startActivity(Intent.createChooser(shareIntent, "Export Note"));
			
			showSnackbar("Note exported successfully");
		} catch (IOException e) {
			Log.e(TAG, "Error exporting note: " + e.getMessage(), e);
			showSnackbar("Failed to export note");
		} catch (Exception e) {
			Log.e(TAG, "Error exporting note: " + e.getMessage(), e);
			showSnackbar("Failed to export note");
		}
	}
	
	private void exportNotebook(String notebookName) {
		try {
			if (notebookName == null || notebookName.isEmpty()) {
				showSnackbar("Invalid notebook name");
				return;
			}
			
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
			if (notes != null) {
				for (HashMap<String, Object> note : notes) {
					if (note != null && note.get("notebook") != null && 
						note.get("notebook").toString().equals(notebookName)) {
						writer.write("=== " + (note.get("title") != null ? note.get("title").toString() : "Untitled") + " ===\n");
						writer.write((note.get("content") != null ? note.get("content").toString() : "") + "\n\n");
						noteCount++;
					}
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
			Log.e(TAG, "Error exporting notebook: " + e.getMessage(), e);
			showSnackbar("Failed to export notebook");
		} catch (Exception e) {
			Log.e(TAG, "Error exporting notebook: " + e.getMessage(), e);
			showSnackbar("Failed to export notebook");
		}
	}
	
	private void showSnackbar(String message) {
		try {
			View rootView = findViewById(android.R.id.content);
			if (rootView != null) {
				Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
					.setBackgroundTint(getColor(R.color.md_theme_light_surfaceContainer))
					.setTextColor(getColor(R.color.md_theme_light_onSurface))
					.show();
			}
		} catch (Exception e) {
			Log.e(TAG, "Error showing snackbar: " + e.getMessage(), e);
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
	}
	
	private void checkForPreviousCrashes() {
		try {
			SharedPreferences debugPrefs = getSharedPreferences("debug_prefs", Context.MODE_PRIVATE);
			String lastCrash = debugPrefs.getString("last_crash", null);
			
			if (lastCrash != null) {
				// Show a notification about the previous crash
				new MaterialAlertDialogBuilder(this)
					.setTitle("Previous Crash Detected")
					.setMessage("The app crashed previously. Would you like to view the crash details in the debug console?")
					.setPositiveButton("View Details", (dialog, which) -> {
						Intent intent = new Intent(MainActivity.this, DebugActivity.class);
						startActivity(intent);
					})
					.setNegativeButton("Dismiss", null)
					.show();
			}
		} catch (Exception e) {
			Log.e(TAG, "Error checking for previous crashes: " + e.getMessage(), e);
		}
	}
	
	private void showErrorDialog(String title, String message) {
		try {
			new MaterialAlertDialogBuilder(this)
				.setTitle(title)
				.setMessage(message + "\n\nWould you like to open the debug console to see more details?")
				.setPositiveButton("Open Debug Console", (dialog, which) -> {
					Intent intent = new Intent(MainActivity.this, DebugActivity.class);
					startActivity(intent);
				})
				.setNegativeButton("OK", null)
				.show();
		} catch (Exception e) {
			Log.e(TAG, "Error showing error dialog: " + e.getMessage(), e);
			Toast.makeText(this, title + ": " + message, Toast.LENGTH_LONG).show();
		}
	}
	
}
