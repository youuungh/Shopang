package com.ninezero.shopang.util.extension

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

fun Fragment.closeFragment() {
    findNavController().popBackStack()
}