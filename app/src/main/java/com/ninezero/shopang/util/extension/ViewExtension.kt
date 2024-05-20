package com.ninezero.shopang.util.extension

import android.graphics.Color
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import com.google.android.material.snackbar.Snackbar
import com.ninezero.shopang.di.GlideApp

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.showSnack(msg: String, length: Int = Snackbar.LENGTH_SHORT, anchor: View? = null) {
    val snackbar = Snackbar.make(this, msg, length)
    anchor?.let {
        snackbar.setAnchorView(it)
    }
    snackbar.show()
}

fun View.showSnack(@StringRes resId: Int, length: Int = Snackbar.LENGTH_SHORT, anchor: View? = null) {
    val snackbar = Snackbar.make(this, resId, length)
    anchor?.let {
        snackbar.setAnchorView(it)
    }
    snackbar.show()
}

fun Int.isDarkColor(): Boolean {
    val darkness = 1 - (0.299 * Color.red(this) + 0.587 * Color.green(this) + 0.114 * Color.blue(this)) / 255
    return darkness >= 0.5
}

fun Int.setTintColor(reverse: Boolean = false): Int = if (this.isDarkColor() xor reverse) Color.WHITE else Color.BLACK

fun ImageView.loadImageFromUrl(url: String) {
    GlideApp.with(context)
        .load(url)
        .into(this)
}

fun ImageView.loadImageFromResource(resId: Int) {
    GlideApp.with(context)
        .load(resId)
        .into(this)
}

fun ImageView.loadGifFromResource(gifResId: Int) {
    GlideApp.with(this.context)
        .asGif()
        .load(gifResId)
        .into(this)
}

fun View.showKeyBoard() {
    fun View.showKeyBoardNow() {
        if (isFocused) {
            post {
                val imm = context.getSystemService<InputMethodManager>()
                imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    }
    requestFocus()

    if (hasWindowFocus()) {
        showKeyBoardNow()
    } else {
        viewTreeObserver.addOnWindowFocusChangeListener {
            object : ViewTreeObserver.OnWindowFocusChangeListener {
                override fun onWindowFocusChanged(hasFocus: Boolean) {
                    if (hasFocus) {
                        this@showKeyBoard.showKeyBoardNow()
                        viewTreeObserver.removeOnWindowFocusChangeListener(this)
                    }
                }

            }
        }
    }
}