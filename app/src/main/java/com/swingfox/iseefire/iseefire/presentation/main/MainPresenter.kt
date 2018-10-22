package com.swingfox.iseefire.iseefire.presentation.main

import android.location.Location
import android.net.Uri
import com.swingfox.iseefire.iseefire.domain.ApiInteractor
import com.swingfox.iseefire.iseefire.domain.LocationService
import com.swingfox.iseefire.iseefire.domain.Repository
import com.swingfox.iseefire.iseefire.util.ImageUtil
import kotlinx.coroutines.experimental.*

class MainPresenter(val repository: Repository, val imageUtil: ImageUtil) : IMainPresenter {

    private var view: IMainView? = null
    private val apiInteractor = ApiInteractor()
    private var isLocationUpdating = true
    private var location: Location? = null
    private val dispatcher = newFixedThreadPoolContext(4, "MainPresenter")

    override fun registerUser() {
        GlobalScope.launch(dispatcher) {
            try {
                val user = apiInteractor.registerUser()
                repository.userId = user.userId ?: ""
                if (!user.error.isNullOrEmpty())
                    view?.onError(user.error ?: "Unknown")
                else
                    view?.onUserRegistered(user.userId)
            } catch (ex: Exception) {handleUncaughtError()}
        }
    }

    override fun updateProfile(name: String, phone: String) {
        GlobalScope.launch(dispatcher) {
            try {
                val response = apiInteractor.updateProfile(repository.userId, name, phone)
                if (!response.error.isNullOrEmpty())
                    view?.onError(response.error ?: "Unknown")
                else
                    view?.onProfileUpdated()
            } catch (ex: Exception) {handleUncaughtError()}
        }
    }

    override fun updateLocation(locationService: LocationService) {
        GlobalScope.launch(dispatcher) {
            isLocationUpdating = true
            while (isLocationUpdating) {
                locationService.requestLocation {
                    this@MainPresenter.location = it
                    view?.onLocationUpdated(it)
                }
                delay(10000)
            }
        }
    }

    override fun stopUpdateLocation() {
        isLocationUpdating = false
    }

    override fun getNearbyFires() {
        GlobalScope.launch(dispatcher) {
            try {
                val fires = apiInteractor.getNearbyFires(repository.userId, location?.latitude ?: 0.1, location?.longitude ?: 0.1, 120000.0)
                view?.onGetNearbyFire(fires)
            } catch (ex: Exception) {handleUncaughtError()}
        }
    }

    override fun attachView(view: IMainView) {
        this.view = view
    }

    override fun detachView() {
        view = null
    }

    private fun handleUncaughtError() {
        view?.onError("Uncaught Error")
    }
}