package com.hi.repx_mobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hi.repx_mobile.data.database.entities.Equipment
import com.hi.repx_mobile.data.database.entities.Exercise
import com.hi.repx_mobile.data.database.entities.MuscleGroups
import com.hi.repx_mobile.viewmodel.RepXViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSearchScreen(
    workoutId: Long,
    viewModel: RepXViewModel,
    onExerciseAdded: () -> Unit,
    onBack: () -> Unit
) {
    val exercises by viewModel.exercises.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var selectedMuscle by remember { mutableStateOf<String?>(null) }
    var showCreateDialog by remember { mutableStateOf(false) }  // NEW: US9

    LaunchedEffect(selectedMuscle) {
        viewModel.filterByMuscle(selectedMuscle)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Exercise") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                // NEW (US9): "+" button to create custom exercise
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Create custom exercise")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search exercises...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Muscle group filter chips
            LazyRow(
                modifier = Modifier.padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedMuscle == null,
                        onClick = { selectedMuscle = null },
                        label = { Text("All") }
                    )
                }
                items(MuscleGroups.all) { muscle ->
                    FilterChip(
                        selected = selectedMuscle == muscle,
                        onClick = {
                            selectedMuscle = if (selectedMuscle == muscle) null else muscle
                        },
                        label = { Text(muscle) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Exercise list
            if (exercises.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.SearchOff,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No exercises found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        // NEW (US9): Link to create custom exercise from empty state
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { showCreateDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Create a custom exercise")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(exercises) { exercise ->
                        ExerciseListItem(
                            exercise = exercise,
                            onClick = {
                                viewModel.addExerciseToWorkout(workoutId, exercise.id) {
                                    onExerciseAdded()
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateExerciseDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { name, muscle, equipment, description ->
                viewModel.createCustomExercise(name, muscle, equipment, description)
                showCreateDialog = false
            }
        )
    }
}

/**
 * Single exercise item in the search results list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListItem(
    exercise: Exercise,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = exercise.name,
                        style = MaterialTheme.typography.titleSmall
                    )
                    if (exercise.isCustom) {
                        Spacer(modifier = Modifier.width(8.dp))
                        SuggestionChip(
                            onClick = {},
                            label = {
                                Text(
                                    "Custom",
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = exercise.primaryMuscle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    exercise.equipment?.let { equip ->
                        Text(
                            text = "· $equip",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            Icon(
                Icons.Default.Add,
                contentDescription = "Add to workout",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * US9: Dialog for creating a custom exercise.
 * Uses ExposedDropdownMenuBox for muscle group and equipment selection.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExerciseDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, muscle: String, equipment: String?, description: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedMuscle by remember { mutableStateOf(MuscleGroups.all.first()) }
    var selectedEquipment by remember { mutableStateOf<String?>(null) }
    var description by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }

    // Dropdown expanded states
    var expandedMuscle by remember { mutableStateOf(false) }
    var expandedEquipment by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Custom Exercise") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Exercise name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        nameError = false
                    },
                    label = { Text("Exercise Name") },
                    placeholder = { Text("e.g., Band Pull-Apart") },
                    isError = nameError,
                    supportingText = if (nameError) {
                        { Text("Name is required") }
                    } else null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Muscle group dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedMuscle,
                    onExpandedChange = { expandedMuscle = it }
                ) {
                    OutlinedTextField(
                        value = selectedMuscle,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Muscle Group") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedMuscle) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedMuscle,
                        onDismissRequest = { expandedMuscle = false }
                    ) {
                        MuscleGroups.all.forEach { muscle ->
                            DropdownMenuItem(
                                text = { Text(muscle) },
                                onClick = {
                                    selectedMuscle = muscle
                                    expandedMuscle = false
                                }
                            )
                        }
                    }
                }

                // Equipment dropdown
                ExposedDropdownMenuBox(
                    expanded = expandedEquipment,
                    onExpandedChange = { expandedEquipment = it }
                ) {
                    OutlinedTextField(
                        value = selectedEquipment ?: "None",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Equipment (optional)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEquipment) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedEquipment,
                        onDismissRequest = { expandedEquipment = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("None") },
                            onClick = {
                                selectedEquipment = null
                                expandedEquipment = false
                            }
                        )
                        Equipment.all.forEach { equip ->
                            DropdownMenuItem(
                                text = { Text(equip) },
                                onClick = {
                                    selectedEquipment = equip
                                    expandedEquipment = false
                                }
                            )
                        }
                    }
                }

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                    } else {
                        onCreate(
                            name.trim(),
                            selectedMuscle,
                            selectedEquipment,
                            description.takeIf { it.isNotBlank() }?.trim()
                        )
                    }
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}