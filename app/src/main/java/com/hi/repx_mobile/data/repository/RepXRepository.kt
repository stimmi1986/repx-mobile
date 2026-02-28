package com.hi.repx_mobile.data.repository

import com.hi.repx_mobile.data.database.dao.*
import com.hi.repx_mobile.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import java.security.MessageDigest

class RepXRepository(
    private val userDao: UserDao,
    private val exerciseDao: ExerciseDao,
    private val workoutDao: WorkoutDao,
) {
    // User Operations
    suspend fun registerUser(email: String, password: String, displayName: String): Result<Long> {
        return try {
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                Result.failure(Exception("Email already registered"))
            } else {
                val passwordHash = hashPassword(password)
                val user = User(email = email, passwordHash = passwordHash, displayName = displayName)
                val userId = userDao.insert(user)
                Result.success(userId)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loginUser(email: String, password: String): Result<User> {
        return try {
            val user = userDao.getUserByEmail(email)
            if (user == null) {
                Result.failure(Exception("User not found"))
            } else if (user.passwordHash != hashPassword(password)) {
                Result.failure(Exception("Invalid password"))
            } else {
                Result.success(user)
            }
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
}
