package com.udacity.asteroidradar.network

import android.annotation.SuppressLint
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


interface RadarService {
    // https://api.nasa.gov/neo/rest/v1/feed?start_date=START_DATE&end_date=END_DATE&api_key=YOUR_API_KEY
    @GET("neo/rest/v1/feed")
    fun getAsteroidsByDateAsync(
        @Query("start_date") start: String = getCurrentDate(),
        @Query("end_date") end: String = getEndDate(),
        @Query("api_key") key: String = API_KEY
    ): Deferred<String>

    // https://api.nasa.gov/planetary/apod?api_key=YOUR_API_KEY
    @GET("planetary/apod")
    fun getPictureOfDay(
        @Query("api_key") key: String = API_KEY
    ): Deferred<PictureOfDay>
}

// TODO: Make this a helper function since we use it in a couple of places
@SuppressLint("WeekBasedYear")
fun getCurrentDate(): String {
    val calendar = Calendar.getInstance()
    val currentTime = calendar.time
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(currentTime)
}

@SuppressLint("WeekBasedYear")
private fun getEndDate(): String {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_YEAR, Constants.DEFAULT_END_DATE_DAYS)
    val currentTime = calendar.time
    val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
    return dateFormat.format(currentTime)
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object Radar {

    // Define an okHttp client to set timeouts and avoid SocketTimeoutException
    // https://howtodoinjava.com/retrofit2/connection-timeout-exception/
    private var okHttpClient = OkHttpClient.Builder()
        .callTimeout(3, TimeUnit.MINUTES)
        .connectTimeout(2, TimeUnit.MINUTES)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofitScalar = Retrofit.Builder()
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    private val retrofitMoshi = Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()

    val service: RadarService by lazy {
        retrofitScalar.create(RadarService::class.java)
    }

    val picture: RadarService by lazy {
        retrofitMoshi.create(RadarService::class.java)
    }
}