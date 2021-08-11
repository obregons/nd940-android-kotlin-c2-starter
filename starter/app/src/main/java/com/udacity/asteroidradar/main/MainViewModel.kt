package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import com.udacity.asteroidradar.network.Radar
import com.udacity.asteroidradar.network.asDomainModel
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroids: LiveData<List<Asteroid>>
        get() = _asteroids

    private val _navigateToSelectedAsteroid = MutableLiveData<Asteroid>()
    val navigateToSelectedAsteroid: LiveData<Asteroid>
        get() = _navigateToSelectedAsteroid

    fun displayAsteroidDetailsComplete() {
        _navigateToSelectedAsteroid.value = null
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToSelectedAsteroid.value = asteroid
    }

    init {
        refreshDataFromNetwork()
    }

    private fun refreshDataFromNetwork() {
        viewModelScope.launch {
            try {
                val result = Radar.service.getAsteroidsByDateAsync().await()
                val container = NetworkAsteroidContainer(result)
                _asteroids.postValue(container.asDomainModel())
            } catch (e: Exception) {
                _asteroids.value = ArrayList()
            }
        }
    }
}
