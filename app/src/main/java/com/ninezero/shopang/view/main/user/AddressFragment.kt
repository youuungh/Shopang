package com.ninezero.shopang.view.main.user

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentAddressBinding
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressFragment : BaseFragment<FragmentAddressBinding>(
    R.layout.fragment_address
) {

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() = with(binding) {
        fragment = this@AddressFragment
        scrollView.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyBoard(v)
            }
            true
        }
    }

    private fun hideKeyBoard(view: View?) {
        view?.let {
            val imm = requireContext().getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
            binding.detail.clearFocus()
        }
    }

    fun onBackPressed() = closeFragment()
}