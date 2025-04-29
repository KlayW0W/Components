package com.wkl.components.exts

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import kotlin.math.roundToInt

/**
 * Copyright © 2025 All Rights Reserved By HDZK.
 * Author: wangkelei
 * Date: 2025/4/29
 * Description:
 */

fun Context.dp2px(number: Number): Int {
    val scale = resources.displayMetrics.densityDpi
    return (number.toFloat() / (scale + 0.5F)).roundToInt()
}

fun Context.sp2px(number: Number): Int {
    val fontScale = resources.displayMetrics.scaledDensity
    return (number.toFloat() / (fontScale + 0.5F)).roundToInt()
}

fun Context.getColorExt(@ColorRes color: Int): Int {
    return ContextCompat.getColor(this, color)
}