package com.wkl.components.exts

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.annotation.ColorInt

/**
 * Copyright © 2025 All Rights Reserved By HDZK.
 * Author: wangkelei
 * Date: 2025/4/29
 * Description:
 */
fun View.round(
    radiusDp: Float = 0F,
    strokeSizeDp: Float = 0F,
    @ColorInt strokeColor: Int = Color.TRANSPARENT,
    @ColorInt bgColor: Int = Color.TRANSPARENT
) {
    val gradientDrawable = GradientDrawable()
    gradientDrawable.cornerRadius = context.dp2px(radiusDp).toFloat()
    gradientDrawable.setColor(bgColor)
    if (strokeSizeDp != 0F)
        gradientDrawable.setStroke(context.dp2px(strokeSizeDp), strokeColor)
    background = gradientDrawable
}

fun View.round(
    topLeftRadiusDp: Float = 0F,
    topRightRadiusDp: Float = 0F,
    bottomRightRadiusDp: Float = 0F,
    bottomLeftRadiusDp: Float = 0F,
    @ColorInt bgColor: Int = Color.TRANSPARENT
) {
    val gradientDrawable = GradientDrawable()
    gradientDrawable.cornerRadii = floatArrayOf(
        context.dp2px(topLeftRadiusDp).toFloat(),
        context.dp2px(topLeftRadiusDp).toFloat(),
        context.dp2px(topRightRadiusDp).toFloat(),
        context.dp2px(topRightRadiusDp).toFloat(),
        context.dp2px(bottomRightRadiusDp).toFloat(),
        context.dp2px(bottomRightRadiusDp).toFloat(),
        context.dp2px(bottomLeftRadiusDp).toFloat(),
        context.dp2px(bottomLeftRadiusDp).toFloat(),
    )
    gradientDrawable.setColor(bgColor)
    background = gradientDrawable
}