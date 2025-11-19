package com.example.musicplayer.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.musicplayer.R
import com.example.musicplayer.ui.components.BottomNavBar
import com.example.musicplayer.ui.theme.Maroon30

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateUp: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    var darkMode by remember { mutableStateOf(true) }
    var highQualityPlayback by remember { mutableStateOf(false) }
    var downloadOverWifi by remember { mutableStateOf(true) }
    var selectedEqualizer by remember { mutableStateOf("Normal") }

    val equalizerOptions = listOf("Normal", "Pop", "Rock", "Jazz", "Classical", "Custom")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(Maroon30),
            )

        }, bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { padding ->

        // 🌄 Background image
        Image(
            painter = painterResource(id = R.drawable.home),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Appearance",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SettingsSwitchItem(
                title = "Dark Mode",
                description = "Enable dark theme",
                checked = darkMode,
                onCheckedChange = { darkMode = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Playback",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            SettingsSwitchItem(
                title = "High Quality Playback",
                description = "Stream and play music in high quality",
                checked = highQualityPlayback,
                onCheckedChange = { highQualityPlayback = it }
            )

            SettingsSwitchItem(
                title = "Download Over Wi-Fi Only",
                description = "Only download music when connected to Wi-Fi",
                checked = downloadOverWifi,
                onCheckedChange = { downloadOverWifi = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Equalizer Preset",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            equalizerOptions.forEach { option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (option == selectedEqualizer),
                            onClick = { selectedEqualizer = option },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (option == selectedEqualizer),
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
