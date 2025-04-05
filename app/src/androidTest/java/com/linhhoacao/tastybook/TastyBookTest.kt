package com.linhhoacao.tastybook

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.linhhoacao.tastybook.data.TastyBookDB
import com.linhhoacao.tastybook.data.TastyBookDao
import com.linhhoacao.tastybook.data.User
import kotlinx.coroutines.runBlocking
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TastyBookTest {
    private lateinit var db: TastyBookDB
    private lateinit var tastyBookDao: TastyBookDao

    // Context of the app under test.
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun createDb() {
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(appContext, TastyBookDB::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        tastyBookDao = db.tastyBookDao()
    }
    @After
    fun closeDb() {
        db.close()
    }

    @Test
    fun useAppContext() {
        assertEquals("com.linhhoacao.tastybook", appContext.packageName)
    }

    @Test
    @Throws(Exception::class)
    //runBlocking:
    //- Blocks the current thread until the coroutine completes.
    //- Used to bridge synchronous and asynchronous code.
    //- Creates a scope for coroutines, controlling their lifetime.
    //Key differences:
    //- suspend defines asynchronous functions.
    //- runBlocking executes coroutines synchronously.
    fun insertAndGetUser() = runBlocking {
        val user = User(username = "Tony", score = 100, duration = 50, date = 10000L)
        tastyBookDao.insert(user)
        val fetchedUser = tastyBookDao.getUserById(1)
        assertEquals("Tony", fetchedUser?.username)
    }

    @Test
    fun deleteAllUsers() = runBlocking {
        val user = User(username = "Sarah", score = 80, duration = 70)
        tastyBookDao.insert(user)
        tastyBookDao.deleteAllUsers()
        val fetchedUser = tastyBookDao.getUserById(1)
        assertNull(fetchedUser)
    }

    @Test
    fun getAllUsers() = runBlocking {
        val user1 = User(username = "Sarah", score = 80, duration = 70)
        val user2 = User(username = "Tony", score = 100, duration = 50, date = 10000L)
        tastyBookDao.insert(user1)
        tastyBookDao.insert(user2)

        val allUsers = tastyBookDao.getAllUsers()
        assertEquals(allUsers.size, 2)
        //order by score DESC -> Tony is the first record
        assertEquals(allUsers[0].username, "Tony")
        assertEquals(allUsers[1].username, "Sarah")
    }

}