package com.example.musicplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.data.model.SongCategory
import com.example.musicplayer.ui.screens.AuthScreen
import com.example.musicplayer.ui.screens.CategoryScreen
import com.example.musicplayer.ui.screens.HomeScreen
import com.example.musicplayer.ui.screens.LastFmSearchScreen
import com.example.musicplayer.ui.screens.PlayerScreen
import com.example.musicplayer.ui.screens.PlaylistDetailScreen
import com.example.musicplayer.ui.screens.PlaylistsScreen
import com.example.musicplayer.ui.screens.ProfileScreen
import com.example.musicplayer.ui.screens.SearchScreen
import com.example.musicplayer.ui.screens.SettingsScreen

import com.example.musicplayer.ui.theme.MusicPlayerTheme
import com.example.musicplayer.ui.viewmodel.AuthViewModel
import com.example.musicplayer.ui.viewmodel.MusicViewModel
import com.example.musicplayer.viewmodel.LastFmViewModel
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

    // Notification permission launcher
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(
                this,
                "Notification permission is required for music controls",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)
        requestPermissions()
        // Add this line

        window.statusBarColor = android.graphics.Color.TRANSPARENT


        setContent {
            MusicPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NotificationPermissionDialog(
                        onPermissionRequested = {
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                        showPermissionDialog = { shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) }
                    )
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
    fun NotificationPermissionDialog(onPermissionRequested: () -> Unit, showPermissionDialog : () -> Boolean) {
        var showDialog by remember { mutableStateOf(false) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    androidx.compose.ui.platform.LocalContext.current,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (showPermissionDialog()) {
                    showDialog = true
                } else {
                    onPermissionRequested()
                }
            }
        }
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Notification Permission Needed") },
                text = { Text("This permission is required to show music playback controls in the notification area.") },
                confirmButton = {
                    Button(onClick = {
                        onPermissionRequested()
                        showDialog = false
                    }) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    Button(onClick = { showDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
        @Composable
    fun AppNavigation(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val musicViewModel: MusicViewModel = hiltViewModel()
    val currentUser by authViewModel.currentUser.collectAsState()
    val lastFmViewModel: LastFmViewModel = hiltViewModel()


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
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("auth") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToPlayer = { navController.navigate("player") },
                onNavigateToPlaylist = { playlistId ->
                    navController.navigate("playlist/$playlistId")
                },
                navController = navController,
                onNavigateToProfile = { navController.navigate("profile") }
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

                musicViewModel = musicViewModel
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
        composable("lastfm_screen") {
            LastFmSearchScreen(
                onNavigateUp = { navController.navigateUp() },
                lastFmViewModel = lastFmViewModel,
                onNavigateToPlayer = { navController.navigate("player") },
                musicViewModel = musicViewModel
            )
        }
    }
}
