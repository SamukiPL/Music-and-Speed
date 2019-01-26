package me.samuki.musicandspeed.helpers

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager


object KeyboardHelper {

    fun showSoftKeyboard(context: Context, view: View?) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        if (imm != null && view != null) {
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    fun hideSoftKeyboard(context: Context, view: View?) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
            if (imm != null && view != null)
                imm.hideSoftInputFromWindow(view.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}