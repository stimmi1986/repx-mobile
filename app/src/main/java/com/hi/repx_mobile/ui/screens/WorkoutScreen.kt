package com.hi.repx_mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hi.repx_mobile.data.database.entities.Exercise
import com.hi.repx_mobile.data.database.entities.WorkoutSet
import com.hi.repx_mobile.data.database.entities.WorkoutExercise


import com.hi.repx_mobile.viewmodel.RepXViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutScreen(
    workoutId: Long,
    viewModel: RepXViewModel,
    onAddExercise: () -> Unit,
    onFinishWorkout: () -> Unit
) {
    val workoutExercises by viewModel.getWorkoutExercises(workoutId).collectAsState(initial = emptyList())
    var showFinishDialog by remember { mutableStateOf(false) }
    var workoutNotes by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Workout") },
                actions = {
                    TextButton(
                        onClick = { showFinishDialog = true }
                    ) {
                        Text("Finish")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddExercise) {
                Icon(Icons.Default.Add, contentDescription = "Add Exercise")
            }
        }
    ) { paddingValues ->
        if (workoutExercises.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No exercises yet",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap + to add an exercise",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                items(workoutExercises) { workoutExercise ->
                    WorkoutExerciseCard(
                        workoutExercise = workoutExercise,
                        viewModel = viewModel
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showFinishDialog) {
        AlertDialog(
            onDismissRequest = { showFinishDialog = false },
            title = { Text("Finish Workout?") },
            text = {
                Column {
                    Text("Add any notes about this workout:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = workoutNotes,
                        onValueChange = { workoutNotes = it },
                        placeholder = { Text("Notes (optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.finishWorkout(workoutId, workoutNotes.takeIf { it.isNotBlank() })
                        showFinishDialog = false
                        onFinishWorkout()
                    }
                ) {
                    Text("Finish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFinishDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun WorkoutExerciseCard(
    workoutExercise: WorkoutExercise,
    viewModel: RepXViewModel
) {
    val sets by viewModel.getWorkoutSets(workoutExercise.id).collectAsState(initial = emptyList())
    var exercise by remember { mutableStateOf<Exercise?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(workoutExercise.exerciseId) {
        exercise = viewModel.getExerciseById(workoutExercise.exerciseId)
    }

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
                        text = exercise?.name ?: "Loading...",
                        style = MaterialTheme.typography.titleMedium
                    )
                    exercise?.let {
                        Text(
                            text = it.primaryMuscle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sets header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Set",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.width(40.dp)
                )
                Text(
                    text = "Weight (kg)",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Reps",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sets
            sets.forEachIndexed { index, set ->
                SetRow(
                    setIndex = index + 1,
                    set = set,
                    onUpdate = { updatedSet ->
                        viewModel.updateSet(updatedSet)
                    },
                    onDelete = {
                        viewModel.deleteSet(set.id)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Add set button
            TextButton(
                onClick = { viewModel.addSetToExercise(workoutExercise.id) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Set")
            }
        }
    }
}

@Composable
fun SetRow(
    setIndex: Int,
    set: WorkoutSet,
    onUpdate: (WorkoutSet) -> Unit,
    onDelete: () -> Unit
) {
    var weight by remember(set.id) { mutableStateOf(set.weight?.toString() ?: "") }
    var reps by remember(set.id) { mutableStateOf(set.reps?.toString() ?: "") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = setIndex.toString(),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(40.dp)
        )

        OutlinedTextField(
            value = weight,
            onValueChange = { newValue ->
                weight = newValue
                val weightFloat = newValue.toFloatOrNull()
                onUpdate(set.copy(weight = weightFloat))
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium
        )

        OutlinedTextField(
            value = reps,
            onValueChange = { newValue ->
                reps = newValue
                val repsInt = newValue.toIntOrNull()
                onUpdate(set.copy(reps = repsInt))
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium
        )

        // Complete checkbox
        Checkbox(
            checked = set.isCompleted,
            onCheckedChange = { checked ->
                onUpdate(set.copy(isCompleted = checked))
            }
        )
    }
}
