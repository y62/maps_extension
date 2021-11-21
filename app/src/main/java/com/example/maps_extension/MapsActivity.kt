package com.example.maps_extension

import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.maps_extension.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.Marker
import java.lang.Exception
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapClickListener(listener)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                mMap.clear()
                val current_location = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(current_location).title("Are you here?"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current_location, 15f))
                val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
                try {
                    val address_list = geocoder .getFromLocation(location.latitude, location.longitude, 1)
                    if (address_list.size > 0) {
                        println(address_list[0].toString())
                    }
                } catch (e:Exception) {
                    e.printStackTrace()
                }
            }

        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1f, locationListener)
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastKnownLocation != null) {
                val last_knownLatgLng = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
                mMap.addMarker(MarkerOptions().position(last_knownLatgLng).position(last_knownLatgLng).title("Last Known Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(last_knownLatgLng, 15f))
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
          if (grantResults.size < 1) {
              if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1, 1f, locationListener)
              }
          }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    val listener = object : GoogleMap.OnMapClickListener {
        override fun onMapClick(p0: LatLng) {
            mMap.clear()
            val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
            if (p0 != null) {
                var address = ""
                try {
                    val addressList = geocoder.getFromLocation(p0.latitude, p0.longitude, 1)
                    if (addressList.size > 0) {
                        if (addressList.get(0).thoroughfare != null) {
                            address += addressList.get(0).thoroughfare
                            if (addressList.get(0).subThoroughfare != null) {
                                address += addressList.get(0).subThoroughfare
                            }
                        }
                    }
            } catch (e:Exception) {
                e.printStackTrace()
                }
                mMap.addMarker(MarkerOptions().position(p0).title(address))
            }
        }
    }
}