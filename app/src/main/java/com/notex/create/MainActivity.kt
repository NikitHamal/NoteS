package com.notex.create

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notex.create.data.Note
import com.notex.create.data.Notebook
import com.notex.create.viewmodel.NoteViewModel

class MainActivity : AppCompatActivity() {

    private val noteViewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(noteViewModel = noteViewModel,
                onAddClick = {
                    startActivity(Intent(this@MainActivity, EditActivity::class.java))
                },
                onSettingsClick = {
                    startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
                },
                onNoteClick = {
                    val intent = Intent(this@MainActivity, ViewActivity::class.java)
                    intent.putExtra("type", "note")
                    intent.putExtra("id", it.id)
                    startActivity(intent)
                },
                onNotebookClick = {
                    val intent = Intent(this@MainActivity, ViewActivity::class.java)
                    intent.putExtra("type", "notebook")
                    intent.putExtra("id", it.id)
                    startActivity(intent)
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    noteViewModel: NoteViewModel,
    onAddClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNoteClick: (Note) -> Unit,
    onNotebookClick: (Notebook) -> Unit
) {
    val notebooks by noteViewModel.allNotebooks.observeAsState(initial = emptyList())
    val notes by noteViewModel.allNotes.observeAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }

    val filteredNotebooks = notebooks.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }
    val filteredNotes = notes.filter {
        it.notebookId == null &&
                (it.title.contains(searchQuery, ignoreCase = true) ||
                        it.content.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NoteX") },
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(painter = painterResource(id = R.drawable.icon_menu_round), contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(painter = painterResource(id = R.drawable.icon_add_round), contentDescription = "Add Note")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Text(text = "Notebooks", style = MaterialTheme.typography.headlineSmall)
            filteredNotebooks.forEach { notebook ->
                NotebookItem(notebook = notebook, onClick = { onNotebookClick(notebook) })
            }
            Text(text = "Notes", style = MaterialTheme.typography.headlineSmall)
            filteredNotes.forEach { note ->
                NoteItem(note = note, onClick = { onNoteClick(note) })
            }
        }
    }
}

@Composable
fun NotebookItem(notebook: Notebook, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Text(text = notebook.name, modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun NoteItem(note: Note, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = note.title, style = MaterialTheme.typography.headlineSmall)
            Text(text = note.content, maxLines = 2)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MaterialTheme {
        // TODO: Create a mock view model for preview
        // MainScreen(noteViewModel = noteViewModel)
    }
}
