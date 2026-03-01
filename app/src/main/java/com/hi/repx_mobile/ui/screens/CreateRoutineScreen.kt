package com.hi.repx_mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hi.repx_mobile.data.database.entities.Exercise
import com.hi.repx_mobile.data.database.entities.MuscleGroups
import com.hi.repx_mobile.viewmodel.RepXViewModel

data class RoutineExerciseEntry(
    val exercise: Exercise,
    val defaultSets: Int = 3,
    val defaultReps: Int? = null,
    val defaultWeight: Float? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    viewModel: RepXViewModel,
    onRoutineCreated: () -> Unit,
    onBack: () -> Unit
) {
    var routineName by remember { mutableStateOf("") }
    var routineDescription by remember { mutableStateOf("") }
    var selectedExercises by remember { mutableStateOf<List<RoutineExerciseEntry>>(emptyList()) }
    var showExercisePicker by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create Routine") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (routineName.isBlank()) {
                                nameError = true
                            } else {
                                viewModel.createRoutine(
                                    name = routineName,
                                    description = routineDescription.takeIf { it.isNotBlank() },
                                    exercises = selectedExercises
                                )
                                onRoutineCreated()
                            }
                        },
                        enabled = selectedExercises.isNotEmpty()
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // Routine name
            item {
                OutlinedTextField(
                    value = routineName,
                    onValueChange = {
                        routineName = it
                        nameError = false
                    },
                    label = { Text("Routine Name") },
                    placeholder = { Text("e.g., Push Day, Leg Day") },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Name is required") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Description
            item {
                OutlinedTextField(
                    value = routineDescription,
                    onValueChange = { routineDescription = it },
                    label = { Text("Description (optional)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Exercises header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Exercises (${selectedExercises.size})",
                        style = MaterialTheme.typography.titleMedium
                    )
                    TextButton(onClick = { showExercisePicker = true }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add Exercise")
                    }
                }
            }

            // Empty state
            if (selectedExercises.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tap \"Add Exercise\" to build your routine",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Selected exercises list
            itemsIndexed(selectedExercises) { index, entry ->
                RoutineExerciseEditCard(
                    index = index,
                    entry = entry,
                    onUpdateSets = { newSets ->
                        selectedExercises = selectedExercises.toMutableList().apply {
                            this[index] = entry.copy(defaultSets = newSets)
                        }
                    },
                    onUpdateReps = { newReps ->
                        selectedExercises = selectedExercises.toMutableList().apply {
                            this[index] = entry.copy(defaultReps = newReps)
                        }
                    },
                    onRemove = {
                        selectedExercises = selectedExercises.toMutableList().apply {
                            removeAt(index)
                        }
                    }
                )
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Exercise picker dialog
    if (showExercisePicker) {
        ExercisePickerDialog(
            viewModel = viewModel,
            alreadySelected = selectedExercises.map { it.exercise.id },
            onSelect = { exercise ->
                selectedExercises = selectedExercises + RoutineExerciseEntry(exercise = exercise)
            },
            onDismiss = { showExercisePicker = false }
        )
    }
}

@Composable
fun RoutineExerciseEditCard(
    index: Int,
    entry: RoutineExerciseEntry,
    onUpdateSets: (Int) -> Unit,
    onUpdateReps: (Int?) -> Unit,
    onRemove: () -> Unit
) {
    var setsText by remember(entry) { mutableStateOf(entry.defaultSets.toString()) }
    var repsText by remember(entry) { mutableStateOf(entry.defaultReps?.toString() ?: "") }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${index + 1}. ${entry.exercise.name}",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = entry.exercise.primaryMuscle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = setsText,
                    onValueChange = { newValue ->
                        setsText = newValue
                        newValue.toIntOrNull()?.let { if (it > 0) onUpdateSets(it) }
                    },
                    label = { Text("Sets") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = repsText,
                    onValueChange = { newValue ->
                        repsText = newValue
                        onUpdateReps(newValue.toIntOrNull())
                    },
                    label = { Text("Reps (opt.)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExercisePickerDialog(
    viewModel: RepXViewModel,
    alreadySelected: List<Long>,
    onSelect: (Exercise) -> Unit,
    onDismiss: () -> Unit
) {
    val allExercises by viewModel.exercises.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscle by remember { mutableStateOf<String?>(null) }

    // Filter exercises locally in the dialog
    val filteredExercises = allExercises.filter { exercise ->
        val matchesSearch = searchQuery.isBlank() || exercise.name.contains(searchQuery, ignoreCase = true)
        val matchesMuscle = selectedMuscle == null || exercise.primaryMuscle == selectedMuscle
        val notAlreadySelected = exercise.id !in alreadySelected
        matchesSearch && matchesMuscle && notAlreadySelected
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Exercise") },
        text = {
            Column(modifier = Modifier.heightIn(max = 400.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Muscle filter chips
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    item {
                        FilterChip(
                            selected = selectedMuscle == null,
                            onClick = { selectedMuscle = null },
                            label = { Text("All", style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                    items(MuscleGroups.all) { muscle ->
                        FilterChip(
                            selected = selectedMuscle == muscle,
                            onClick = { selectedMuscle = if (selectedMuscle == muscle) null else muscle },
                            label = { Text(muscle, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(filteredExercises) { exercise ->
                        Card(
                            onClick = {
                                onSelect(exercise)
                                onDismiss()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(exercise.name, style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        exercise.primaryMuscle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done")
            }
        }
    )
}
