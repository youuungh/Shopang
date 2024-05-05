package com.ninezero.shopang.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("android:imageResource")
fun setImageResource(view: ImageView, resource: Int) {
    view.setImageResource(resource)
}

@BindingAdapter("android:text")
fun setText(view: TextView, textResId: Int) {
    view.setText(textResId)
}