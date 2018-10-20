package com.swingfox.iseefire.iseefire.presentation.report

import android.net.Uri
import com.swingfox.iseefire.iseefire.domain.ApiInteractor
import com.swingfox.iseefire.iseefire.domain.Repository
import com.swingfox.iseefire.iseefire.util.ImageUtil
import kotlinx.coroutines.experimental.Dispatchers
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.launch

class ReportPresenter(val repository: Repository, val imageUtil: ImageUtil) : IReportPresenter {

    private var view: IReportView? = null
    private val apiInteractor = ApiInteractor()

    private var imagePath: Uri? = null

    override fun reportFire(comment: String) {
        GlobalScope.launch {
            val report = apiInteractor.reportFire(repository.userId)
            if (!report.error.isNullOrEmpty())
                view?.onError(report.error ?: "Unknown error")
            else if (imagePath != null) {
                uploadImage(report.reportId ?: 0)
            } else
                view?.onFireReported(report.reportId ?: 0)
        }
    }

    override fun setImageUri(uri: Uri) {
        this.imagePath = uri
    }

    override fun attachView(view: IReportView) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    private fun uploadImage(reportId: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                imagePath?.let {
                    val path = imageUtil.readToLocal(it)
                    val response = apiInteractor.uploadImage(repository.userId, reportId.toString(), path)
                    if (!response.error.isNullOrEmpty())
                        view?.onError(response.error ?: "Unknown error")
                    else
                        view?.onImageUploaded(reportId)
                }
            } catch (ex: Exception) {
                handleUncaughtError()
            }
        }
    }

    private fun handleUncaughtError() {
        view?.onError("Uncaught Error")
    }

}