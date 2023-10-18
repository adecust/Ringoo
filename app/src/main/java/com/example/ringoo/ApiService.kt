package com.example.ringoo

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {


    @GET("/api/v1/bus/getLastLocations")
    fun getLastLocations() : Call<List<LocationModelItem>>

}