package com.sample.ebayapp.data

import com.sample.ebayapp.data.model.Earthquakes
import retrofit2.http.GET

interface EarthquakeApi {

    @GET("/earthquakesJSON?formatted=true&north=44.1&south=-9.9&east=-22.4&west=55.2&username=mkoppelman")
    suspend fun getEarthquakeList(): Earthquakes
}