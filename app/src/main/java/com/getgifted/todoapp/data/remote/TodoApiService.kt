package com.getgifted.todoapp.data.remote

import com.getgifted.todoapp.data.model.Todo
import retrofit2.http.GET
import retrofit2.http.Query

interface TodoApiService {

    @GET("todos")
    suspend fun getTodos(
        @Query("_start") start: Int,
        @Query("_limit") limit: Int
    ): List<Todo>
    

    @GET("todos")
    suspend fun getAllTodos(): List<Todo>
}
