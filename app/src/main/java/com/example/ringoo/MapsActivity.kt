package com.example.ringoo

import android.content.Context
import android.graphics.Bitmap

import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ringoo.databinding.ActivityMapsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.maps.model.LatLng

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    val apiService = RetrofitInstance.retrofit.create(ApiService::class.java)

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val handler = Handler()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val updateInterval = 5000L
        handler.postDelayed(object : Runnable {
            override fun run() {
                mMap.clear()
                fetchBusLocationsFromAPI()
                handler.postDelayed(this, updateInterval)
            }
        }, updateInterval)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val user = LatLng(37.17306963612173, 38.99769571159779)

        mMap.addMarker(
            MarkerOptions()
                .position(user)
                .title("YOU")
        )

        val zoomLevel = 15.25f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, zoomLevel))
    }


    //MARK- İCON BİTMAP CONVERTER
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }

    //MARK-- FETCH ALL BUSSES LOCATİON
    fun fetchBusLocationsFromAPI() {
        apiService.getLastLocations().enqueue(object : Callback<List<LocationModelItem>> {
            override fun onResponse(
                call: Call<List<LocationModelItem>>,
                response: Response<List<LocationModelItem>>
            ) {
                if (response.isSuccessful) {
                    val locations = response.body()
                    if (locations != null) {
                        for (location in locations) {
                            val newLocation = LatLng(location.latitude, location.longitude)
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(newLocation)
                                        .icon(bitmapDescriptorFromVector(this@MapsActivity, R.drawable.bus_icon))
                                        .title("Bus ${location.deviceId}")
                                )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<LocationModelItem>>, t: Throwable) {
                Log.e("aaaa", "error")
            }
        })
    }
}
