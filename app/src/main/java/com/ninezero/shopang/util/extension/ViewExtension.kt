package com.ninezero.shopang.util.extension

import android.graphics.Color
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.core.content.getSystemService
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
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

fun View.doOnApplyWindowInsets(
    windowInsetsListener: (
        insetView: View,
        windowInsets: WindowInsetsCompat,
        initialPadding: Insets,
        initialMargins: Insets
    ) -> Unit
) {
    val initialPadding = Insets.of(paddingLeft, paddingTop, paddingRight, paddingBottom)
    val initialMargins = Insets.of(marginLeft, marginTop, marginRight, marginBottom)

    ViewCompat.setOnApplyWindowInsetsListener(this) { insetView, windowInsets ->
        windowInsets.also {
            windowInsetsListener(insetView, windowInsets, initialPadding, initialMargins)
        }
    }

    addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View) {
            v.requestApplyInsets()
        }

        override fun onViewDetachedFromWindow(v: View) = Unit
    })

    if (isAttachedToWindow) {
        requestApplyInsets()
    }
}