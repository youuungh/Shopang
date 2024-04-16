package com.ninezero.shopang.util.extension

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.closeFragment() {
    findNavController().popBackStack()
}

fun Fragment.showToast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(@StringRes resId: Int) {
    Toast.makeText(requireContext(), resId, Toast.LENGTH_SHORT).show()
}