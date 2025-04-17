package com.wkl.components.utils

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle


/**
 * Copyright © 2025 All Rights Reserved By HDZK.
 * Author: wangkelei
 * Date: 2025/4/17
 * Description:
 */
object Utils {
    private var sApp: Application? = null

    fun init(app: Application?) {
        if (app == null) {
            Log.e("Utils", "app is null.")
            return
        }
        if (sApp == null) {
            sApp = app
        }
    }

    fun getApp(): Application {
        if (sApp == null) throw RuntimeException("please init app first.")
        return sApp!!
    }
}