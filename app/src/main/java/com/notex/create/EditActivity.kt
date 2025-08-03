package com.notex.create

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.notex.create.data.Note
import com.notex.create.viewmodel.NoteViewModel

class EditActivity : AppCompatActivity() {

    private val noteViewModel: NoteViewModel by viewModels()
    private var noteId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        noteId = intent.getIntExtra("id", -1)
        setContent {
            EditScreen(
                noteViewModel = noteViewModel,
                noteId = noteId,
                onSave = {
                    finish()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditScreen(
    noteViewModel: NoteViewModel,
    noteId: Int,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    val notebooks by noteViewModel.allNotebooks.observeAsState(initial = emptyList())
    val notes by noteViewModel.allNotes.observeAsState(initial = emptyList())
    val note = remember(notes, noteId) { notes.find { it.id == noteId } }

    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedNotebook by remember { mutableStateOf<Int?>(null) }
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(note) {
        if (note != null) {
            title = note.title
            content = note.content
            selectedNotebook = note.notebookId
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (noteId == -1) "New Note" else "Edit Note") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") }
            )
            TextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("Content") }
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = notebooks.find { it.id == selectedNotebook }?.name ?: "None",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None") },
                        onClick = {
                            selectedNotebook = null
                            expanded = false
                        }
                    )
                    notebooks.forEach { notebook ->
                        DropdownMenuItem(
                            text = { Text(notebook.name) },
                            onClick = {
                                selectedNotebook = notebook.id
                                expanded = false
                            }
                        )
                    }
                }
            }
            Button(onClick = {
                if (title.isNotBlank()) {
                    val newNote = Note(
                        id = if (noteId == -1) 0 else noteId,
                        title = title,
                        content = content,
                        notebookId = selectedNotebook
                    )
                    if (noteId == -1) {
                        noteViewModel.insertNote(newNote)
                    } else {
                        noteViewModel.updateNote(newNote)
                    }
                    Toast.makeText(context, "Note saved", Toast.LENGTH_SHORT).show()
                    onSave()
                } else {
                    Toast.makeText(context, "Please enter a title", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text("Save")
            }
        }
    }
}
