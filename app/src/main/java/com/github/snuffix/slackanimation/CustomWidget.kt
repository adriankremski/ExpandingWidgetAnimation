package com.github.snuffix.slackanimation

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import androidx.core.graphics.drawable.DrawableCompat
import androidx.dynamicanimation.animation.SpringForce
import kotlinx.android.synthetic.main.custom_widget.view.*

class CustomWidget @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    interface OpenCloseListener {
        fun onOpened(customWidget: CustomWidget)
        fun onClosed(customWidget: CustomWidget)
    }

    enum class ExpandState {
        COLLAPSED,
        EXPANDED,
        COLLAPSED_2 // couldn't come up with something more original than ;)
    }

    private var state = ExpandState.COLLAPSED
    private var backgroundInitialDrawable: Drawable? = null

    var openCloseListener: OpenCloseListener? = null
    var isExpandable: Boolean = true

    init {
        View.inflate(context, R.layout.custom_widget, this)
        headerContainer.setCameraDistance(8000)
        backgroundInitialDrawable = headerContainer.background

        headerContainer.setOnClickListener {
            when (state) {
                ExpandState.COLLAPSED -> {
                    if (!isExpandable) {
                        return@setOnClickListener
                    }

                    openCloseListener?.onOpened(this)
                    rotateIcon(180f)
                    animateColors(isExpanded = true)
                    expand()
                }
                ExpandState.EXPANDED -> {
                    openCloseListener?.onClosed(this)
                    rotateIcon(360f)
                    collapse()
                }
                ExpandState.COLLAPSED_2 -> {
                    dropDownIcon.rotation = 0f
                    animateColors(isExpanded = false)
                    rotateHeader()
                }
            }

            state = state.next()
        }
    }

    fun headerHeight() = 60.dp + 8.dp

    private fun expand() {
        animateViewSize(widgetScrollView, 0.dp, 400.dp)
//        ExpandCollapseHeightInterpolator(widgetScrollView, 400.dp).apply {
//            attachToSpring {
//                spring.stiffness = SpringForce.STIFFNESS_MEDIUM
//                spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
//            }
//        }.start(true)
    }

    private fun collapse() {
//        animateViewSize(widgetScrollView, 400.dp, 0.dp)
        ExpandCollapseHeightInterpolator(widgetScrollView).apply {
            attachToSpring {
                spring.stiffness = SpringForce.STIFFNESS_MEDIUM
                spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
            }
        }.start(false)
    }

    private fun rotateHeader() {
        headerContainer.animate().withLayer()
            .rotationY(90f)
            .setDuration(150)
            .withEndAction {
                headerContainer.rotationY = -90f
                headerContainer.animate().withLayer()
                    .rotationY(0f)
                    .setDuration(150)
                    .start()
            }
    }

    private fun rotateIcon(rotation: Float) {
        dropDownIcon.animate().apply {
            duration = 200
            rotation(rotation)
        }
    }

    private fun animateColors(isExpanded: Boolean) {
        val startForegroundColor =
            if (isExpanded) context.getColorCompat(R.color.black) else context.getColorCompat(R.color.white)
        val endForegroundColor =
            if (isExpanded) context.getColorCompat(R.color.white) else context.getColorCompat(R.color.black)

        val startBackgroundColor =
            if (isExpanded) context.getColorCompat(R.color.grey) else context.getColorCompat(R.color.blue)
        val endBackgroundColor =
            if (isExpanded) context.getColorCompat(R.color.blue) else context.getColorCompat(R.color.grey)

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 200
        animator.addUpdateListener { animator ->
            val ratio = animator.animatedValue as Float
            val foregroundColor = blendColors(startForegroundColor, endForegroundColor, ratio)
            val backgroundColor = blendColors(startBackgroundColor, endBackgroundColor, ratio)

            label1.setTextColor(foregroundColor)
            label2.setTextColor(foregroundColor)
            dropDownIcon.setColorFilter(foregroundColor)
            setBackgroundDrawableColor(backgroundColor)
        }
        animator.start()
    }

    private fun setBackgroundDrawableColor(color: Int) {
        backgroundInitialDrawable?.constantState?.newDrawable()?.let {
            val drawable = DrawableCompat.wrap(it)
            DrawableCompat.setTint(drawable, color)
            drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            headerContainer.background = drawable
        }
    }

    private fun animateViewSize(
        view: View, startHeight: Int, endHeight: Int,
        interpolator: Interpolator = OvershootInterpolator(1f)
    ) {
        val valueAnimator = ValueAnimator.ofInt(startHeight, endHeight)
        valueAnimator.duration = 300L
        valueAnimator.interpolator = interpolator
        valueAnimator.addUpdateListener {
            val animatedValue = valueAnimator.animatedValue as Int
            val layoutParams = view.layoutParams
            layoutParams.height = animatedValue
            view.layoutParams = layoutParams
        }
        valueAnimator.start()
    }
}
