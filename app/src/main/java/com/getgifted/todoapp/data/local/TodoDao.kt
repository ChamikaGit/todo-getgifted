package com.getgifted.todoapp.data.local

import androidx.room.*
import com.getgifted.todoapp.data.model.Todo
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Todo operations
 */
@Dao
interface TodoDao {
    
    /**
     * Get all todos as a Flow for reactive updates
     */
    @Query("SELECT * FROM todos ORDER BY id ASC")
    fun getAllTodos(): Flow<List<Todo>>
    
    /**
     * Get a specific todo by ID
     */
    @Query("SELECT * FROM todos WHERE id = :todoId")
    suspend fun getTodoById(todoId: Int): Todo?
    
    /**
     * Insert multiple todos (used when fetching from API)
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodos(todos: List<Todo>)
    
    /**
     * Insert a single todo
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: Todo)
    
    /**
     * Update a todo (used when updating description)
     */
    @Update
    suspend fun updateTodo(todo: Todo)
    
    /**
     * Delete all todos
     */
    @Query("DELETE FROM todos")
    suspend fun deleteAllTodos()
    
    /**
     * Get the count of todos in the database
     */
    @Query("SELECT COUNT(*) FROM todos")
    suspend fun getTodoCount(): Int
    
    /**
     * Update only the description field for a specific todo
     */
    @Query("UPDATE todos SET description = :description WHERE id = :todoId")
    suspend fun updateDescription(todoId: Int, description: String?)
}
