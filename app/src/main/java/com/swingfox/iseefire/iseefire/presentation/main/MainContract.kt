package com.swingfox.iseefire.iseefire.presentation.main

import android.location.Location
import android.net.Uri
import com.swingfox.iseefire.iseefire.domain.LocationService
import com.swingfox.iseefire.iseefire.presentation.base.IBasePresenter
import com.swingfox.iseefire.iseefire.presentation.base.IBaseView

interface IMainPresenter: IBasePresenter<IMainView> {
    fun reportFire()

    fun registerUser()

    fun updateProfile(name: String, phone: String)

    fun updateLocation(locationService: LocationService)

    fun stopUpdateLocation()

    fun uploadImage(path: Uri)
}

interface IMainView: IBaseView {
    fun onImageUploaded()

    fun onFireReported(fireId: Int?)

    fun onUserRegistered(userId: String?)

    fun onProfileUpdated()

    fun onError(error: String)

    fun onLocationUpdated(location: Location)
}