package com.getgifted.todoapp.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.getgifted.todoapp.data.model.Todo
import com.getgifted.todoapp.data.repository.TodoRepository
import com.getgifted.todoapp.ui.viewmodel.TodoListViewModel

/**
 * Todo List screen displaying all todos with pagination
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: TodoListViewModel = hiltViewModel()
) {
    val todos by viewModel.todos.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isPaginationLoading by viewModel.isPaginationLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    val listState = rememberLazyListState()
    
    // Fetch initial data
    LaunchedEffect(Unit) {
        viewModel.fetchInitialData()
    }
    
    // Pagination logic
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                if (lastVisibleIndex != null && todos.isNotEmpty()) {
                    val pageSize = TodoRepository.PAGE_SIZE
                    val isEndOfPage = (lastVisibleIndex + 1) % pageSize == 0
                    
                    Log.d("Pagination", "Last visible = $lastVisibleIndex, isEndOfPage = $isEndOfPage")
                    
                    if (!isLoading && !isPaginationLoading && isEndOfPage && lastVisibleIndex + 1 == todos.size) {
                        Log.d("Pagination", ">>> Loading NEXT page...")
                        viewModel.loadMoreTodos()
                    }
                }
            }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Todo List",
                        fontWeight = FontWeight.Bold
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && todos.isEmpty()) {
                // Initial loading
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (error != null && todos.isEmpty()) {
                // Error state
                Text(
                    text = error ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            } else {
                // Todo list with pull to refresh
                PullToRefreshTodoList(
                    todos = todos,
                    isRefreshing = isLoading && todos.isNotEmpty() && !isPaginationLoading,
                    isPaginationLoading = isPaginationLoading,
                    listState = listState,
                    onRefresh = { viewModel.refreshTodos() },
                    onTodoClick = onNavigateToDetail
                )
            }
        }
    }
    
    // Show error toast
    error?.let {
        LaunchedEffect(it) {
            viewModel.clearError()
        }
    }
}

@Composable
private fun PullToRefreshTodoList(
    todos: List<Todo>,
    isRefreshing: Boolean,
    isPaginationLoading: Boolean,
    listState: LazyListState,
    onRefresh: () -> Unit,
    onTodoClick: (Int) -> Unit
) {
    // Note: Using Material3's PullToRefresh would be ideal, but it's still experimental
    // For now, using a simple LazyColumn with manual refresh
    Column(modifier = Modifier.fillMaxSize()) {
        if (isRefreshing) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = todos,
                key = { it.id }
            ) { todo ->
                TodoItem(
                    todo = todo,
                    onClick = { onTodoClick(todo.id) }
                )
            }
            
            // Pagination loading indicator
            if (isPaginationLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodoItem(
    todo: Todo,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Completion indicator
            if (todo.completed) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Completed",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Todo card contents
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = todo.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                // This is added for debug purpose to check the item number
                Text(
                    text = "ID: ${todo.id}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Status card with icon and text
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (todo.completed) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant
                },
                modifier = Modifier.padding(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = if (todo.completed) "Completed" else "Uncompleted",
                        tint = if (todo.completed) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.size(16.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(6.dp))
                    
                    Text(
                        text = if (todo.completed) "Completed" else "Uncompleted",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (todo.completed) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }
        }
    }
}
