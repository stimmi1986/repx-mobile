package com.hi.repx_mobile.data.repository

import com.hi.repx_mobile.data.database.dao.*
import com.hi.repx_mobile.data.database.entities.*
import com.hi.repx_mobile.data.network.ApiClient
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest
import java.io.File

class RepXRepository(
    private val userDao: UserDao,
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao,
    private val routineDao: RoutineDao,
    private val progressPhotoDao: ProgressPhotoDao,
    private val bodyWeightDao: BodyWeightDao
) {
    // User Operations

    suspend fun registerUser(email: String, password: String, displayName: String): Result<Long> {
        return try {
            val response = ApiClient.authService.register(
                com.hi.repx_mobile.data.network.RegisterRequest(email, password, displayName)
            )
            ApiClient.saveToken(response.token)
            val user = User(
                id = response.user.id,
                email = response.user.email,
                displayName = response.user.displayName,
                passwordHash = ""
            )
            userDao.insert(user)
            Result.success(response.user.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val response = ApiClient.authService.login(
                com.hi.repx_mobile.data.network.LoginRequest(email, password)
            )
            ApiClient.saveToken(response.token)
            val user = User(
                id = response.user.id,
                email = response.user.email,
                displayName = response.user.displayName,
                passwordHash = ""
            )
            userDao.insert(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(userId: Long) {
        userDao.deleteById(userId)
    }

    fun getUserFlow(userId: Long): Flow<User?> = userDao.getUserByIdFlow(userId)

    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    // Exercise Operations

    fun getAllExercises(userId: Long): Flow<List<Exercise>> = exerciseDao.getAllExercises(userId)

    fun searchExercises(userId: Long, query: String): Flow<List<Exercise>> =
        exerciseDao.searchExercises(userId, query)

    fun getExercisesByMuscle(userId: Long, muscle: String): Flow<List<Exercise>> =
        exerciseDao.getExercisesByMuscle(userId, muscle)

    suspend fun getExerciseById(exerciseId: Long): Exercise? = exerciseDao.getExerciseById(exerciseId)

    fun getCustomExercises(userId: Long): Flow<List<Exercise>> = exerciseDao.getCustomExercises(userId)

    suspend fun createCustomExercise(
        userId: Long,
        name: String,
        primaryMuscle: String,
        equipment: String?,
        description: String?
    ): Long {
        val exercise = Exercise(
            name = name,
            primaryMuscle = primaryMuscle,
            equipment = equipment,
            description = description,
            isCustom = true,
            userId = userId
        )
        return exerciseDao.insert(exercise)
    }

    suspend fun deleteExercise(exercise: Exercise) = exerciseDao.delete(exercise)

    suspend fun ensureDefaultExercises() {
        if (exerciseDao.getDefaultExerciseCount() == 0) {
            exerciseDao.insertAll(com.hi.repx_mobile.data.database.DefaultExercises.exercises)
        }
    }

    // Workout Operations

    fun getAllWorkouts(userId: Long): Flow<List<Workout>> = workoutDao.getAllWorkouts(userId)

    fun getCompletedWorkouts(userId: Long): Flow<List<Workout>> = workoutDao.getCompletedWorkouts(userId)

    suspend fun getActiveWorkout(userId: Long): Workout? = workoutDao.getActiveWorkout(userId)

    fun getActiveWorkoutFlow(userId: Long): Flow<Workout?> = workoutDao.getActiveWorkoutFlow(userId)

    suspend fun getWorkoutById(workoutId: Long): Workout? = workoutDao.getWorkoutById(workoutId)

    fun getWorkoutsByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<Workout>> =
        workoutDao.getWorkoutsByDateRange(userId, startDate, endDate)

    suspend fun startWorkout(userId: Long, title: String? = null): Long {
        val workout = Workout(userId = userId, title = title)
        return workoutDao.insertWorkout(workout)
    }

    suspend fun finishWorkout(workoutId: Long, notes: String? = null) {
        val workout = workoutDao.getWorkoutById(workoutId)
        workout?.let {
            workoutDao.updateWorkout(it.copy(
                isCompleted = true,
                endTime = System.currentTimeMillis(),
                notes = notes
            ))
        }
    }

    suspend fun updateWorkoutNotes(workoutId: Long, notes: String) {
        val workout = workoutDao.getWorkoutById(workoutId)
        workout?.let {
            workoutDao.updateWorkout(it.copy(notes = notes))
        }
    }

    suspend fun deleteWorkout(workout: Workout) = workoutDao.deleteWorkout(workout)

    // WorkoutExercise operations
    fun getWorkoutExercises(workoutId: Long): Flow<List<WorkoutExercise>> =
        workoutDao.getWorkoutExercises(workoutId)

    suspend fun getWorkoutExercisesList(workoutId: Long): List<WorkoutExercise> =
        workoutDao.getWorkoutExercisesList(workoutId)

    suspend fun addExerciseToWorkout(workoutId: Long, exerciseId: Long): Long {
        val exercises = workoutDao.getWorkoutExercisesList(workoutId)
        val workoutExercise = WorkoutExercise(
            workoutId = workoutId,
            exerciseId = exerciseId,
            orderIndex = exercises.size
        )
        return workoutDao.insertWorkoutExercise(workoutExercise)
    }

    suspend fun removeExerciseFromWorkout(workoutExercise: WorkoutExercise) =
        workoutDao.deleteWorkoutExercise(workoutExercise)

    // WorkoutSet operations
    fun getWorkoutSets(workoutExerciseId: Long): Flow<List<WorkoutSet>> =
        workoutDao.getWorkoutSets(workoutExerciseId)

    suspend fun getWorkoutSetsList(workoutExerciseId: Long): List<WorkoutSet> =
        workoutDao.getWorkoutSetsList(workoutExerciseId)

    suspend fun addSetToExercise(workoutExerciseId: Long): Long {
        val sets = workoutDao.getWorkoutSetsList(workoutExerciseId)
        val workoutSet = WorkoutSet(
            workoutExerciseId = workoutExerciseId,
            setIndex = sets.size
        )
        return workoutDao.insertWorkoutSet(workoutSet)
    }

    suspend fun updateSet(workoutSet: WorkoutSet) = workoutDao.updateWorkoutSet(workoutSet)

    suspend fun deleteSet(setId: Long) = workoutDao.deleteWorkoutSetById(setId)

    // Copy workout
    suspend fun copyWorkout(originalWorkoutId: Long, userId: Long): Long {
        val originalWorkout = workoutDao.getWorkoutById(originalWorkoutId) ?: return -1
        val newWorkoutId = workoutDao.insertWorkout(
            Workout(userId = userId, title = originalWorkout.title)
        )

        val originalExercises = workoutDao.getWorkoutExercisesList(originalWorkoutId)
        for (exercise in originalExercises) {
            val newExerciseId = workoutDao.insertWorkoutExercise(
                WorkoutExercise(
                    workoutId = newWorkoutId,
                    exerciseId = exercise.exerciseId,
                    orderIndex = exercise.orderIndex
                )
            )

            val originalSets = workoutDao.getWorkoutSetsList(exercise.id)
            for (set in originalSets) {
                workoutDao.insertWorkoutSet(
                    WorkoutSet(
                        workoutExerciseId = newExerciseId,
                        setIndex = set.setIndex,
                        reps = set.reps,
                        weight = set.weight
                    )
                )
            }
        }

        return newWorkoutId
    }

    // Routine Operations

    fun getAllRoutines(userId: Long): Flow<List<Routine>> = routineDao.getAllRoutines(userId)

    suspend fun getRoutineById(routineId: Long): Routine? = routineDao.getRoutineById(routineId)

    fun getRoutineExercises(routineId: Long): Flow<List<RoutineExercise>> =
        routineDao.getRoutineExercises(routineId)

    suspend fun getRoutineExercisesList(routineId: Long): List<RoutineExercise> =
        routineDao.getRoutineExercisesList(routineId)

    suspend fun createRoutine(
        userId: Long,
        name: String,
        description: String?,
        exercises: List<Pair<Long, Triple<Int, Int?, Float?>>>
    ): Long {
        val routine = Routine(userId = userId, name = name, description = description)
        val routineId = routineDao.insertRoutine(routine)

        val routineExercises = exercises.mapIndexed { index, (exerciseId, defaults) ->
            RoutineExercise(
                routineId = routineId,
                exerciseId = exerciseId,
                orderIndex = index,
                defaultSets = defaults.first,
                defaultReps = defaults.second,
                defaultWeight = defaults.third
            )
        }
        routineDao.insertRoutineExercises(routineExercises)

        return routineId
    }

    suspend fun deleteRoutine(routineId: Long) = routineDao.deleteRoutineById(routineId)

    suspend fun startWorkoutFromRoutine(routineId: Long, userId: Long): Long {
        val routine = routineDao.getRoutineById(routineId) ?: return -1
        val routineExercises = routineDao.getRoutineExercisesList(routineId)

        val workoutId = workoutDao.insertWorkout(
            Workout(userId = userId, title = routine.name)
        )

        for (routineExercise in routineExercises) {
            val workoutExerciseId = workoutDao.insertWorkoutExercise(
                WorkoutExercise(
                    workoutId = workoutId,
                    exerciseId = routineExercise.exerciseId,
                    orderIndex = routineExercise.orderIndex
                )
            )

            for (setIndex in 0 until routineExercise.defaultSets) {
                workoutDao.insertWorkoutSet(
                    WorkoutSet(
                        workoutExerciseId = workoutExerciseId,
                        setIndex = setIndex,
                        reps = routineExercise.defaultReps,
                        weight = routineExercise.defaultWeight
                    )
                )
            }
        }

        return workoutId
    }

    fun getAllProgressPhotos(userId: Long): Flow<List<ProgressPhoto>> =
        progressPhotoDao.getAllPhotos(userId)

    suspend fun saveProgressPhoto(userId: Long, filePath: String, note: String?): Long {
        val photo = ProgressPhoto(userId = userId, filePath = filePath, note = note)
        return progressPhotoDao.insertPhoto(photo)
    }

    suspend fun deleteProgressPhoto(photo: ProgressPhoto) {
        val file = File(photo.filePath)
        if (file.exists()) file.delete()
        progressPhotoDao.deletePhoto(photo)
    }

    suspend fun updatePhotoNote(photo: ProgressPhoto, note: String?) {
        progressPhotoDao.updatePhoto(photo.copy(note = note))
    }

    // Body Weight Operations

    fun getAllBodyWeights(userId: Long): Flow<List<BodyWeight>> =
        bodyWeightDao.getAllWeights(userId)

    suspend fun logBodyWeight(userId: Long, weight: Float, note: String?): Long {
        val entry = BodyWeight(userId = userId, weight = weight, note = note)
        return bodyWeightDao.insert(entry)
    }

    suspend fun deleteBodyWeight(bodyWeight: BodyWeight) =
        bodyWeightDao.delete(bodyWeight)
}
