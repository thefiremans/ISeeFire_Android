package com.swingfox.iseefire.iseefire.presentation.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.View
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode
import com.swingfox.iseefire.iseefire.R
import com.swingfox.iseefire.iseefire.domain.LocationService
import com.swingfox.iseefire.iseefire.ISeeFireApplication
import com.swingfox.iseefire.iseefire.data.Fire
import com.swingfox.iseefire.iseefire.presentation.report.ReportActivity
import com.swingfox.iseefire.iseefire.util.checkAndRequestPermission
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity(), IMainView, OnMapReadyCallback {


    private val REPORT_REQUEST_CODE = 1002
    val presenter = MainPresenter(ISeeFireApplication.instance.repository, ISeeFireApplication.instance.imageUtil)

    private val locationService = LocationService(this)
    private var locationLayerPlugin: LocationLayerPlugin? = null
    private var map: MapboxMap? = null
    private var isFirst = true
    private var location: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView?.onCreate(savedInstanceState)
        presenter.attachView(this)
        showProgress()
        presenter.registerUser()
        requestPermissions()
        report_Button.setOnClickListener {
            startActivityForResult(
                ReportActivity.intent(
                    this,
                    location?.latitude ?: 0.0,
                    location?.longitude ?: 0.0
                ), REPORT_REQUEST_CODE
            )
        }
        nameTextView.setOnClickListener {
            isFirst = true
            presenter.getNearbyFires()
        }
        callButton.setOnClickListener {
            if(!checkAndRequestPermission(this, Manifest.permission.CALL_PHONE)) return@setOnClickListener
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "112"))
            startActivity(intent)
        }
    }


    override fun onStart() {
        super.onStart()
        mapView?.onStart()
        locationLayerPlugin?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        locationLayerPlugin?.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState ?: Bundle())
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                REPORT_REQUEST_CODE -> showReportSuccess()
            }
    }


    override fun onDestroy() {
        presenter.stopUpdateLocation()
        presenter.detachView()
        locationLayerPlugin?.onStop()
        locationLayerPlugin = null
        mapView?.onDestroy()
        super.onDestroy()
    }

    override fun onUserRegistered(userId: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            hideProgress()
            val snackbar = Snackbar.make(root_layout, "User authentication success", Snackbar.LENGTH_LONG)
            snackbar.view.setBackgroundColor(Color.BLUE)
            snackbar.show()
            mapView?.getMapAsync(this@MainActivity)
        }
    }

    override fun onProfileUpdated() {
        GlobalScope.launch(Dispatchers.Main) {
            val snackbar = Snackbar.make(root_layout, "Profile updating success", Snackbar.LENGTH_LONG)
            snackbar.view.setBackgroundColor(Color.BLUE)
            snackbar.show()
        }
    }

    override fun onLocationUpdated(location: Location) {
        GlobalScope.launch(Dispatchers.Main) {
            if (isFirst) {
                isFirst = false
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude, location.longitude), 15.0))
            }
            this@MainActivity.location = location
            presenter.getNearbyFires()
        }
    }

    override fun onMapReady(mapboxMap: MapboxMap) {
        map = mapboxMap
        presenter.updateLocation(locationService)
        mapboxMap.uiSettings.apply {
            isRotateGesturesEnabled = false
            isScrollGesturesEnabled = true
            isZoomGesturesEnabled = true
            isTiltGesturesEnabled = false
        }
        val locationComponent = mapboxMap.locationComponent
        if (checkAndRequestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION))
            mapView?.let { mapView ->
                locationLayerPlugin = LocationLayerPlugin(mapView, mapboxMap).also {
                    it.renderMode = RenderMode.NORMAL
                }
            }

    }


    override fun onError(error: String) {
        GlobalScope.launch(Dispatchers.Main) {
            hideProgress()
            val snackbar = Snackbar.make(root_layout, error, Snackbar.LENGTH_LONG)
            snackbar.view.setBackgroundColor(Color.RED)
            snackbar.show()
        }
    }

    override fun onGetNearbyFire(fires: List<Fire>) {
        GlobalScope.launch(Dispatchers.Main) {
            map?.removeAnnotations()
            fires.forEach {
                val marker = MarkerOptions()
                val icon = IconFactory.getInstance(this@MainActivity).fromResource(R.mipmap.fire_marker_red_80)
                marker.icon = icon
                marker.position = LatLng(it.latitude, it.longitude)
                Log.e("MAP", "marker $map")
                map?.addMarker(marker)
            }
        }
    }

    private fun showReportSuccess() {
        GlobalScope.launch(Dispatchers.Main) {
            val snackbar = Snackbar.make(
                root_layout,
                "Your report was successfully uploaded. \n Thank you for your help",
                Snackbar.LENGTH_LONG
            )
            snackbar.view.setBackgroundColor(Color.BLUE)
            snackbar.show()
        }
    }

    private fun showProgress() {
        progressBarLayout.visibility = View.VISIBLE
    }

    private fun hideProgress() {
        progressBarLayout.visibility = View.GONE
    }

    private fun requestPermissions() {
        checkAndRequestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        checkAndRequestPermission(this, Manifest.permission.CAMERA)
        checkAndRequestPermission(this, Manifest.permission.CALL_PHONE)
        checkAndRequestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

    }


}
