package com.example.childcontrol.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [Child::class, DeviceBannedTime::class, LocalAppList::class],
    version = 9,
    exportSchema = false
)
abstract class ChildDatabase : RoomDatabase() {
    abstract val childDatabaseDao: ChildDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: ChildDatabase? = null

        fun getInstance(context: Context): ChildDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        ChildDatabase::class.java,
                        "child_history_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}