@file:OptIn(ExperimentalMaterial3Api::class)

package com.linhhoacao.tastybook.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.linhhoacao.tastybook.ui.theme.TastyBookTheme

//define app navigation
@Composable
fun AppNavigation() {
    //retrieve navController
    val navController = rememberNavController()
    //set navHost and the route
    NavHost(navController, startDestination = "home") {
        composable(route = "home") { HomeScreen(navController) }
        composable(route = "second") { SecondScreen(navController) }
    }
}

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(topBar = {TopAppBar(title = {Text("Home Screen") }) }) {
        padding ->
        Column(modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Button(onClick = {navController.navigate(route = "second")}) {
                Text(text = "Click me")
            }
        }
    }
}

@Composable
fun SecondScreen(navController: NavController, modifier: Modifier = Modifier) {
    Scaffold(topBar = {TopAppBar(title = {Text("Second Screen") }) }) {
        padding ->
        Column(modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Button(onClick = {navController.navigate(route = "home")}) {
                Text(text = "Click me again")
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ScreenPreview() {
    TastyBookTheme {
        AppNavigation()
    }
}