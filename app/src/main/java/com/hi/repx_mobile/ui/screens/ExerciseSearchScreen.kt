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
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
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
                            // Toggle: tap same chip again to deselect
                            selectedMuscle = if (selectedMuscle == muscle) null else muscle
                        },
                        label = { Text(muscle) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
}

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
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleSmall
                )
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
