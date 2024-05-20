package com.ninezero.shopang.util.extension

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding

fun Fragment.closeFragment() {
    findNavController().popBackStack()
}

fun Fragment.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), msg, length).show()
}

fun Fragment.showToast(@StringRes resId: Int, length: Int = Toast.LENGTH_SHORT) {
    showToast(getString(resId), length)
}

fun applyWindowInsets(binding: ViewBinding) {
    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
        v.updatePadding(top = insets.top, bottom = insets.bottom)
        windowInsets
    }
}