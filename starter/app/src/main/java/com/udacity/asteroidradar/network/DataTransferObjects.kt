package com.udacity.asteroidradar.network

import com.squareup.moshi.JsonClass
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.DatabaseAsteroid
import org.json.JSONObject

// Ideally this would be a Retrofit converter, but this seems like a good workaround
@JsonClass(generateAdapter = true)
data class NetworkAsteroidContainer(val asteroids: String)

fun NetworkAsteroidContainer.asDomainModel(): List<Asteroid> {
    return parseAsteroidsJsonResult(JSONObject(asteroids))
}

fun NetworkAsteroidContainer.asDatabaseModel(): Array<DatabaseAsteroid> {
    return parseAsteroidsJsonResult(JSONObject(asteroids)).map {
        DatabaseAsteroid(
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous
        )
    }.toTypedArray()
}

