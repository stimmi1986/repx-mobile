package com.hi.repx_mobile.data.database.dao

import androidx.room.*
import com.hi.repx_mobile.data.database.entities.BodyWeight
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyWeightDao {
    @Query("SELECT * FROM body_weights WHERE userId = :userId ORDER BY recordedAt DESC")
    fun getAllWeights(userId: Long): Flow<List<BodyWeight>>

    @Query("SELECT * FROM body_weights WHERE userId = :userId ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLatestWeight(userId: Long): BodyWeight?

    @Insert
    suspend fun insert(bodyWeight: BodyWeight): Long

    @Update
    suspend fun update(bodyWeight: BodyWeight)

    @Delete
    suspend fun delete(bodyWeight: BodyWeight)

    @Query("DELETE FROM body_weights WHERE id = :id")
    suspend fun deleteById(id: Long)
}