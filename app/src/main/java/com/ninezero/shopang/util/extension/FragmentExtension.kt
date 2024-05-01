package com.ninezero.shopang.util.extension

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.closeFragment() {
    findNavController().popBackStack()
}

fun Fragment.showToast(msg: String, length: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(requireContext(), msg, length).show()
}

fun Fragment.showToast(@StringRes resId: Int, length: Int = Toast.LENGTH_SHORT) {
    showToast(getString(resId), length)
}
