package com.getgifted.todoapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Todo entity representing a todo item from the API and local database
 */
@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey
    @SerializedName("id")
    val id: Int,
    
    @SerializedName("userId")
    val userId: Int,
    
    @SerializedName("title")
    val title: String,
    
    @SerializedName("completed")
    val completed: Boolean,
    
    // Local-only field for user-added description
    val description: String? = null
)
