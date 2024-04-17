package com.ninezero.shopang.util.extension

import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import com.google.android.material.snackbar.Snackbar

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.showSnack(msg: String, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, msg, length).show()
}

fun View.showSnack(@StringRes resId: Int, length: Int = Snackbar.LENGTH_SHORT) {
    showSnack(resId, length)
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