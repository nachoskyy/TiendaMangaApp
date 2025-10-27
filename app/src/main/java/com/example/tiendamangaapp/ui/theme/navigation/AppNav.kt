package com.example.tiendamangaapp.ui.theme.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tiendamangaapp.ui.theme.screens.CartScreen
import com.example.tiendamangaapp.ui.theme.screens.CatalogScreen
import com.example.tiendamangaapp.ui.theme.screens.DetailScreen
import com.example.tiendamangaapp.ui.theme.screens.LoginScreen
import com.example.tiendamangaapp.ui.theme.screens.HistoryScreen

object Routes {
    const val LOGIN = "login"
    const val CATALOG = "Catalog"
    const val DETAIL = "detail/{id}"
    const val CART = "cart"
    const val HISTORY = "history"
    fun detail(id: Long) = "detail/$id"
}

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Routes.LOGIN) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.CATALOG) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.CATALOG) {
            CatalogScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.CATALOG) { inclusive = true }
                    }
                },
                onViewDetail = { id -> navController.navigate(Routes.detail(id)) },
                onOpenCart = { navController.navigate(Routes.CART) }
            )
        }
        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            DetailScreen(
                mangaId = id,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Routes.CART) {
            CartScreen(
                onBack = { navController.popBackStack() },
                onOpenHistory = { navController.navigate(Routes.HISTORY) }
            )
        }
        composable(Routes.HISTORY) { HistoryScreen(onBack = { navController.popBackStack() }) }
    }
}

