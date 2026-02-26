package com.hi.repx_mobile.ui.screens


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hi.repx_mobile.viewmodel.RepXViewModel
import com.hi.repx_mobile.data.database.entities.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutDetailScreen(
    workoutId: Long,
    viewModel: RepXViewModel,
    onCopyWorkout: (Long) -> Unit,
    onBack: () -> Unit
) {
    var workout by remember { mutableStateOf<Workout?>(null) }
    val workoutExercises by viewModel.getWorkoutExercises(workoutId).collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    LaunchedEffect(workoutId) {
        workout = viewModel.getWorkoutById(workoutId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(workout?.title ?: "Workout Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.copyWorkout(workoutId) { newWorkoutId ->
                                onCopyWorkout(newWorkoutId)
                            }
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy Workout")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            workout?.notes?.let { notes ->
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Notes", style = MaterialTheme.typography.titleSmall)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(notes, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            items(workoutExercises) { workoutExercise ->
                WorkoutDetailExerciseCard(workoutExercise = workoutExercise, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun WorkoutDetailExerciseCard(
    workoutExercise: WorkoutExercise,
    viewModel: RepXViewModel
) {
    val sets by viewModel.getWorkoutSets(workoutExercise.id).collectAsState(initial = emptyList())
    var exercise by remember { mutableStateOf<Exercise?>(null) }

    LaunchedEffect(workoutExercise.exerciseId) {
        exercise = viewModel.getExerciseById(workoutExercise.exerciseId)
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = exercise?.name ?: "Loading...",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            sets.forEachIndexed { index, set ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Set ${index + 1}", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${set.weight ?: "-"} kg × ${set.reps ?: "-"} reps",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}