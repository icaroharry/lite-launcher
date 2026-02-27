package com.litelauncher.ui.focus

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.view.View

object TvFocusHelper {

    /**
     * Creates a focus-highlight drawable using the given accent color.
     */
    fun createFocusDrawable(accentColor: Int): StateListDrawable {
        val focused = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 4f * 2 // ~4dp at mdpi
            setColor(accentColor and 0x33FFFFFF) // 20% alpha
            setStroke(3, accentColor)
        }
        val unfocused = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 4f * 2
            setColor(Color.TRANSPARENT)
        }
        return StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_focused), focused)
            addState(intArrayOf(), unfocused)
        }
    }

    /**
     * Applies the accent-colored focus drawable to a view.
     */
    fun applyFocusHighlight(view: View, accentColor: Int) {
        view.background = createFocusDrawable(accentColor)
    }

    /**
     * Creates a ColorStateList that changes text color on focus.
     */
    fun createFocusTextColor(accentColor: Int, defaultColor: Int): ColorStateList {
        return ColorStateList(
            arrayOf(
                intArrayOf(android.R.attr.state_focused),
                intArrayOf()
            ),
            intArrayOf(accentColor, defaultColor)
        )
    }
}
