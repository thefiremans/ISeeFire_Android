package com.swingfox.iseefire.iseefire.domain

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.location.Location
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.json.JSONObject

class LocationService(val activity: Activity) {

    private val MY_PERMISSIONS_REQUEST_LOCATION = 100

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    @SuppressLint("MissingPermission")
    fun requestLocation(onLocation: (Location) -> Unit) {
        if(!checkLocationPermission())
            return
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location!=null){

                    val jsonObject = JSONObject()
                    jsonObject.put("latitude",location.latitude)
                    jsonObject.put("longitude",location.longitude)

                } else {
                    Log.e("TAG","location is null")
                }
            }

    }

    fun checkLocationPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                AlertDialog.Builder(activity)
                    .setTitle("Location permission")
                    .setMessage("You need the location permission for some things to work")
                    .setPositiveButton("OK") { _, i ->

                        ActivityCompat.requestPermissions(activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION)
                    }
                    .create()
                    .show()

            } else {
                ActivityCompat.requestPermissions(activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION)
            }
            return false
        } else {
            return true
        }
    }
}