package com.swingfox.iseefire.iseefire.presentation.main

import android.net.Uri
import com.swingfox.iseefire.iseefire.domain.ApiInteractor
import com.swingfox.iseefire.iseefire.domain.LocationService
import com.swingfox.iseefire.iseefire.domain.Repository
import com.swingfox.iseefire.iseefire.util.ImageUtil
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

class MainPresenter(val repository: Repository, val imageUtil: ImageUtil) : IMainPresenter {

    private var view: IMainView? = null
    private val apiInteractor = ApiInteractor()
    private var isLocationUpdating = true

    override fun registerUser() {
        GlobalScope.launch {
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
        GlobalScope.launch {
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
        GlobalScope.launch(Dispatchers.IO) {
            isLocationUpdating = true
            while (isLocationUpdating) {
                locationService.requestLocation { view?.onLocationUpdated(it) }
                delay(10000)
            }
        }
    }

    override fun stopUpdateLocation() {
        isLocationUpdating = false
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