package com.hi.repx_mobile.data.database.dao

import androidx.room.*
import com.hi.repx_mobile.data.database.entities.ProgressPhoto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressPhotoDao {
    @Query("SELECT * FROM progress_photos WHERE userId = :userId ORDER BY takenAt DESC")
    fun getAllPhotos(userId: Long): Flow<List<ProgressPhoto>>

    @Query("SELECT * FROM progress_photos WHERE id = :photoId LIMIT 1")
    suspend fun getPhotoById(photoId: Long): ProgressPhoto?

    @Insert
    suspend fun insertPhoto(photo: ProgressPhoto): Long

    @Update
    suspend fun updatePhoto(photo: ProgressPhoto)

    @Delete
    suspend fun deletePhoto(photo: ProgressPhoto)

    @Query("DELETE FROM progress_photos WHERE id = :photoId")
    suspend fun deletePhotoById(photoId: Long)

    @Query("SELECT COUNT(*) FROM progress_photos WHERE userId = :userId")
    suspend fun getPhotoCount(userId: Long): Int
}