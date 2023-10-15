package com.example.ringoo

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.ringoo.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val customMarkerBitmap = BitmapFactory.decodeResource(resources, R.drawable.bus_icon)

        val user = LatLng(37.17306963612173, 38.99769571159779)
        val buss = LatLng(37.16595648556052, 38.996100618280884)

        val customMarkerIcon: BitmapDescriptor

        if (customMarkerBitmap != null) {
            customMarkerIcon = BitmapDescriptorFactory.fromBitmap(customMarkerBitmap)
        } else {
            customMarkerIcon = BitmapDescriptorFactory.defaultMarker()
        }

        val bussMarkerOptions = MarkerOptions()
        bussMarkerOptions.position(buss)
        bussMarkerOptions.title("63 BC 253")
        bussMarkerOptions.icon(customMarkerIcon)

        mMap.addMarker(MarkerOptions().position(user).title("YOU"))
        mMap.addMarker(bussMarkerOptions)

        val zoomLevel = 14.75f
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, zoomLevel))
    }

}