package com.github.snuffix.slackanimation

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    enum class ExpandState {
        COLLAPSED,
        EXPANDED,
        COLLAPSED_2
    }

    private var state = ExpandState.COLLAPSED
    private var backgroundInitialDrawable: Drawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scale = resources.displayMetrics.density
        val distance = 8000
        container.cameraDistance = distance * scale
        backgroundInitialDrawable = container.background

        val headerTopMargin = resources.getDimensionPixelSize(R.dimen.headerTopMargin)

        container.setOnClickListener {

            when (state) {
                ExpandState.COLLAPSED -> {
                    dropDownIcon.animate().apply {
                        duration = 200
                        rotation(180f)
                    }
                    animateColors(isExpanded = true)

                    state = ExpandState.EXPANDED

                    header.animate()
                        .yBy(-header.height.toFloat() - headerTopMargin)
                        .setDuration(300)
                        .setInterpolator(OvershootInterpolator(1f))
                        .start()

                    mainContainer.animate()
                        .yBy(-header.height.toFloat() - headerTopMargin)
                        .setDuration(300)
                        .setInterpolator(OvershootInterpolator(1f))
                        .start()

                    mainScrollView.scrollable = false

                    widgetScrollView.visibility = View.VISIBLE
                    animateViewSize(widgetScrollView, 0, 400.dp)

                }
                ExpandState.EXPANDED -> {
                    mainScrollView.scrollable = true

                    dropDownIcon.animate().apply {
                        duration = 200
                        rotation(360f)
                    }

                    header.animate()
                        .yBy(header.height.toFloat() + headerTopMargin)
                        .setDuration(300)
                        .setInterpolator(OvershootInterpolator(1f))
                        .start()

                    mainContainer.animate()
                        .yBy(header.height.toFloat() + headerTopMargin)
                        .setDuration(300)
                        .setInterpolator(OvershootInterpolator(1f))
                        .start()

                    state = ExpandState.COLLAPSED_2
                    animateViewSize(widgetScrollView, 400.dp, 0, animateAlpha = true)

                }
                ExpandState.COLLAPSED_2 -> {
                    dropDownIcon.rotation = 0f
                    container.animate().withLayer()
                        .rotationY(90f)
                        .setDuration(150)
                        .withEndAction {
                            container.setRotationY(-90f);
                            container.animate().withLayer()
                                .rotationY(0f)
                                .setDuration(150)
                                .start();
                        }

                    animateColors(isExpanded = false)



                    state = ExpandState.COLLAPSED
                }
            }
        }

    }


    class CustomAccelerateDecelerateInterpolator : Interpolator {
        override fun getInterpolation(value: Float): Float {
            val sqt = value * value
            return sqt / (2.0f * (sqt - value) + 1.0f)
        }
    }

    private fun animateColors(isExpanded: Boolean) {
        val startForegroundColor =
            if (isExpanded) getColorCompat(R.color.black) else getColorCompat(R.color.white)
        val endForegroundColor =
            if (isExpanded) getColorCompat(R.color.white) else getColorCompat(R.color.black)

        val startBackgroundColor =
            if (isExpanded) getColorCompat(R.color.grey) else getColorCompat(R.color.blue)
        val endBackgroundColor =
            if (isExpanded) getColorCompat(R.color.blue) else getColorCompat(R.color.grey)

        val animator = ValueAnimator.ofFloat(0f, 1f)
        animator.duration = 200
        animator.addUpdateListener { animator ->
            val foregroundColor = blendColors(
                startForegroundColor,
                endForegroundColor,
                animator.animatedValue as Float
            )
            label1.setTextColor(foregroundColor)
            label2.setTextColor(foregroundColor)
            dropDownIcon.setColorFilter(foregroundColor)

            backgroundInitialDrawable?.constantState?.newDrawable()?.let {
                val drawable = DrawableCompat.wrap(it)
                DrawableCompat.setTint(
                    drawable,
                    blendColors(
                        startBackgroundColor,
                        endBackgroundColor,
                        animator.animatedValue as Float
                    )
                )
                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
                container.background = drawable
            }
        }
        animator.start()
    }

    private fun animateViewSize(
        view: View, startHeight: Int, endHeight: Int,
        animateAlpha: Boolean = false,
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

            if (animateAlpha) {
                if (animatedValue - endHeight < 100) {
                    layoutParams.height = 0
                    view.layoutParams = layoutParams
                }
            }
        }
        valueAnimator.start()
    }

    private fun blendColors(from: Int, to: Int, ratio: Float): Int {
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
}
