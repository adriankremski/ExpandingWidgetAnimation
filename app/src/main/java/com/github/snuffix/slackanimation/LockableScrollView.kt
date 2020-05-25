package com.github.snuffix.slackanimation

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ScrollView

class LockableScrollView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ScrollView(context, attrs, defStyleAttr) {

    var scrollable = true

    override fun onTouchEvent(event: MotionEvent) = when (event.action) {
        MotionEvent.ACTION_DOWN -> {
            if (scrollable) {
                super.onTouchEvent(event)
            } else {
                scrollable
            }
        }
        else -> {
            super.onTouchEvent(event)
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent?) = if (!scrollable) {
        false
    } else {
        super.onInterceptTouchEvent(event)
    }
}