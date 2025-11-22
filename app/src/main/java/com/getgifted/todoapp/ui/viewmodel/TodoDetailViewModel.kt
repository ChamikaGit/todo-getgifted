package com.getgifted.todoapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.getgifted.todoapp.data.model.Todo
import com.getgifted.todoapp.data.repository.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the Todo Detail screen
 * Handles displaying and updating a single todo
 */
@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {
    
    // State for the current todo
    private val _todo = MutableStateFlow<Todo?>(null)
    val todo: StateFlow<Todo?> = _todo.asStateFlow()
    
    // State for loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // State for save success
    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess.asStateFlow()
    
    /**
     * Load todo by ID
     */
    fun loadTodo(todoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val todo = repository.getTodoById(todoId)
            _todo.value = todo
            _isLoading.value = false
        }
    }
    
    /**
     * Update todo description
     */
    fun updateDescription(description: String) {
        val currentTodo = _todo.value ?: return
        
        viewModelScope.launch {
            _isLoading.value = true
            
            // Update in database
            repository.updateTodoDescription(currentTodo.id, description)
            
            // Update local state
            _todo.value = currentTodo.copy(description = description)
            
            _isLoading.value = false
            _saveSuccess.value = true
        }
    }
    
    /**
     * Reset save success state
     */
    fun resetSaveSuccess() {
        _saveSuccess.value = false
    }
}
