package com.hi.repx_mobile.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.hi.repx_mobile.data.database.entities.ProgressPhoto
import com.hi.repx_mobile.viewmodel.RepXViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressPhotosScreen(
    viewModel: RepXViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val photos by viewModel.progressPhotos.collectAsState()

    var pendingPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var pendingPhotoPath by remember { mutableStateOf<String?>(null) }

    var selectedPhoto by remember { mutableStateOf<ProgressPhoto?>(null) }

    var showNoteDialog by remember { mutableStateOf(false) }
    var noteForPhotoPath by remember { mutableStateOf<String?>(null) }
    var noteText by remember { mutableStateOf("") }

    var photoToDelete by remember { mutableStateOf<ProgressPhoto?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success && pendingPhotoPath != null) {
            noteForPhotoPath = pendingPhotoPath
            noteText = ""
            showNoteDialog = true
        }
        pendingPhotoUri = null
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) {
            launchCamera(context, cameraLauncher) { uri, path ->
                pendingPhotoUri = uri
                pendingPhotoPath = path
            }
        }
    }

    fun onTakePhoto() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            launchCamera(context, cameraLauncher) { uri, path ->
                pendingPhotoUri = uri
                pendingPhotoPath = path
            }
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Framfaramyndir") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Til baka")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onTakePhoto() },
                icon = { Icon(Icons.Default.CameraAlt, contentDescription = null) },
                text = { Text("Taka mynd") }
            )
        }
    ) { paddingValues ->
        if (photos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Engar framfaramyndir",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Taktu mynd til að fylgjast með framförum",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(photos) { photo ->
                    PhotoGridItem(
                        photo = photo,
                        onClick = { selectedPhoto = photo },
                        onDelete = { photoToDelete = photo }
                    )
                }
            }
        }
    }

    if (showNoteDialog && noteForPhotoPath != null) {
        AlertDialog(
            onDismissRequest = {
                // Save without note
                viewModel.saveProgressPhoto(noteForPhotoPath!!, null)
                showNoteDialog = false
                noteForPhotoPath = null
            },
            title = { Text("Bæta við athugasemd?") },
            text = {
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    placeholder = { Text("t.d. Vika 4 — Framan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.saveProgressPhoto(
                        noteForPhotoPath!!,
                        noteText.takeIf { it.isNotBlank() }
                    )
                    showNoteDialog = false
                    noteForPhotoPath = null
                }) {
                    Text("Vista")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.saveProgressPhoto(noteForPhotoPath!!, null)
                    showNoteDialog = false
                    noteForPhotoPath = null
                }) {
                    Text("Sleppa")
                }
            }
        )
    }

    if (selectedPhoto != null) {
        FullScreenPhotoViewer(
            photo = selectedPhoto!!,
            onDismiss = { selectedPhoto = null },
            onDelete = {
                photoToDelete = selectedPhoto
                selectedPhoto = null
            }
        )
    }

    if (photoToDelete != null) {
        AlertDialog(
            onDismissRequest = { photoToDelete = null },
            title = { Text("Eyða mynd?") },
            text = { Text("Ertu viss um að þú viljir eyða þessari mynd? Þetta er ekki hægt að afturkalla.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteProgressPhoto(photoToDelete!!)
                    photoToDelete = null
                }) {
                    Text("Eyða", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { photoToDelete = null }) {
                    Text("Hætta við")
                }
            }
        )
    }
}


@Composable
fun PhotoGridItem(
    photo: ProgressPhoto,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("d. MMM yyyy", Locale("is")) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            // The photo — loaded from file path using Coil
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(File(photo.filePath))
                    .crossfade(true)
                    .build(),
                contentDescription = "Framfaramynd",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )

            // Info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = dateFormat.format(Date(photo.takenAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    photo.note?.let { note ->
                        Text(
                            text = note,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                }
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eyða",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
fun FullScreenPhotoViewer(
    photo: ProgressPhoto,
    onDismiss: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("d. MMMM yyyy · HH:mm", Locale("is")) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.9f))
            .clickable { onDismiss() }
    ) {
        // Close button
        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Close, contentDescription = "Loka", tint = MaterialTheme.colorScheme.inverseOnSurface)
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Eyða", tint = MaterialTheme.colorScheme.error)
        }

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(File(photo.filePath))
                .crossfade(true)
                .build(),
            contentDescription = "Framfaramynd",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 60.dp)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dateFormat.format(Date(photo.takenAt)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.inverseOnSurface
            )
            photo.note?.let { note ->
                Text(
                    text = note,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
            }
        }
    }
}

private fun launchCamera(
    context: android.content.Context,
    cameraLauncher: androidx.activity.result.ActivityResultLauncher<Uri>,
    onUriCreated: (Uri, String) -> Unit
) {
    // Create a file in the app's pictures directory
    val photoDir = File(context.filesDir, "progress_photos")
    if (!photoDir.exists()) photoDir.mkdirs()

    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val photoFile = File(photoDir, "IMG_${timestamp}.jpg")

    // Get a content:// URI via FileProvider
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        photoFile
    )

    onUriCreated(uri, photoFile.absolutePath)
    cameraLauncher.launch(uri)
}