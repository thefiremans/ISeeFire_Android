package com.swingfox.iseefire.iseefire.presentation.report

import android.net.Uri
import com.swingfox.iseefire.iseefire.presentation.base.IBasePresenter
import com.swingfox.iseefire.iseefire.presentation.base.IBaseView

interface IReportPresenter: IBasePresenter<IReportView> {
    fun reportFire(comment: String)
    fun setImageUri(uri: Uri)
}

interface IReportView: IBaseView {
    fun onImageUploaded(reportId: Int)

    fun onFireReported(reportId: Int)
}