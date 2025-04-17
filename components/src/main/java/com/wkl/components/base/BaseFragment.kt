package com.wkl.components.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

/**
 * Copyright © 2025 All Rights Reserved By HDZK.
 * Author: wangkelei
 * Date: 2025/4/17
 * Description:
 */
open class BaseFragment : Fragment() {
    protected val TAG by lazy { javaClass.simpleName }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData(savedInstanceState)
        setListener()
    }

    open fun initData(savedInstanceState: Bundle?) {

    }

    open fun setListener() {

    }

    open fun finish() {
        activity?.finish()
    }
}