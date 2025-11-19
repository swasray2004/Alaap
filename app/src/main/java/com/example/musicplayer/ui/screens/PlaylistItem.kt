import android.R
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.musicplayer.data.model.Playlist
import androidx.palette.graphics.Palette

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var dominantColor by remember { mutableStateOf(Color.DarkGray) }
    var coverUri by remember { mutableStateOf<Uri?>(null) }

    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            coverUri = it
        }
    }

    // Load dominant color from image
    LaunchedEffect(coverUri ?: playlist.coverArtUri) {
        val request = ImageRequest.Builder(context)
            .data(coverUri ?: playlist.coverArtUri ?: "https://via.placeholder.com/160")
            .allowHardware(false)
            .build()

        val result = coil.ImageLoader(context).execute(request)
        if (result is SuccessResult) {
            val bitmap = result.drawable.toBitmap()
            Palette.from(bitmap).generate { palette ->
                palette?.dominantSwatch?.rgb?.let {
                    dominantColor = Color(it)
                }
            }
        }
    }

    Card(
        modifier = modifier
            .width(160.dp)
            .height(120.dp)
            .clickable(onClick = onClick)
            .animateContentSize(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(80.dp)
                    .fillMaxWidth()
            ) {
                AsyncImage(
                    model = coverUri ?: playlist.coverArtUri ?: "https://via.placeholder.com/160",
                    contentDescription = playlist.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = onClick,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Change Image",
                        tint = Color.Black
                    )
                }
            }

            Column(
                modifier = Modifier
                    .background(color=Color.Black)
                    .padding(8.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = playlist.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


