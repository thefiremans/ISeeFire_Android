package com.swingfox.iseefire.iseefire.presentation.report

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide
import com.swingfox.iseefire.iseefire.R
import com.swingfox.iseefire.iseefire.presentation.ISeeFireApplication
import com.swingfox.iseefire.iseefire.util.ImageUtil
import kotlinx.android.synthetic.main.activity_report.*
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class ReportActivity : AppCompatActivity(), IReportView {

    private val REQUEST_SELECT_IMAGE_IN_ALBUM = 1001
    private val presenter = ReportPresenter(ISeeFireApplication.instance.repository, ImageUtil(this))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        presenter.attachView(this)
        report_Button.setOnClickListener { presenter.reportFire("Test Comment") }
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
                            presenter.setImageUri(uri)
                        }
                    }
                    data?.data?.let { uri ->
                        Glide.with(this)
                            .load(uri)
                            .into(fire_image)
                        presenter.setImageUri(uri)
                    }
                }
            }
        }
    }

    override fun onImageUploaded(reportId: Int) {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onFireReported(reportId: Int) {
        setResult(Activity.RESULT_OK)
        finish()
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

    companion object {
        fun intent(context: Context) = Intent(context, ReportActivity::class.java)

    }
}