package com.wkl.components.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * Copyright © 2025 All Rights Reserved By HDZK.
 * Author: wangkelei
 * Date: 2025/4/17
 * Description:
 */
open class BaseActivity() : AppCompatActivity() {
    protected val TAG by lazy { javaClass.simpleName }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData(savedInstanceState)
        setListener()
    }

    open fun initData(savedInstanceState: Bundle?) {

    }

    open fun setListener() {

    }
}