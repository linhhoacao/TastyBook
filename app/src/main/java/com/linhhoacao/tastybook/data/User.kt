package com.linhhoacao.tastybook.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Create a data class representing a table in the database.
@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val level: String = "1",
    val score: Int = 0,
    val duration: Int = 0,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP") val date: Long = System.currentTimeMillis()
)
{}
