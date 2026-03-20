package com.hi.repx_mobile.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hi.repx_mobile.data.database.AppDatabase
import com.hi.repx_mobile.data.database.entities.*
import com.hi.repx_mobile.data.repository.RepXRepository
import com.hi.repx_mobile.ui.screens.RoutineExerciseEntry
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class RepXViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = RepXRepository(
        userDao = database.userDao(),
        exerciseDao = database.exerciseDao(),
        workoutDao = database.workoutDao(),
        routineDao = database.routineDao(),
        progressPhotoDao = database.progressPhotoDao(),
        bodyWeightDao = database.bodyWeightDao()
    )

    // User State
    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = _currentUserId.map { it != null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    // Auth state
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Exercise State

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMuscle = MutableStateFlow<String?>(null)
    val selectedMuscle: StateFlow<String?> = _selectedMuscle.asStateFlow()

    val exercises: StateFlow<List<Exercise>> = combine(
        _currentUserId,
        _searchQuery,
        _selectedMuscle
    ) { userId, query, muscle ->
        Triple(userId, query, muscle)
    }.flatMapLatest { (userId, query, muscle) ->
        if (userId == null) {
            flowOf(emptyList())
        } else when {
            query.isNotBlank() -> repository.searchExercises(userId, query)
            muscle != null -> repository.getExercisesByMuscle(userId, muscle)
            else -> repository.getAllExercises(userId)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Workout State

    val workouts: StateFlow<List<Workout>> = _currentUserId.flatMapLatest { userId ->
        if (userId != null) repository.getCompletedWorkouts(userId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val activeWorkout: StateFlow<Workout?> = _currentUserId.flatMapLatest { userId ->
        if (userId != null) repository.getActiveWorkoutFlow(userId)
        else flowOf(null)
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    // Routine State

    val routines: StateFlow<List<Routine>> = _currentUserId.flatMapLatest { userId ->
        if (userId != null) repository.getAllRoutines(userId)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Progress Photo State
    val progressPhotos: StateFlow<List<ProgressPhoto>> = _currentUserId.flatMapLatest { userId ->
        if (userId != null) repository.getAllProgressPhotos(userId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Init

    init {
        viewModelScope.launch {
            repository.ensureDefaultExercises()
        }
    }

    // Auth Methods
    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            repository.registerUser(email, password, displayName)
                .onSuccess { userId ->
                    _currentUserId.value = userId
                    loadCurrentUser()
                }
                .onFailure { error ->
                    _authError.value = error.message
                }

            _isLoading.value = false
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            repository.loginUser(email, password)
                .onSuccess { user ->
                    _currentUserId.value = user.id
                    _currentUser.value = user
                }
                .onFailure { error ->
                    _authError.value = error.message
                }

            _isLoading.value = false
        }
    }

    fun logout() {
        _currentUserId.value = null
        _currentUser.value = null
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                repository.deleteUser(userId)
                logout()
            }
        }
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                repository.getUserFlow(userId).collect { user ->
                    _currentUser.value = user
                }
            }
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }

    // Exercise Methods
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun filterByMuscle(muscle: String?) {
        _selectedMuscle.value = muscle
    }

    suspend fun getExerciseById(exerciseId: Long): Exercise? {
        return repository.getExerciseById(exerciseId)
    }

    fun addExerciseToWorkout(workoutId: Long, exerciseId: Long, onAdded: () -> Unit) {
        viewModelScope.launch {
            repository.addExerciseToWorkout(workoutId, exerciseId)
            onAdded()
        }
    }

    fun createCustomExercise(
        name: String,
        primaryMuscle: String,
        equipment: String?,
        description: String?
    ) {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                repository.createCustomExercise(userId, name, primaryMuscle, equipment, description)
            }
        }
    }

    // Workout Methods
    fun startNewWorkout(title: String? = null, onStarted: (Long) -> Unit) {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                val workoutId = repository.startWorkout(userId, title)
                onStarted(workoutId)
            }
        }
    }

    suspend fun getWorkoutById(workoutId: Long): Workout? {
        return repository.getWorkoutById(workoutId)
    }

    suspend fun getWorkoutExercisesList(workoutId: Long): List<WorkoutExercise> {
        return repository.getWorkoutExercisesList(workoutId)
    }

    fun getWorkoutExercises(workoutId: Long): Flow<List<WorkoutExercise>> {
        return repository.getWorkoutExercises(workoutId)
    }

    fun getWorkoutSets(workoutExerciseId: Long): Flow<List<WorkoutSet>> {
        return repository.getWorkoutSets(workoutExerciseId)
    }

    fun copyWorkout(workoutId: Long, onCopied: (Long) -> Unit) {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                val newWorkoutId = repository.copyWorkout(workoutId, userId)
                if (newWorkoutId > 0) {
                    onCopied(newWorkoutId)
                }
            }
        }
    }

    fun addSetToExercise(workoutExerciseId: Long) {
        viewModelScope.launch {
            repository.addSetToExercise(workoutExerciseId)
        }
    }

    fun updateSet(workoutSet: WorkoutSet) {
        viewModelScope.launch {
            repository.updateSet(workoutSet)
        }
    }

    fun deleteSet(setId: Long) {
        viewModelScope.launch {
            repository.deleteSet(setId)
        }
    }

    fun finishWorkout(workoutId: Long, notes: String? = null) {
        viewModelScope.launch {
            repository.finishWorkout(workoutId, notes)
        }
    }

    // Routine Methods
    fun getRoutineExercises(routineId: Long): Flow<List<RoutineExercise>> {
        return repository.getRoutineExercises(routineId)
    }

    fun createRoutine(
        name: String,
        description: String?,
        exercises: List<RoutineExerciseEntry>
    ) {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                val exerciseData = exercises.map { entry ->
                    entry.exercise.id to Triple(
                        entry.defaultSets,
                        entry.defaultReps,
                        entry.defaultWeight
                    )
                }
                repository.createRoutine(userId, name, description, exerciseData)
            }
        }
    }

    fun deleteRoutine(routineId: Long) {
        viewModelScope.launch {
            repository.deleteRoutine(routineId)
        }
    }

    fun startWorkoutFromRoutine(routineId: Long, onStarted: (Long) -> Unit) {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                val workoutId = repository.startWorkoutFromRoutine(routineId, userId)
                if (workoutId > 0) {
                    onStarted(workoutId)
                }
            }
        }
    }

    // Progress Photo Methods
    fun saveProgressPhoto(filePath: String, note: String?) {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                repository.saveProgressPhoto(userId, filePath, note)
            }
        }
    }

    fun deleteProgressPhoto(photo: ProgressPhoto) {
        viewModelScope.launch { repository.deleteProgressPhoto(photo) }
    }

    // Body Weight State
    val bodyWeights: StateFlow<List<BodyWeight>> = _currentUserId.flatMapLatest { userId ->
        if (userId != null) repository.getAllBodyWeights(userId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Body Weight Methods
    fun logBodyWeight(weight: Float, note: String?) {
        viewModelScope.launch {
            _currentUserId.value?.let { userId ->
                repository.logBodyWeight(userId, weight, note)
            }
        }
    }

    fun deleteBodyWeight(bodyWeight: BodyWeight) {
        viewModelScope.launch { repository.deleteBodyWeight(bodyWeight) }
    }
}