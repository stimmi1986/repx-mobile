package com.hi.repx_mobile.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hi.repx_mobile.data.database.dao.*
import com.hi.repx_mobile.data.database.entities.*

@Database(
    entities = [
        User::class,
        Workout::class,
        WorkoutExercise::class,
        WorkoutSet::class,
        Exercise::class,
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "repx_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .addCallback(object : Callback() {})
                    .build()
                INSTANCE = instance
                instance
            }
        }


    }
}
