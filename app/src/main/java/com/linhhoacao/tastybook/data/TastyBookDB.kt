package com.linhhoacao.tastybook.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

//Room Database: Create the database instance.

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class TastyBookDB : RoomDatabase() {
    abstract fun tastyBookDao(): TastyBookDao
    //a companion object is similar to Java static declarations.
    //adding companion to the object declaration allows for adding
    // the "static" functionality to an object

    companion object {
        @Volatile
        private var INSTANCE: TastyBookDB? = null

        fun getDatabase(context: Context): TastyBookDB {
            //?: takes the right-hand value if the left-hand value is null (the elvis operator)
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TastyBookDB::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}