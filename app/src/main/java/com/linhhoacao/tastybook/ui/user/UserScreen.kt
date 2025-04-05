package com.linhhoacao.tastybook.ui.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.linhhoacao.tastybook.R
import com.linhhoacao.tastybook.presenter.UserPresenter
import com.linhhoacao.tastybook.ui.common.CustomBottomNavigation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserScreen(
    userPresenter: UserPresenter,
    navigateToHome: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToSearch: () -> Unit,
    navigateToMyRecipes: () -> Unit,
    navigateToAdd: () -> Unit,
    onSignOut: () -> Unit,
    navigateToAllRecipe: () -> Unit
) {
    LaunchedEffect(Unit) {
        userPresenter.loadCurrentUserByEmail()
    }

    val userState by userPresenter.userState.collectAsState()
    val currentUser = userState.currentUser

    LaunchedEffect(userState.error) {
        userState.error?.let { _ ->
            userPresenter.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = navigateToHome) {
                        Icon(
                            painter = painterResource(R.drawable.back),
                            contentDescription = "Back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onSignOut) {
                        Icon(
                            painter = painterResource(R.drawable.logout),
                            contentDescription = "Logout",
                            tint = Color(0xFFFFC266),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            CustomBottomNavigation(
                navigateToHome = { navigateToHome() },
                navigateToSearch = { navigateToSearch() },
                navigateToAdd = { navigateToAdd() },
                navigateToBook = { navigateToAllRecipe() },
                tab = 3
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
                ) {
                    if (currentUser?.profilePictureUrl != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentUser.profilePictureUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.user_alt),
                                contentDescription = "Profile",
                                tint = Color.Black,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentUser?.name ?: "User Name",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = currentUser?.email ?: "useremail123@gmail.com",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { navigateToEditProfile() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFC266),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .width(120.dp)
                            .height(36.dp)
                    ) {
                        Text(
                            "Edit profile",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 47.dp)
                        .clickable { navigateToMyRecipes() },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFEEEEEE)
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.book),
                            contentDescription = "My Recipes",
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = "My recipes",
                            modifier = Modifier.weight(1f),
                            fontSize = 16.sp
                        )

                        Icon(
                            painter = painterResource(R.drawable.expand_right),
                            contentDescription = "Go to My Recipes",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            if (userState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFFFFC266)
                )
            }
        }
    }
}