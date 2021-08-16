package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import com.udacity.asteroidradar.network.Radar
import com.udacity.asteroidradar.network.asDatabaseModel
import com.udacity.asteroidradar.network.getCurrentDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.net.SocketTimeoutException

class AsteroidsRepository(private val database: AsteroidsDatabase) {
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getCurrentAsteroids(getCurrentDate())) {
            it.asDomainModel()
        }

    suspend fun refreshData() {
        withContext(Dispatchers.IO) {
            Timber.d("Get data from network")
            try {
                val asteroidList = Radar.service.getAsteroidsByDateAsync().await()
                val container = NetworkAsteroidContainer(asteroidList)
                database.asteroidDao.insertAll(*container.asDatabaseModel())
            } catch (e: Exception) {
                if (e is SocketTimeoutException) {
                    Timber.e(e, "Connection timeout")
                } else {
                    Timber.e(e)
                }
            }
        }
    }

    suspend fun cleanData() {
        withContext(Dispatchers.IO) {
            Timber.d("Delete old asteroids")
            try {
                database.asteroidDao.deleteOldAsteroids(getCurrentDate())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }
}