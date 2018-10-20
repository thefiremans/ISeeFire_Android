package com.swingfox.iseefire.iseefire.presentation

import android.app.Application
import com.swingfox.iseefire.iseefire.domain.Repository
import com.swingfox.iseefire.iseefire.util.ImageUtil
import com.swingfox.iseefire.iseefire.util.PreferenceUtil

class ISeeFireApplication: Application() {

    lateinit var repository: Repository
    lateinit var imageUtil: ImageUtil
    override fun onCreate() {
        super.onCreate()
        repository = Repository(PreferenceUtil(this))
        imageUtil = ImageUtil(this)
        instance = this
    }

    companion object {
        lateinit var instance: ISeeFireApplication
    }
}