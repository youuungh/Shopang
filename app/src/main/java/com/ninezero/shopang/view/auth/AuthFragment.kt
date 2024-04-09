package com.ninezero.shopang.view.auth

import androidx.navigation.fragment.findNavController
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentAuthBinding
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : BaseFragment<FragmentAuthBinding>(
    R.layout.fragment_auth
) {

    override fun initView() {
        binding.fragment = this@AuthFragment
    }

    fun navigateToPhoneAuthFragment() {
        val action = AuthFragmentDirections.actionAuthFragmentToPhoneAuthFragment()
        findNavController().navigate(action)
    }

}