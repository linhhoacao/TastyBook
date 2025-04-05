package com.linhhoacao.tastybook.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.linhhoacao.tastybook.data.repository.RecipeRepository
import com.linhhoacao.tastybook.data.repository.UserRepository
import com.linhhoacao.tastybook.presenter.*
import com.linhhoacao.tastybook.ui.splash.SplashScreen
import com.linhhoacao.tastybook.ui.theme.TastyBookTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val recipeRepository = RecipeRepository()
        val userRepository = UserRepository()

        val homePresenter = HomePresenter(recipeRepository)
        val recipeDetailPresenter = RecipeDetailPresenter(recipeRepository)
        val searchPresenter = SearchPresenter(recipeRepository)
        val addRecipePresenter = AddRecipePresenter(recipeRepository)
        val authPresenter = AuthPresenter(userRepository)
        val userPresenter = UserPresenter(userRepository)

        setContent {
            TastyBookTheme {
                var showSplashScreen by remember { mutableStateOf(true) }

                if (showSplashScreen) {
                    SplashScreen(onStartCooking = {
                        showSplashScreen = false
                    })
                } else {
                    AppNavigation(
                        homePresenter = homePresenter,
                        recipeDetailPresenter = recipeDetailPresenter,
                        searchPresenter = searchPresenter,
                        addRecipePresenter = addRecipePresenter,
                        authPresenter = authPresenter,
                        userPresenter = userPresenter
                    )
                }
            }
        }
    }
}