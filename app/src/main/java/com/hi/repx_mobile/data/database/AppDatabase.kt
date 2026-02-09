package com.hi.repx_mobile.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hi.repx_mobile.data.database.dao.UserDao
import com.hi.repx_mobile.data.database.entities.User


@Database(
    entities = [
        User::class,

    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

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
                    .addCallback(object : Callback() {})
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
