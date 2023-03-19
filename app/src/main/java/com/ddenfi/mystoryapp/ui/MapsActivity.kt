package com.ddenfi.mystoryapp.ui

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.ddenfi.mystoryapp.R
import com.ddenfi.mystoryapp.database.ListStoryItem
import com.ddenfi.mystoryapp.databinding.ActivityMapsBinding
import com.ddenfi.mystoryapp.utils.Results
import com.ddenfi.mystoryapp.viewmodel.MapsViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val viewModel:MapsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.map_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }
            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }
            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }
            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        addStory()
        setMapStyle()

        // Move the camera to Indonesia
        val indonesia = LatLng(0.7893, 113.9213)
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(indonesia, 4f))
    }

    private fun setMapStyle() {
        try {
            val success =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
    }

    private fun addStory() {
        viewModel.getToken().observe(this) { token ->
            viewModel.getMapStories(token).observe(this){
                when (it) {
                    is Results.Loading -> showLoading()
                    is Results.Error -> showError(it.message)
                    is Results.Success -> {
                        showSuccess()
                        val story = it.data?.listStory
                        if (story != null) {
                            for (i in story.indices){
                              addMarker(story[i])
                            }
                        }
                    }
                }
            }
        }
    }

    private fun addMarker(story:ListStoryItem){
        val position = LatLng(story.lat,story.lon)
        mMap.addMarker(MarkerOptions()
            .position(position)
            .title(story.name)
            .snippet(story.createdAt))
    }

    private fun showError(error: String?) {
        binding.pbBlock.visibility = View.GONE
        if (error == "Missing authentication") {
            logOut()
            Toast.makeText(this@MapsActivity, error , Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this@MapsActivity, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading() {
        binding.pbBlock.visibility = View.VISIBLE
    }

    private fun showSuccess() {
        binding.pbBlock.visibility = View.GONE
    }

    private fun logOut(){
        viewModel.saveUserState("",false)
        val intent = Intent(this,LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or  Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
        Toast.makeText(application,"Logged Out!",Toast.LENGTH_SHORT).show()
    }


}