package com.linhhoacao.tastybook.ui.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.linhhoacao.tastybook.R
import com.linhhoacao.tastybook.presenter.RecipeDetailPresenter
import com.linhhoacao.tastybook.ui.common.CustomBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    presenter: RecipeDetailPresenter,
    recipeId: String,
    navigateBack: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToAdd: () -> Unit,
    navigateToAllRecipes: () -> Unit,
    navigateToHome: () -> Unit
) {
    val recipeState by presenter.recipeState.collectAsState()
    val recipe = recipeState.recipe
    val isLoading = recipeState.isLoading

    LaunchedEffect(recipeId) {
        presenter.loadRecipe(recipeId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = navigateBack,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                title = {
                    Text(
                        text = "Detail recipes",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        bottomBar = {
            CustomBottomNavigation(
                navigateToHome = { navigateToHome() },
                navigateToSearch = { navigateToSearch() },
                navigateToBook = { navigateToAllRecipes() },
                navigateToAdd = { navigateToAdd() },
                navigateToProfile = { navigateToProfile() },
                tab = -1
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (recipe != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = recipe.imageUrl,
                        error = painterResource(id = R.drawable.dice_3)
                    ),
                    contentDescription = recipe.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = recipe.name,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.tumer),
                                contentDescription = "Preparation Time",
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${recipe.preparationTime} min",
                                fontSize = 14.sp
                            )
                        }
                    }

                    Text(
                        text = recipe.category,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Ingredient",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    recipe.ingredients.forEach { ingredient ->
                        Row(
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = "• ",
                                fontSize = 14.sp
                            )
                            Text(
                                text = ingredient,
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "How to make",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    recipe.instructions.forEachIndexed { index, instruction ->
                        Text(
                            text = instruction,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Recipe not found")
            }
        }
    }
}