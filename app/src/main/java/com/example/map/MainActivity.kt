package com.example.map

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.example.map.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var searchView: SearchView
    private lateinit var mapOptionalButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchView = findViewById(R.id.mapSearch)
        mapOptionalButton = findViewById(R.id.mapOption)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                val location = searchView.query.toString()
                var addressList: List<Address>? = null
                if (location != null || location != "") {
                    val geocoder = Geocoder(this@MainActivity)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val address = addressList?.get(0)
                    val latLng = LatLng(address?.latitude!!, address.longitude)
                    mMap.addMarker(MarkerOptions().position(latLng).title(location))
                    zoomInMap(latLng)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        val popupMenu = PopupMenu(this, mapOptionalButton)
        popupMenu.menuInflater.inflate(R.menu.map_options, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            changeMap(menuItem.itemId)
            true
        }

        mapOptionalButton.setOnClickListener {
            popupMenu.show()
        }
    }

    private fun zoomInMap(latLng: LatLng) {
        val newLatLngZoom = CameraUpdateFactory.newLatLngZoom(latLng, 12f)
        mMap.animateCamera(newLatLngZoom)
    }
    private fun changeMap(itemId: Int) {
        when (itemId) {
            R.id.normal_map -> mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
            R.id.hybrid_map -> mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satelite_map -> mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
            R.id.terrain_map -> mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }
}
