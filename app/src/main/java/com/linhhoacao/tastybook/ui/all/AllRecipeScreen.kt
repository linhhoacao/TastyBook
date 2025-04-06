package com.linhhoacao.tastybook.ui.all

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.linhhoacao.tastybook.R
import com.linhhoacao.tastybook.model.Recipe
import com.linhhoacao.tastybook.presenter.HomePresenter
import com.linhhoacao.tastybook.ui.common.CustomBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllRecipeScreen(
    presenter: HomePresenter,
    navigateToRecipeDetail: (String) -> Unit,
    navigateToSearch: () -> Unit,
    navigateToAddRecipe: () -> Unit,
    onBackClick: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToHome: () -> Unit
) {
    val allRecipes by presenter.allRecipes.collectAsState()
    val isLoading by presenter.isLoading.collectAsState()

    LaunchedEffect(key1 = true) {
        presenter.loadRecipes()
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                navigateToHome = { navigateToHome() },
                navigateToSearch = { navigateToSearch() },
                navigateToAdd = { navigateToAddRecipe() },
                navigateToProfile = { navigateToProfile() },
                tab = 2
            )
        },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
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
                        text = "All recipes",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                if (allRecipes.isEmpty() && !isLoading) {
                    Text(
                        text = "No popular recipes",
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = Color.Gray
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 28.dp)
                    ) {
                        items(allRecipes) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = { navigateToRecipeDetail(recipe.id) },
                                onDeleteClick = { presenter.deleteRecipe(recipe.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RecipeCard(
    recipe: Recipe,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .height(150.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    showDeleteDialog = true
                }
            ),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Image(
                    painter = rememberAsyncImagePainter(
                        model = recipe.imageUrl,
                        error = painterResource(id = R.drawable.dice_3)
                    ),
                    contentDescription = recipe.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = recipe.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = recipe.category,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete recipe") },
                text = { Text("Are you sure you want to delete this recipe?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteClick()
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Delete")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}