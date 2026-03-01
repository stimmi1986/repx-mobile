package com.hi.repx_mobile.data.database.dao

import androidx.room.*
import com.hi.repx_mobile.data.database.entities.Routine
import com.hi.repx_mobile.data.database.entities.RoutineExercise
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {
    @Query("SELECT * FROM routines WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllRoutines(userId: Long): Flow<List<Routine>>

    @Query("SELECT * FROM routines WHERE id = :routineId LIMIT 1")
    suspend fun getRoutineById(routineId: Long): Routine?

    @Insert
    suspend fun insertRoutine(routine: Routine): Long

    @Update
    suspend fun updateRoutine(routine: Routine)

    @Delete
    suspend fun deleteRoutine(routine: Routine)

    @Query("DELETE FROM routines WHERE id = :routineId")
    suspend fun deleteRoutineById(routineId: Long)

    // RoutineExercise queries
    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY orderIndex ASC")
    fun getRoutineExercises(routineId: Long): Flow<List<RoutineExercise>>

    @Query("SELECT * FROM routine_exercises WHERE routineId = :routineId ORDER BY orderIndex ASC")
    suspend fun getRoutineExercisesList(routineId: Long): List<RoutineExercise>

    @Insert
    suspend fun insertRoutineExercise(routineExercise: RoutineExercise): Long

    @Insert
    suspend fun insertRoutineExercises(exercises: List<RoutineExercise>)

    @Delete
    suspend fun deleteRoutineExercise(routineExercise: RoutineExercise)

    @Query("DELETE FROM routine_exercises WHERE routineId = :routineId")
    suspend fun deleteAllRoutineExercises(routineId: Long)
}
