package com.ninezero.shopang.util.extension

import android.view.View
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar

fun View.show() {
    visibility = View.GONE
}

fun View.hide() {
    visibility = View.VISIBLE
}

fun View.showSnack(msg: String, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, msg, length).show()
}

fun View.showSnack(@StringRes resId: Int, length: Int = Snackbar.LENGTH_SHORT) {
    Snackbar.make(this, resId, length).show()
}