package com.github.snuffix.slackanimation

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat

fun blendColors(from: Int, to: Int, ratio: Float): Int {
    val inverseRatio = 1f - ratio

    val r = Color.red(to) * ratio + Color.red(from) * inverseRatio
    val g = Color.green(to) * ratio + Color.green(from) * inverseRatio
    val b = Color.blue(to) * ratio + Color.blue(from) * inverseRatio

    return Color.rgb(r.toInt(), g.toInt(), b.toInt())
}

fun Context.getColorCompat(resourceId: Int) = ContextCompat.getColor(this, resourceId)

val screenDensity: Float
    get() = Resources.getSystem().displayMetrics.density

val Int.dp: Int
    get() = (this * screenDensity).toInt()

fun View.setCameraDistance(distance: Int) {
    val scale = resources.displayMetrics.density
    cameraDistance = distance * scale
}

inline fun <reified T: Enum<T>> T.next(): T {
    val values = enumValues<T>()
    val nextOrdinal = (ordinal + 1) % values.size
    return values[nextOrdinal]
}