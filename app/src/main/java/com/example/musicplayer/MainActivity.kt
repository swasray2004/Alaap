package com.example.musicplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicplayer.data.model.SongCategory
import com.example.musicplayer.ui.screens.AuthScreen
import com.example.musicplayer.ui.screens.CategoryScreen
import com.example.musicplayer.ui.screens.HomeScreen
import com.example.musicplayer.ui.screens.PlayerScreen
import com.example.musicplayer.ui.screens.PlaylistDetailScreen
import com.example.musicplayer.ui.screens.PlaylistsScreen
import com.example.musicplayer.ui.screens.ProfileScreen
import com.example.musicplayer.ui.screens.SearchScreen
import com.example.musicplayer.ui.screens.SettingsScreen
import com.example.musicplayer.ui.theme.MusicPlayerTheme
import com.example.musicplayer.ui.viewmodel.AuthViewModel
import com.example.musicplayer.ui.viewmodel.MusicViewModel
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.entries.all { it.value }
        if (granted) {
            // Permissions granted, load songs
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        requestPermissions()

        setContent {
            MusicPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController)
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.READ_MEDIA_AUDIO)
        } else {
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        requestPermissionLauncher.launch(permissions)
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val musicViewModel: MusicViewModel = hiltViewModel()

    val currentUser by authViewModel.currentUser.collectAsState()



    val isPlayerScreenVisible by remember {
        derivedStateOf {
            navController.currentDestination?.route == "player"
        }
    }
    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) "home" else "auth"
    ) {
        composable("auth") {
            AuthScreen(
                onNavigateToHome = { navController.navigate("home") {
                    popUpTo("auth") { inclusive = true }
                }}
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToPlayer = { navController.navigate("player") },
                onNavigateToPlaylist = { playlistId ->
                    navController.navigate("playlist/$playlistId")
                },
                navController = navController
            )
        }

        composable("playlists") {
            PlaylistsScreen(
                onNavigateToPlaylist = { playlistId ->
                    navController.navigate("playlist/$playlistId")
                },
                onNavigateToPlayer = { navController.navigate("player") }
            )
        }

        composable("player") {
            PlayerScreen(
                onNavigateUp = { navController.popBackStack() }
            )
        }

        composable("playlist/{playlistId}") { backStackEntry ->
            val playlistId = backStackEntry.arguments?.getString("playlistId")?.toLongOrNull() ?: 0L
            PlaylistDetailScreen(
                playlistId = playlistId,
                onNavigateUp = { navController.navigateUp() },
                onNavigateToPlayer = { navController.navigate("player") }
            )
        }

        composable("profile") {
            ProfileScreen(
                onNavigateUp = { navController.navigateUp() },
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate("auth") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable("search")
        {
            SearchScreen(
                onNavigateUp = { navController.popBackStack() },

                musicViewModel=musicViewModel
            )
        }

        // Category screens
        composable("category/whatsapp") {
            CategoryScreen(
                category = SongCategory.WHATSAPP_AUDIO,
                categoryTitle = "WhatsApp Audios",
                onNavigateUp = { navController.navigateUp() },
                onNavigateToPlayer = { navController.navigate("player") }
            )
        }

        composable("category/downloaded") {
            CategoryScreen(
                category = SongCategory.DOWNLOADED,
                categoryTitle = "Downloaded Music",
                onNavigateUp = { navController.navigateUp() },
                onNavigateToPlayer = { navController.navigate("player") }
            )
        }

        composable("category/recorded") {
            CategoryScreen(
                category = SongCategory.RECORDED,
                categoryTitle = "Recorded Audios",
                onNavigateUp = { navController.navigateUp() },
                onNavigateToPlayer = { navController.navigate("player") }
            )
        }

        composable("category/liked") {
            CategoryScreen(
                category = SongCategory.OTHER, // We'll use a special case in the CategoryScreen
                categoryTitle = "Liked Songs",
                onNavigateUp = { navController.navigateUp() },
                onNavigateToPlayer = { navController.navigate("player") }
            )
        }
    }
}
