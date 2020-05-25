package com.github.snuffix.slackanimation

import android.view.View
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation

class ExpandCollapseHeightInterpolator(view: View, private val expandedHeight: Int, var onEnd: () -> Unit = {}) : FloatPropertyCompat<View>("ExpandCollapseHeightInterpolator") {

    private val spring = SpringAnimation(view, this, 0.toFloat()).apply {
        addEndListener { _, _, _, _ ->
            onEnd()
        }
    }

    fun start(expanded: Boolean) {
        if (expanded) {
            spring.animateToFinalPosition(expandedHeight.toFloat())
        } else {
            spring.animateToFinalPosition(0f)
        }
    }

    fun attachToSpring(options: (SpringAnimation.() -> Unit)?) {
        options?.invoke(spring)
    }

    override fun getValue(view: View): Float = view.height.toFloat()

    override fun setValue(view: View, value: Float) = view.run {
        layoutParams.width = view.layoutParams.width
        layoutParams.height = value.toInt()
        requestLayout()
        invalidate()
    }
}

