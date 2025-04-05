package com.linhhoacao.tastybook.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TastyBookDao {
    //suspending function: a function that can be paused and resumed at a later time
    //suspending function: can execute a long running operation and wait for it to complete without blocking
    //suspending functions can only be invoked by another suspending function or within a coroutine
    //Use suspend:
    //1. When defining asynchronous functions.
    //2. For IO-bound operations (network, database, file).
    //3. To declare functions that can be paused and resumed.
    @Insert
    suspend fun insert(user: User)

    @Query("SELECT * FROM user_table WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    @Query("DELETE FROM user_table")
    suspend fun deleteAllUsers()

    @Query("SELECT * FROM user_table ORDER BY "
            + "level ASC, "
            + "score DESC, "
            + "duration ASC, "
            + "username ASC")
    fun getAllUsers(): List<User>

    @Query("SELECT * FROM user_table ORDER BY "
            + "level ASC, "
            + "score DESC, "
            + "duration ASC, "
            + "username ASC")

    fun getAllUsersLiveData(): LiveData<List<User>>
}