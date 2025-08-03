package com.notex.create

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.notex.create.viewmodel.NoteViewModel

class ViewActivity : AppCompatActivity() {

    private val noteViewModel: NoteViewModel by viewModels()
    private var viewType: String? = null
    private var viewId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewType = intent.getStringExtra("type")
        viewId = intent.getIntExtra("id", -1)
        setContent {
            ViewScreen(
                noteViewModel = noteViewModel,
                viewType = viewType,
                viewId = viewId,
                onEditClick = {
                    val intent = Intent(this@ViewActivity, EditActivity::class.java)
                    intent.putExtra("id", viewId)
                    startActivity(intent)
                    finish()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewScreen(
    noteViewModel: NoteViewModel,
    viewType: String?,
    viewId: Int,
    onEditClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (viewType == "note") {
                        val note by noteViewModel.allNotes.observeAsState(initial = emptyList())
                            .let { notes ->
                                remember {
                                    mutableStateOf(notes.value?.find { it.id == viewId })
                                }
                            }
                        Text(note?.title ?: "Note")
                    } else {
                        val notebook by noteViewModel.allNotebooks.observeAsState(initial = emptyList())
                            .let { notebooks ->
                                remember {
                                    mutableStateOf(notebooks.value?.find { it.id == viewId })
                                }
                            }
                        Text(notebook?.name ?: "Notebook")
                    }
                },
                actions = {
                    if (viewType == "note") {
                        IconButton(onClick = onEditClick) {
                            Icon(painter = painterResource(id = R.drawable.icon_edit_round), contentDescription = "Edit")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (viewType == "note") {
                val note by noteViewModel.allNotes.observeAsState(initial = emptyList())
                    .let { notes ->
                        remember {
                            mutableStateOf(notes.value?.find { it.id == viewId })
                        }
                    }
                note?.let {
                    Text(text = it.content)
                }
            } else {
                val notes by noteViewModel.getNotesByNotebook(viewId).observeAsState(initial = emptyList())
                Column {
                    notes.forEach { note ->
                        Text(text = "# ${note.title}")
                        Text(text = note.content)
                        Text(text = "---")
                    }
                }
            }
        }
    }
}
