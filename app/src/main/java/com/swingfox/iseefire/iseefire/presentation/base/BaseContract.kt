package com.swingfox.iseefire.iseefire.presentation.base

interface IBasePresenter<T: IBaseView> {
    fun attachView(view: T)

    fun detachView()
}

interface IBaseView {
    fun onError(error: String)
}