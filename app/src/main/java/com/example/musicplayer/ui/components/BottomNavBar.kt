package com.example.musicplayer.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person

import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.musicplayer.ui.theme.Maroon30

@Composable
fun BottomNavBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavItem(
            name = "Home",
            route = "home",
            icon = Icons.Default.Home
        ),
        BottomNavItem(
            name = "Playlists",
            route = "playlists",
            icon = Icons.Default.PlaylistAdd
        ),
        BottomNavItem(
            name = "Search",
            route = "search",
            icon = Icons.Default.Search
        ),
        BottomNavItem(
            name = "Settings",
            route = "settings",
            icon = Icons.Default.Settings
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        contentColor = Color.White
    ) {
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.name,
                    tint = if (currentRoute == item.route) {
                        MaterialTheme.colorScheme.primary // Highlight selected item
                    } else {
                        Color.White.copy(alpha = 0.8f) // Semi-transparent white for unselected
                    }
                )
                       },
                label = { Text(text = item.name) },

                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

data class BottomNavItem(
    val name: String,
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
