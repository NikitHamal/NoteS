package com.notex.create.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notebooks")
data class Notebook(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
