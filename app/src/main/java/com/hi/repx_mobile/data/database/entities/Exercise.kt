package com.hi.repx_mobile.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val primaryMuscle: String,
    val equipment: String? = null,
    val description: String? = null,
    val isCustom: Boolean = false,
    val userId: Long? = null
)

// Predefined muscle groups
object MuscleGroups {
    const val CHEST = "Chest"
    const val BACK = "Back"
    const val SHOULDERS = "Shoulders"
    const val BICEPS = "Biceps"
    const val TRICEPS = "Triceps"
    const val LEGS = "Legs"
    const val CORE = "Core"
    const val CARDIO = "Cardio"
    const val FULL_BODY = "Full Body"

    val all = listOf(CHEST, BACK, SHOULDERS, BICEPS, TRICEPS, LEGS, CORE, CARDIO, FULL_BODY)
}

// Predefined equipment types
object Equipment {
    const val BARBELL = "Barbell"
    const val DUMBBELL = "Dumbbell"
    const val MACHINE = "Machine"
    const val CABLE = "Cable"
    const val BODYWEIGHT = "Bodyweight"
    const val KETTLEBELL = "Kettlebell"
    const val OTHER = "Other"

    val all = listOf(BARBELL, DUMBBELL, MACHINE, CABLE, BODYWEIGHT, KETTLEBELL, OTHER)
}
