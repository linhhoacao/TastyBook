package com.linhhoacao.tastybook.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.linhhoacao.tastybook.presenter.AddRecipePresenter
import com.linhhoacao.tastybook.presenter.AuthPresenter
import com.linhhoacao.tastybook.presenter.HomePresenter
import com.linhhoacao.tastybook.presenter.RecipeDetailPresenter
import com.linhhoacao.tastybook.presenter.SearchPresenter
import com.linhhoacao.tastybook.presenter.UserPresenter
import com.linhhoacao.tastybook.ui.add.AddRecipeScreen
import com.linhhoacao.tastybook.ui.all.AllRecipeScreen
import com.linhhoacao.tastybook.ui.auth.LoginScreen
import com.linhhoacao.tastybook.ui.detail.RecipeDetailScreen
import com.linhhoacao.tastybook.ui.home.HomeScreen
import com.linhhoacao.tastybook.ui.search.SearchScreen
import com.linhhoacao.tastybook.ui.user.EditProfileScreen
import com.linhhoacao.tastybook.ui.user.SignUpScreen
import com.linhhoacao.tastybook.ui.user.UserScreen

@Composable
fun AppNavigation(
    homePresenter: HomePresenter,
    searchPresenter: SearchPresenter,
    addRecipePresenter: AddRecipePresenter,
    recipeDetailPresenter: RecipeDetailPresenter,
    authPresenter: AuthPresenter,
    userPresenter: UserPresenter
) {
    val navController = rememberNavController()

    val authState by authPresenter.authState.collectAsState()

    val startDestination = if (authState.isAuthenticated) "home" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                presenter = authPresenter,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToSignUp = { navController.navigate("signup") }
            )
        }

        composable("signup") {
            SignUpScreen(
                presenter = authPresenter,
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScreen(
                presenter = homePresenter,
                userPresenter = userPresenter,
                navigateToRecipeDetail = { recipeId ->
                    navController.navigate("recipe_detail/$recipeId")
                },
                navigateToSearch = { navController.navigate("search") },
                navigateToAddRecipe = { navController.navigate("add_recipe") },
                navigateToProfile = { navController.navigate("profile") },
                navigateToSearchWithString = { query -> navController.navigate("search/$query") },
                navigateToAllRecipe = { navController.navigate("all_recipes") }
            )
        }

        composable("recipe_detail/{recipeId}") { backStackEntry ->
            val recipeId = backStackEntry.arguments?.getString("recipeId") ?: ""
            RecipeDetailScreen(
                presenter = recipeDetailPresenter,
                recipeId = recipeId,
                navigateBack = { navController.popBackStack() },
                navigateToAdd = { navController.navigate("add_recipe") },
                navigateToProfile = { navController.navigate("profile") },
                navigateToAllRecipes = { navController.navigate("all_recipes") },
                navigateToHome = { navController.navigate("home") },
                navigateToSearch = { navController.navigate("search") }
            )
        }

        composable(
            route = "search/{query}",
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchScreen(
                presenter = searchPresenter,
                navigateToRecipeDetail = { recipeId ->
                    navController.navigate("recipe_detail/$recipeId")
                },
                navigateToHome = { navController.navigate("home") },
                onBackClick = { navController.popBackStack() },
                query = query,
                navigateToProfile = { navController.navigate("profile") },
                navigateToAdd = { navController.navigate("add_recipe") },
                navigateToAllRecipe = { navController.navigate("all_recipes") }
            )
        }

        composable(
            route = "search",
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchScreen(
                presenter = searchPresenter,
                navigateToRecipeDetail = { recipeId ->
                    navController.navigate("recipe_detail/$recipeId")
                },
                navigateToHome = { navController.navigate("home") },
                onBackClick = { navController.popBackStack() },
                query = query,
                navigateToProfile = { navController.navigate("profile") },
                navigateToAdd = { navController.navigate("add_recipe") },
                navigateToAllRecipe = { navController.navigate("all_recipes") }
            )
        }

        composable("all_recipes") {
            AllRecipeScreen(
                presenter = homePresenter,
                navigateToRecipeDetail = { recipeId ->
                    navController.navigate("recipe_detail/$recipeId")
                },
                navigateToSearch = { navController.navigate("search") },
                navigateToAddRecipe = { navController.navigate("add_recipe") },
                navigateToProfile = { navController.navigate("profile") },
                onBackClick = { navController.popBackStack() },
                navigateToHome = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("profile") {
            UserScreen(
                userPresenter = userPresenter,
                navigateToHome = { navController.navigate("home") },
                navigateToEditProfile = {
                    navController.navigate("edit_profile")
                },
                navigateToSearch = { navController.navigate("search") },
                navigateToMyRecipes = {
                    // navController.navigate("my_recipes")
                },
                navigateToAdd = { navController.navigate("add_recipe") },
                onSignOut = {
                    authPresenter.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                },
                navigateToAllRecipe = { navController.navigate("all_recipes") }
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                userPresenter = userPresenter,
                navigateBack = {
                    navController.navigateUp()
                }
            )
        }

        composable("add_recipe") {
            AddRecipeScreen(
                presenter = addRecipePresenter,
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}