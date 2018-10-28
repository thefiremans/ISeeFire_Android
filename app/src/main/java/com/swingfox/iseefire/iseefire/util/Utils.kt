package com.swingfox.iseefire.iseefire.util

import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog

inline fun <T> tryOrNull(f: () -> T): T? {
    return try {
        f()
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }
}

fun checkAndRequestPermission(activity: Activity, permissionName: String): Boolean {
    val MY_PERMISSIONS_REQUEST = 100
    if (ContextCompat.checkSelfPermission(activity,
            permissionName) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                permissionName)) {

            AlertDialog.Builder(activity)
                .setTitle("Location permission")
                .setMessage("You need the location permission for some things to work")
                .setPositiveButton("OK") { _, i ->

                    ActivityCompat.requestPermissions(activity,
                        arrayOf(permissionName),
                        MY_PERMISSIONS_REQUEST)
                }
                .create()
                .show()

        } else {
            ActivityCompat.requestPermissions(activity,
                arrayOf(permissionName),
                MY_PERMISSIONS_REQUEST)
        }
        return false
    } else {
        return true
    }
}