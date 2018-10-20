package com.swingfox.iseefire.iseefire.presentation.main

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import com.swingfox.iseefire.iseefire.R
import com.swingfox.iseefire.iseefire.domain.LocationService
import com.swingfox.iseefire.iseefire.presentation.ISeeFireApplication
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity(), IMainView {

    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1001

    val presenter = MainPresenter(ISeeFireApplication.instance.repository, ISeeFireApplication.instance.imageUtil)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.attachView(this)
        presenter.registerUser()
        presenter.updateLocation(LocationService(this))
        report_Button.setOnClickListener { presenter.reportFire() }
        gallery_Button.setOnClickListener { selectImageInAlbum() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Log.e("TAG", "REQUEST_CODE $requestCode")
            when (requestCode) {
                REQUEST_SELECT_IMAGE_IN_ALBUM -> {
                    data?.clipData?.let { clipData ->
                        (0 until clipData.itemCount).mapNotNull { clipData.getItemAt(it).uri }.forEach { uri ->
                            presenter.uploadImage(uri)
                        }
                    }
                    data?.data?.let { uri -> presenter.uploadImage(uri) }
                }
            }
        }
    }


    override fun onDestroy() {
        presenter.stopUpdateLocation()
        presenter.detachView()
        super.onDestroy()
    }

    override fun onFireReported(reportId: Int?) {
        GlobalScope.launch(Dispatchers.Main) {
            report_TextView.text = "Fire id is ${reportId ?: 0}"
        }
    }

    override fun onUserRegistered(userId: String?) {
        GlobalScope.launch(Dispatchers.Main) {
            report_TextView.text = "User id is ${userId ?: 0}"
        }
    }

    override fun onProfileUpdated() {
        GlobalScope.launch(Dispatchers.Main) {
            report_TextView.text = "User profile is updated"
        }
    }

    override fun onLocationUpdated(location: Location) {
        GlobalScope.launch(Dispatchers.Main) {
            report_TextView.text = "Location lat:${location.latitude} long:${location.longitude}"
        }
    }

    override fun onImageUploaded() {
        GlobalScope.launch(Dispatchers.Main) {
            val snackbar = Snackbar.make(root_layout, "Image uploaded", Snackbar.LENGTH_LONG)
            snackbar.view.setBackgroundColor(Color.BLUE)
            snackbar.show()
        }
    }

    override fun onError(error: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val snackbar = Snackbar.make(root_layout, error, Snackbar.LENGTH_LONG)
            snackbar.view.setBackgroundColor(Color.RED)
            snackbar.show()
        }
    }

    private fun selectImageInAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_SELECT_IMAGE_IN_ALBUM)
        }
    }

}
