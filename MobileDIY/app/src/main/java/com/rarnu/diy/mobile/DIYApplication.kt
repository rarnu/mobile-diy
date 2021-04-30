package com.rarnu.diy.mobile

import android.annotation.SuppressLint
import android.content.Context
import org.chromium.base.ApplicationStatus
import org.xwalk.core.XWalkApplication
import java.lang.Exception

class DIYApplication: XWalkApplication() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var ctx: Context
        fun getContext(): Context = ctx
    }
    override fun onCreate() {
        super.onCreate()
        try {
            ctx = applicationContext
        } catch (e: Exception) {

        }
        ApplicationStatus.initialize(this)
    }

}