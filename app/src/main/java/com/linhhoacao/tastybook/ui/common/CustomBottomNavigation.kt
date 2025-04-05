package com.linhhoacao.tastybook.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.linhhoacao.tastybook.R

@Composable
fun CustomBottomNavigation(
    navigateToHome: () -> Unit = {},
    navigateToSearch: () -> Unit = {},
    navigateToBook: () -> Unit = {},
    navigateToProfile: () -> Unit = {},
    navigateToAdd: () -> Unit = {},
    tab: Int
) {
    var selectedTab by remember { mutableIntStateOf(tab) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color(0xFFF0F0F0))
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavigationIcon(
                drawableResId = R.drawable.home_light,
                isSelected = selectedTab == 0,
                onClick = {
                    selectedTab = 0
                    navigateToHome()
                }
            )

            NavigationIcon(
                drawableResId = R.drawable.search,
                isSelected = selectedTab == 1,
                onClick = {
                    selectedTab = 1
                    navigateToSearch()
                }
            )

            Spacer(modifier = Modifier.width(48.dp))

            NavigationIcon(
                drawableResId = R.drawable.book_open_light,
                isSelected = selectedTab == 2,
                onClick = {
                    selectedTab = 2
                    navigateToBook()
                }
            )

            NavigationIcon(
                drawableResId = R.drawable.user_alt,
                isSelected = selectedTab == 3,
                onClick = {
                    selectedTab = 3
                    navigateToProfile()
                }
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = ((-26.5)).dp)
                .size(53.dp)
                .clip(CircleShape)
                .border(2.dp, Color.White, CircleShape)
                .background(Color(0xFFFFC107))
                .clickable {
                    navigateToAdd()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.add_round),
                contentDescription = "Thêm mới",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun NavigationIcon(
    drawableResId: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = drawableResId),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .then(
                    if (isSelected)
                        Modifier.alpha(1f)
                    else
                        Modifier.alpha(0.5f)
                )
        )
    }
}