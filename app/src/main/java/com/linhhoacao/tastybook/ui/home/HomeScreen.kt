package com.linhhoacao.tastybook.ui.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
import com.linhhoacao.tastybook.presenter.UserPresenter
import com.linhhoacao.tastybook.ui.common.CustomBottomNavigation
import com.linhhoacao.tastybook.ui.common.CustomSearchTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    presenter: HomePresenter,
    userPresenter: UserPresenter,
    navigateToRecipeDetail: (String) -> Unit,
    navigateToSearch: () -> Unit,
    navigateToSearchWithString: (String) -> Unit,
    navigateToAddRecipe: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToAllRecipe: () -> Unit
) {
    val userState by userPresenter.userState.collectAsState()
    val currentUser = userState.currentUser
    val newRecipes by presenter.newRecipes.collectAsState()
    val allRecipes by presenter.allRecipes.collectAsState()
    val isLoading by presenter.isLoading.collectAsState()

    LaunchedEffect(key1 = true) {
        userPresenter.loadCurrentUserByEmail()
        presenter.loadRecipes()
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                navigateToSearch = { navigateToSearch() },
                navigateToAdd = { navigateToAddRecipe() },
                navigateToBook = { navigateToAllRecipe() },
                navigateToProfile = { navigateToProfile() },
                tab = 0
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello ${currentUser?.name}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "What are you cooking today?",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFCC80))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.user_alt),
                            contentDescription = "Profile",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    var searchText by remember { mutableStateOf("") }

                    CustomSearchTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        onSearchTextChange = { },
                        navigateToSearch = { query ->
                            navigateToSearchWithString(query)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.setting),
                            contentDescription = "Filter",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "New recipes",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))
                if (newRecipes.isEmpty() && !isLoading) {
                    Text(
                        text = "Không có công thức mới",
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = Color.Gray
                    )
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(end = 8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(newRecipes.take(5)) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = { navigateToRecipeDetail(recipe.id) },
                                onDeleteClick = { presenter.deleteRecipe(recipe.id) },
                                modifier = Modifier.width(180.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Popular",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (allRecipes.isEmpty() && !isLoading) {
                    Text(
                        text = "Không có công thức phổ biến",
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
            .size(150.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    showDeleteDialog = true
                }
            ),
        shape = RoundedCornerShape(12.dp),
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
                    .height(90.dp)
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
                    fontSize = 13.sp,
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
                title = { Text("Xóa công thức") },
                text = { Text("Bạn có chắc chắn muốn xóa công thức này?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDeleteClick()
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Xóa")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDeleteDialog = false }
                    ) {
                        Text("Hủy")
                    }
                }
            )
        }
    }
}