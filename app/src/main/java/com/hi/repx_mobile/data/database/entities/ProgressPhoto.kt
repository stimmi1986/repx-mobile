package com.hi.repx_mobile.data.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "progress_photos",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class ProgressPhoto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val filePath: String,
    val note: String? = null,
    val takenAt: Long = System.currentTimeMillis()
)