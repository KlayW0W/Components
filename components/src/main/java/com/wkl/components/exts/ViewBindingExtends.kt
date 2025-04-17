package com.wkl.components.exts

import android.app.Activity
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * Copyright © 2025 All Rights Reserved By HDZK.
 * Author: wangkelei
 * Date: 2025/4/17
 * Description:
 */

fun <V : ViewBinding> AppCompatActivity.viewBinding(
    viewInflater: (LayoutInflater) -> V
): ReadOnlyProperty<AppCompatActivity, V> = ActivityViewBindingProperty(viewInflater)

private class ActivityViewBindingProperty<V : ViewBinding>(
    private val viewInflater: (LayoutInflater) -> V
) : ReadOnlyProperty<AppCompatActivity, V> {
    private var binding: V? = null


    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): V {
        return binding ?: viewInflater(thisRef.layoutInflater).also {
            thisRef.enableEdgeToEdge()
            thisRef.setContentView(it.root)
            binding = it
        }
    }
}

fun <V : ViewBinding> Fragment.viewBinding(viewBinder: (View) -> V)
        : ReadOnlyProperty<Fragment, V> = FragmentViewBindingProperty(viewBinder)

private class FragmentViewBindingProperty<V : ViewBinding>(private val viewBinder: (View) -> V) :
    ReadOnlyProperty<Fragment, V> {

    private var binding: V? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): V {
        return binding ?: run {
            thisRef.viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    // Fragment.viewLifecycleOwner call LifecycleObserver.onDestroy() before Fragment.onDestroyView().
                    // That's why we need to postpone reset of the viewBinding
                    Handler(Looper.getMainLooper()).post { binding = null }
                    thisRef.viewLifecycleOwner.lifecycle.removeObserver(this)
                }
            })
            val view = thisRef.requireView()
            viewBinder(view).also { binding = it }
        }
    }
}
