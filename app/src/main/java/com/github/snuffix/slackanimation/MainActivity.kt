package com.github.snuffix.slackanimation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val widgetOpenCloseListener = object : CustomWidget.OpenCloseListener {
        var scrollViewYInitialPosition: Int = 0

        private fun widgets() = mainContainer.children.filterIsInstance<CustomWidget>()

        override fun onOpened(customWidget: CustomWidget) {
            widgets().filterNot { it == customWidget }.forEach { it.isExpandable = false }

            scrollViewYInitialPosition = mainScrollView.scrollY
            mainScrollView.scrollable = false
            mainScrollView.smoothScrollTo(0, customWidget.bottom - customWidget.headerHeight())
        }

        override fun onClosed(customWidget: CustomWidget) {
            widgets().forEach { it.isExpandable = true }

            mainScrollView.scrollable = true
            mainScrollView.smoothScrollTo(0, scrollViewYInitialPosition)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainContainer.children.filterIsInstance<CustomWidget>().forEach {
            it.openCloseListener = widgetOpenCloseListener
        }
    }
}
