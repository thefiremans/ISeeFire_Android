package com.swingfox.iseefire.iseefire.domain

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.swingfox.iseefire.iseefire.util.checkAndRequestPermission

class LocationService(val activity: Activity) {


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    @SuppressLint("MissingPermission")
    fun requestLocation(onLocation: (Location) -> Unit) {
        if(!checkAndRequestPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION))
            return
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location!=null){
                    onLocation(location)
                } else {
                    Log.e("TAG","location is null")
                }
            }

    }

}