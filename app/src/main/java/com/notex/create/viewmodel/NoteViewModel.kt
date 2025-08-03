package com.notex.create.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.notex.create.data.Note
import com.notex.create.data.NoteDatabase
import com.notex.create.data.NoteRepository
import com.notex.create.data.Notebook
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NoteRepository
    val allNotes: LiveData<List<Note>>
    val allNotebooks: LiveData<List<Notebook>>

    init {
        val noteDao = NoteDatabase.getDatabase(application).noteDao()
        repository = NoteRepository(noteDao)
        allNotes = repository.allNotes
        allNotebooks = repository.allNotebooks
    }

    fun getNotesByNotebook(notebookId: Int): LiveData<List<Note>> {
        return repository.getNotesByNotebook(notebookId)
    }

    fun insertNote(note: Note) = viewModelScope.launch {
        repository.insertNote(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch {
        repository.updateNote(note)
    }

    fun deleteNote(note: Note) = viewModelScope.launch {
        repository.deleteNote(note)
    }

    fun insertNotebook(notebook: Notebook) = viewModelScope.launch {
        repository.insertNotebook(notebook)
    }

    fun updateNotebook(notebook: Notebook) = viewModelScope.launch {
        repository.updateNotebook(notebook)
    }

    fun deleteNotebook(notebook: Notebook) = viewModelScope.launch {
        repository.deleteNotebook(notebook)
    }
}
