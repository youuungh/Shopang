package com.ninezero.shopang.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.ninezero.shopang.R
import com.ninezero.shopang.util.extension.loadImageFromResource
import com.ninezero.shopang.util.extension.loadImageFromUrl

@BindingAdapter("imageResource")
fun setImageResource(view: ImageView, resource: Int) {
    view.setImageResource(resource)
}

@BindingAdapter("text")
fun setText(view: TextView, textResId: Int) {
    view.setText(textResId)
}

@BindingAdapter("imageUrl")
fun ImageView.setImageFromUrl(url: String?) {
    if (!url.isNullOrEmpty())
        loadImageFromUrl(url)
}