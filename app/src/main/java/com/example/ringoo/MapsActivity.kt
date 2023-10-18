package com.example.ringoo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap

import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ringoo.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    val apiService = RetrofitInstance.retrofit.create(ApiService::class.java)
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    private lateinit var mMap: GoogleMap
    private lateinit var nMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val handler = Handler()
    private val busMarkers = mutableListOf<Marker>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val updateInterval = 5000L
        handler.postDelayed(object : Runnable {
            override fun run() {
                for (busMarker in busMarkers) {
                    busMarker.remove()
                }
                busMarkers.clear()

                fetchBusLocationsFromAPI()
                handler.postDelayed(this, updateInterval)
            }
        }, updateInterval)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        nMap= googleMap
        createStations()
        val user = LatLng(37.17306963612173, 38.99769571159779)
        val zoomLevel = 15.25f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, zoomLevel))
    }


    //MARK- İCON BİTMAP CONVERTER
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
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
                    val locations = response.body() ?: return

                    for (location in locations) {
                        val newLocation = LatLng(location.latitude, location.longitude)
                       val busMarker= mMap.addMarker(
                            MarkerOptions()
                                .position(newLocation)
                                .icon(
                                    bitmapDescriptorFromVector(
                                        this@MapsActivity,
                                        R.drawable.bus_icon
                                    )
                                )
                                .title("Bus ${location.deviceId}")
                        )
                        if (busMarker != null) {
                            busMarkers.add(busMarker)
                        }
                    }

                }
            }

            override fun onFailure(call: Call<List<LocationModelItem>>, t: Throwable) {
                Log.e("aaaa", "Api Error")
            }
        })
    }
    //MARK- STATİONS
    fun createStations(){
        val coordinates = listOf(
            Pair(37.16586167202024, 38.99618142474569),
            Pair(37.1662017320957, 38.99786977371168),
            Pair(37.16750670104437, 39.0017757324483),
            Pair(37.16823699237787, 39.00283023736826),
            Pair(37.171026468594555, 39.00517814888009),
            Pair(37.17351858530618, 39.00189170114116),
            Pair(37.17435824962693, 38.99877685898203),
            Pair(37.17515536285534, 38.99691507249199),
            Pair(37.17735326513586, 38.99572918004911),
            Pair(37.175414019440055, 38.99544128991743),
            Pair(37.17121916241424, 38.99627597497785),
            Pair(37.16880202104119, 38.99495270003612)
        )

        for (coordinate in coordinates){
            val cord=LatLng(coordinate.first,coordinate.second)
            nMap.addMarker(
                MarkerOptions()
                    .position(cord)
                    .icon(
                        bitmapDescriptorFromVector(
                            this@MapsActivity,
                            R.drawable.durak
                        )
                    )

            )
        }

    }


}
