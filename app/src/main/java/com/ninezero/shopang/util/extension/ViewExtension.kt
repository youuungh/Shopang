package com.ninezero.shopang.util.extension

import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

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

fun ImageView.loadImageFromUrl(url: String) {
    Glide.with(context)
        .load(url)
        .into(this)
}

fun ImageView.loadImageFromResource(resId: Int) {
    Glide.with(context)
        .load(resId)
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