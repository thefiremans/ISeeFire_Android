package com.swingfox.iseefire.iseefire.presentation.main

import android.location.Location
import android.net.Uri
import com.swingfox.iseefire.iseefire.data.Fire
import com.swingfox.iseefire.iseefire.domain.LocationService
import com.swingfox.iseefire.iseefire.presentation.base.IBasePresenter
import com.swingfox.iseefire.iseefire.presentation.base.IBaseView

interface IMainPresenter: IBasePresenter<IMainView> {
    fun registerUser()

    fun updateProfile(name: String, phone: String)

    fun updateLocation(locationService: LocationService)

    fun stopUpdateLocation()
    fun getNearbyFires()
}

interface IMainView: IBaseView {

    fun onUserRegistered(userId: String?)

    fun onProfileUpdated()

    fun onLocationUpdated(location: Location)
    fun onGetNearbyFire(fires: List<Fire>)
}