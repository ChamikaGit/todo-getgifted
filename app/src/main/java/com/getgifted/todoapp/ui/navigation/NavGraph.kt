package com.getgifted.todoapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.getgifted.todoapp.ui.screens.SplashScreen
import com.getgifted.todoapp.ui.screens.TodoDetailScreen
import com.getgifted.todoapp.ui.screens.TodoListScreen

/**
 * Navigation graph for the app
 */
@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToTodoList = {
                    navController.navigate(Screen.TodoList.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.TodoList.route) {
            TodoListScreen(
                onNavigateToDetail = { todoId ->
                    navController.navigate(Screen.TodoDetail.createRoute(todoId))
                }
            )
        }
        
        composable(
            route = Screen.TodoDetail.route,
            arguments = listOf(
                navArgument("todoId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getInt("todoId") ?: -1
            TodoDetailScreen(
                todoId = todoId,
                onNavigateBack = {
                    navController.navigateUp()
                }
            )
        }
    }
}

/**
 * Representing app screens
 */
sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object TodoList : Screen("todo_list")
    object TodoDetail : Screen("todo_detail/{todoId}") {
        fun createRoute(todoId: Int) = "todo_detail/$todoId"
    }
}
