package com.hi.repx_mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hi.repx_mobile.data.database.entities.Exercise
import com.hi.repx_mobile.data.database.entities.Routine
import com.hi.repx_mobile.data.database.entities.RoutineExercise
import com.hi.repx_mobile.viewmodel.RepXViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutineListScreen(
    viewModel: RepXViewModel,
    onCreateRoutine: () -> Unit,
    onStartWorkoutFromRoutine: (Long) -> Unit,
    onBack: () -> Unit
) {
    val routines: List<Routine> by viewModel.routines.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Routines") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCreateRoutine,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("New Routine") }
            )
        }
    ) { paddingValues ->
        if (routines.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No routines yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Create a routine to start workouts faster",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(routines) { routine: Routine ->
                    RoutineCard(
                        routine = routine,
                        viewModel = viewModel,
                        onStart = {
                            viewModel.startWorkoutFromRoutine(routine.id) { workoutId: Long ->
                                onStartWorkoutFromRoutine(workoutId)
                            }
                        },
                        onDelete = { viewModel.deleteRoutine(routine.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun RoutineCard(
    routine: Routine,
    viewModel: RepXViewModel,
    onStart: () -> Unit,
    onDelete: () -> Unit
) {
    val routineExercises: List<RoutineExercise> by viewModel.getRoutineExercises(routine.id)
        .collectAsState(initial = emptyList())
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = routine.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    routine.description?.let { desc ->
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${routineExercises.size} exercises",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete routine",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }

            // Show exercise names preview
            if (routineExercises.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                routineExercises.take(4).forEach { routineExercise: RoutineExercise ->
                    RoutineExercisePreview(
                        routineExercise = routineExercise,
                        viewModel = viewModel
                    )
                }
                if (routineExercises.size > 4) {
                    Text(
                        text = "+${routineExercises.size - 4} more",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onStart,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Start Workout")
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Routine?") },
            text = { Text("Are you sure you want to delete \"${routine.name}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun RoutineExercisePreview(
    routineExercise: RoutineExercise,
    viewModel: RepXViewModel
) {
    var exercise by remember { mutableStateOf<Exercise?>(null) }

    LaunchedEffect(routineExercise.exerciseId) {
        exercise = viewModel.getExerciseById(routineExercise.exerciseId)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "• ${exercise?.name ?: "Loading..."}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "${routineExercise.defaultSets} sets",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
