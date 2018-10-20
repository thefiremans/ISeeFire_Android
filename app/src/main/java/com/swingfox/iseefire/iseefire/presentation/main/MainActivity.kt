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
import com.swingfox.iseefire.iseefire.presentation.report.ReportActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class MainActivity : AppCompatActivity(), IMainView {

    private val REPORT_REQUEST_CODE = 1002
    val presenter = MainPresenter(ISeeFireApplication.instance.repository, ISeeFireApplication.instance.imageUtil)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        presenter.attachView(this)
        presenter.registerUser()
        presenter.updateLocation(LocationService(this))
        report_Button.setOnClickListener { startActivityForResult(ReportActivity.intent(this), REPORT_REQUEST_CODE) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REPORT_REQUEST_CODE -> showReportSuccess()
            }
        }
    }


    override fun onDestroy() {
        presenter.stopUpdateLocation()
        presenter.detachView()
        super.onDestroy()
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


    override fun onError(error: String) {
        GlobalScope.launch(Dispatchers.Main) {
            val snackbar = Snackbar.make(root_layout, error, Snackbar.LENGTH_LONG)
            snackbar.view.setBackgroundColor(Color.RED)
            snackbar.show()
        }
    }

    private fun showReportSuccess() {
        GlobalScope.launch(Dispatchers.Main) {
            val snackbar = Snackbar.make(
                root_layout,
                "Your report was successfuly uploaded. \n Thank you for your help",
                Snackbar.LENGTH_LONG
            )
            snackbar.view.setBackgroundColor(Color.BLUE)
            snackbar.show()
        }
    }

}
