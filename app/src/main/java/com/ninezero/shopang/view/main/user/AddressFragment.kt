package com.ninezero.shopang.view.main.user

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentAddressBinding
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.view.BaseFragment
import com.ninezero.shopang.view.auth.UserInfoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressFragment : BaseFragment<FragmentAddressBinding>(
    R.layout.fragment_address
) {
    private val userInfoViewModel by activityViewModels<UserInfoViewModel>()
    private var tempAddress: String? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        with(binding) {
            fragment = this@AddressFragment
            scrollView.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    hideKeyBoard(v)
                }
                true
            }
        }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("data")
            ?.observe(viewLifecycleOwner) {
                tempAddress = it
            }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("postcode")
            ?.observe(viewLifecycleOwner) {
                binding.postcode.setText(it)
            }

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<String>("address")
            ?.observe(viewLifecycleOwner) {
                binding.address.setText(it)
            }
    }

    fun setAddress() = with(binding) {
        val postcode = postcode.text.toString().trim()
        if (postcode.isEmpty()) {
            root.showSnack(getString(R.string.chk_empty_postcode_and_address), anchor = anchor)
            return@with
        }

        val detailAddress = detail.text.toString()
        val userAddress = if (tempAddress != null) {
            if (detailAddress.isNotEmpty()) {
                "$tempAddress, $detailAddress"
            } else {
                tempAddress
            }
        } else {
            null
        }

        userAddress?.let {
            userInfoViewModel.updateUserAddress(it)
            closeFragment()
        }
    }

    private fun hideKeyBoard(view: View?) {
        view?.let {
            val imm = requireContext().getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
            binding.detail.clearFocus()
        }
    }

    fun openAddressWebView() {
        val action = AddressFragmentDirections.actionAddressFragmentToAddressWebView()
        findNavController().navigate(action)
    }

    fun onBackPressed() = closeFragment()
}