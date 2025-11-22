package com.getgifted.todoapp.data.local


import androidx.room.Database

import androidx.room.RoomDatabase
import com.getgifted.todoapp.data.model.Todo

/**
 * Room database for storing todos locally
 */
@Database(
    entities = [Todo::class],
    version = 1,
    exportSchema = false
)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}
