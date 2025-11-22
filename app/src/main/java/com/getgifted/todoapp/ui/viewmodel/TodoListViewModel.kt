package com.getgifted.todoapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.getgifted.todoapp.data.model.Todo
import com.getgifted.todoapp.data.repository.TodoRepository
import com.getgifted.todoapp.data.utils.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the Todo List screen
 * Handles fetching and displaying todos with pagination
 */
@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel() {

    companion object {
        private const val TAG = "TodoListViewModel"
    }
    
    // State for todos list
    private val _todos = MutableStateFlow<List<Todo>>(emptyList())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()
    
    // State for loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // State for pagination loading
    private val _isPaginationLoading = MutableStateFlow(false)
    val isPaginationLoading: StateFlow<Boolean> = _isPaginationLoading.asStateFlow()

    // State for last page
    private var isLastPage = false
    
    // State for errors
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    // Current page for pagination
    private var currentPage = 0
    
    // Flag to prevent multiple simultaneous loads
    private var isLoadingMore = false
    
    init {
        // Observe local database
        observeTodos()
    }
    
    /**
     * Observe todos from local database
     */
    private fun observeTodos() {
        viewModelScope.launch {
            repository.getTodos().collect { todoList ->
                _todos.value = todoList
            }
        }
    }
    
    /**
     * Fetch initial data if database is empty
     */
    public fun fetchInitialData() {
        viewModelScope.launch {
            if (repository.shouldFetchInitialData()) {
                fetchTodos()
            }
        }
    }
    
    /**
     * Fetch todos from API
     */
    fun fetchTodos() {
        if (isLoadingMore || isLastPage) {
            Log.d(TAG, "fetchTodos: Skipping request - Loading: $isLoadingMore, LastPage: $isLastPage")
            return
        }
        
        Log.d(TAG, "fetchTodos: Starting to fetch page $currentPage")
        
        viewModelScope.launch {
            isLoadingMore = true
            _isLoading.value = true
            
            // If we already have items, this is a pagination load
            if (_todos.value.isNotEmpty()) {
                _isPaginationLoading.value = true
            }
            
            repository.fetchTodosFromApi(currentPage).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        _error.value = null
                        val itemCount = result.data?.size ?: 0
                        Log.d(TAG, "fetchTodos: Success - Fetched $itemCount items for page $currentPage")
                        if (!result.data.isNullOrEmpty()) {
                            currentPage++
                            Log.d(TAG, "fetchTodos: Incremented page to $currentPage")
                            if (itemCount < TodoRepository.PAGE_SIZE) {
                                isLastPage = true
                                Log.d(TAG, "fetchTodos: Reached last page (items < page size)")
                            }
                        } else {
                            isLastPage = true
                            Log.d(TAG, "fetchTodos: No more data available")
                        }
                        _isLoading.value = false
                        _isPaginationLoading.value = false
                        isLoadingMore = false
                    }
                    is Resource.Error -> {
                        _error.value = result.message
                        Log.e(TAG, "fetchTodos: Error - ${result.message}")
                        _isLoading.value = false
                        _isPaginationLoading.value = false
                        isLoadingMore = false
                    }
                    is Resource.Loading -> {
                        Log.d(TAG, "fetchTodos: Loading state")
                    }
                }
            }
        }
    }
    /**
     * Load more todos (pagination)
     */
    fun loadMoreTodos() {
        fetchTodos()
    }
    
    /**
     * Refresh todos (reset pagination)
     */
    fun refreshTodos() {
        currentPage = 0
        isLastPage = false
        fetchTodos()
    }
    
    /**
     * Clear error message
     */
    fun clearError() {
        _error.value = null
    }
}
