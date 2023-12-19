package com.sample.ebayapp.data.repository

import com.sample.ebayapp.data.EarthquakeApi
import com.sample.ebayapp.data.model.Earthquake
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EarthquakeRepository @Inject constructor(private val api: EarthquakeApi) {

    private val earthquakes = MutableStateFlow<List<Earthquake>>(emptyList())

    suspend fun getEarthquakes(): List<Earthquake> {
        val result = api.getEarthquakeList().earthquakes
        earthquakes.value = result
        return result
    }

    fun getEarthquake(id: String): Earthquake? {
        return earthquakes.value.firstOrNull { it.eqid == id }
    }
}