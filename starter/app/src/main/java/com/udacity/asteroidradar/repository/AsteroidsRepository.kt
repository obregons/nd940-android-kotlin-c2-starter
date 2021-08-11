package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import com.udacity.asteroidradar.network.Radar
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AsteroidsRepository(private val database: AsteroidsDatabase) {
    val asteroids: LiveData<List<Asteroid>> =
        Transformations.map(database.asteroidDao.getAllAsteroids()) {
            it.asDomainModel()
        }

    suspend fun refreshData() {
        withContext(Dispatchers.IO) {
            val asteroidList = Radar.service.getAsteroidsByDateAsync().await()
            val container = NetworkAsteroidContainer(asteroidList)
            database.asteroidDao.insertAll(*container.asDatabaseModel())
        }
    }
}