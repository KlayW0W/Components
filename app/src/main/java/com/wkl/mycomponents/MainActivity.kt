package com.wkl.mycomponents

import android.os.Bundle
import com.gyf.immersionbar.ImmersionBar
import com.wkl.components.base.BaseActivity
import com.wkl.components.exts.viewBinding
import com.wkl.mycomponents.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).titleBar(binding.toolbar)
            .statusBarDarkFont(true).init()
    }
}