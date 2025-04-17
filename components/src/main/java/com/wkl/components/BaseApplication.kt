package com.wkl.components

import android.app.Application
import com.wkl.components.utils.Utils

/**
 * Copyright © 2025 All Rights Reserved By HDZK.
 * Author: wangkelei
 * Date: 2025/4/17
 * Description:
 */
open class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}