package com.getgifted.todoapp.data.repository

import com.getgifted.todoapp.data.local.TodoDao
import com.getgifted.todoapp.data.model.Todo
import com.getgifted.todoapp.data.remote.TodoApiService
import com.getgifted.todoapp.data.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

import javax.inject.Inject

class TodoRepository @Inject constructor(
    private val todoDao: TodoDao,
    private val apiService: TodoApiService
) {

    companion object {
        const val PAGE_SIZE = 10
    }


    fun getTodos(): Flow<List<Todo>> {
        return todoDao.getAllTodos()
    }

    /**
     * Fetch todos from API with pagination and save to local database
     */
    fun fetchTodosFromApi(page: Int): Flow<Resource<List<Todo>>> = flow {
        emit(Resource.Loading())

        try {
            val start = page * PAGE_SIZE
            val remoteTodos = apiService.getTodos(start, PAGE_SIZE)

            // Save to local database
            todoDao.insertTodos(remoteTodos)

            emit(Resource.Success(remoteTodos))
        } catch (e: HttpException) {
            emit(Resource.Error(
                message = "Oops, something went wrong: ${e.localizedMessage}"
            ))
        } catch (e: IOException) {
            emit(Resource.Error(
                message = "Couldn't reach server, check your internet connection."
            ))
        } catch (e: Exception) {
            emit(Resource.Error(
                message = "An unexpected error occurred: ${e.localizedMessage}"
            ))
        }
    }

    /**
     * Get a specific todo by ID
     */
    suspend fun getTodoById(todoId: Int): Todo? {
        return todoDao.getTodoById(todoId)
    }

    /**
     * Update todo description
     */
    suspend fun updateTodoDescription(todoId: Int, description: String?) {
        todoDao.updateDescription(todoId, description)
    }

    /**
     * Update entire todo
     */
    suspend fun updateTodo(todo: Todo) {
        todoDao.updateTodo(todo)
    }

    /**
     * Get the count of todos in local database
     */
    suspend fun getTodoCount(): Int {
        return todoDao.getTodoCount()
    }

    /**
     * Check if we need to fetch initial data
     */
    suspend fun shouldFetchInitialData(): Boolean {
        return getTodoCount() == 0
    }
}
