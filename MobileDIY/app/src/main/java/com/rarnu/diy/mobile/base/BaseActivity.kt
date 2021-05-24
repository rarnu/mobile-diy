package com.rarnu.diy.mobile.base

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun getResources(): Resources =
        createConfigurationContext(super.getResources().configuration.apply {
            fontScale = 1f
        }).resources

}