package com.getgifted.todoapp.data.repository

import com.getgifted.todoapp.data.local.TodoDao
import com.getgifted.todoapp.data.remote.TodoApiService

import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao,
    private val apiService: TodoApiService
) {
    

}
