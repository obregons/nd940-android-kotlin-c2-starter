package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface AsteroidDao {

    @Query("SELECT * from databaseasteroid WHERE date(closeapproachdate) >= date(:today) ORDER BY date(closeapproachdate)")
    fun getCurrentAsteroids(today: String): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroids: DatabaseAsteroid)

    @Query("DELETE from databaseasteroid WHERE date(closeapproachdate) < date(:today)" )
    fun deleteOldAsteroids(today: String)

    @Query("DELETE FROM databaseasteroid")
    fun clear()
}

@Database(entities = [DatabaseAsteroid::class], version = 1)
abstract class AsteroidsDatabase() : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
}

private lateinit var INSTANCE: AsteroidsDatabase

fun getDatabase(context: Context): AsteroidsDatabase {
    synchronized(AsteroidsDatabase::class) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AsteroidsDatabase::class.java,
                "asteroids"
            ).build()
        }
    }
    return INSTANCE
}