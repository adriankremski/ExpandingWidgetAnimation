package com.github.snuffix.slackanimation

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

class CustomWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {


    init {
        View.inflate(context, R.layout.custom_widget, this)
    }
}
