package com.hi.repx_mobile.data.database.dao

import androidx.room.*
import com.hi.repx_mobile.data.database.entities.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises WHERE isCustom = 0 OR userId = :userId ORDER BY name ASC")
    fun getAllExercises(userId: Long): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE (isCustom = 0 OR userId = :userId) AND name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchExercises(userId: Long, query: String): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE (isCustom = 0 OR userId = :userId) AND primaryMuscle = :muscle ORDER BY name ASC")
    fun getExercisesByMuscle(userId: Long, muscle: String): Flow<List<Exercise>>

    @Query("SELECT * FROM exercises WHERE id = :id LIMIT 1")
    suspend fun getExerciseById(id: Long): Exercise?

    @Query("SELECT * FROM exercises WHERE isCustom = 0")
    suspend fun getDefaultExercises(): List<Exercise>

    @Query("SELECT * FROM exercises WHERE isCustom = 1 AND userId = :userId")
    fun getCustomExercises(userId: Long): Flow<List<Exercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: Exercise): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<Exercise>)

    @Update
    suspend fun update(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)

    @Query("SELECT COUNT(*) FROM exercises WHERE isCustom = 0")
    suspend fun getDefaultExerciseCount(): Int
}
