package com.hi.repx_mobile.data.database.dao

import androidx.room.*
import com.hi.repx_mobile.data.database.entities.Workout
import com.hi.repx_mobile.data.database.entities.WorkoutExercise
import com.hi.repx_mobile.data.database.entities.WorkoutSet
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {
    // Workout queries
    @Query("SELECT * FROM workouts WHERE userId = :userId ORDER BY startTime DESC")
    fun getAllWorkouts(userId: Long): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE userId = :userId AND isCompleted = 1 ORDER BY startTime DESC")
    fun getCompletedWorkouts(userId: Long): Flow<List<Workout>>

    @Query("SELECT * FROM workouts WHERE userId = :userId AND isCompleted = 0 LIMIT 1")
    suspend fun getActiveWorkout(userId: Long): Workout?

    @Query("SELECT * FROM workouts WHERE userId = :userId AND isCompleted = 0")
    fun getActiveWorkoutFlow(userId: Long): Flow<Workout?>

    @Query("SELECT * FROM workouts WHERE id = :workoutId LIMIT 1")
    suspend fun getWorkoutById(workoutId: Long): Workout?

    @Query("SELECT * FROM workouts WHERE userId = :userId AND startTime BETWEEN :startDate AND :endDate ORDER BY startTime DESC")
    fun getWorkoutsByDateRange(userId: Long, startDate: Long, endDate: Long): Flow<List<Workout>>

    @Insert
    suspend fun insertWorkout(workout: Workout): Long

    @Update
    suspend fun updateWorkout(workout: Workout)

    @Delete
    suspend fun deleteWorkout(workout: Workout)

    // WorkoutExercise queries
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex ASC")
    fun getWorkoutExercises(workoutId: Long): Flow<List<WorkoutExercise>>

    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex ASC")
    suspend fun getWorkoutExercisesList(workoutId: Long): List<WorkoutExercise>

    @Insert
    suspend fun insertWorkoutExercise(workoutExercise: WorkoutExercise): Long

    @Update
    suspend fun updateWorkoutExercise(workoutExercise: WorkoutExercise)

    @Delete
    suspend fun deleteWorkoutExercise(workoutExercise: WorkoutExercise)

    // WorkoutSet queries
    @Query("SELECT * FROM workout_sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY setIndex ASC")
    fun getWorkoutSets(workoutExerciseId: Long): Flow<List<WorkoutSet>>

    @Query("SELECT * FROM workout_sets WHERE workoutExerciseId = :workoutExerciseId ORDER BY setIndex ASC")
    suspend fun getWorkoutSetsList(workoutExerciseId: Long): List<WorkoutSet>

    @Insert
    suspend fun insertWorkoutSet(workoutSet: WorkoutSet): Long

    @Update
    suspend fun updateWorkoutSet(workoutSet: WorkoutSet)

    @Delete
    suspend fun deleteWorkoutSet(workoutSet: WorkoutSet)

    @Query("DELETE FROM workout_sets WHERE id = :setId")
    suspend fun deleteWorkoutSetById(setId: Long)
}