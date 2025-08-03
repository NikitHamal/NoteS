package com.notex.create.data

import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDao) {

    val allNotes: LiveData<List<Note>> = noteDao.getAllNotes()
    val allNotebooks: LiveData<List<Notebook>> = noteDao.getAllNotebooks()

    fun getNotesByNotebook(notebookId: Int): LiveData<List<Note>> {
        return noteDao.getNotesByNotebook(notebookId)
    }

    suspend fun insertNote(note: Note) {
        noteDao.insertNote(note)
    }

    suspend fun updateNote(note: Note) {
        noteDao.updateNote(note)
    }

    suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    suspend fun insertNotebook(notebook: Notebook) {
        noteDao.insertNotebook(notebook)
    }

    suspend fun updateNotebook(notebook: Notebook) {
        noteDao.updateNotebook(notebook)
    }

    suspend fun deleteNotebook(notebook: Notebook) {
        noteDao.deleteNotebook(notebook)
    }
}
