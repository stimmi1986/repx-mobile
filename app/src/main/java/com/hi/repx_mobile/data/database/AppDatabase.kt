package com.hi.repx_mobile.data.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hi.repx_mobile.data.database.dao.*
import com.hi.repx_mobile.data.database.entities.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Workout::class,
        WorkoutExercise::class,
        WorkoutSet::class,
        Exercise::class,
        Routine::class,
        RoutineExercise::class,
        ProgressPhoto::class,
        BodyWeight::class
    ],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun routineDao(): RoutineDao
    abstract fun progressPhotoDao(): ProgressPhotoDao
    abstract fun bodyWeightDao(): BodyWeightDao

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
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    seedDefaultExercises(database.exerciseDao())
                                }
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedDefaultExercises(exerciseDao: ExerciseDao) {
            if (exerciseDao.getDefaultExerciseCount() == 0) {
                exerciseDao.insertAll(DefaultExercises.exercises)
            }
        }
    }
}
