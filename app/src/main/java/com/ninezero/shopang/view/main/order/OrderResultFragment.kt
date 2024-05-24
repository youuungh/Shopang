package com.ninezero.shopang.view.main.order

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentOrderResultBinding
import com.ninezero.shopang.util.extension.applyWindowInsets
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderResultFragment : BaseFragment<FragmentOrderResultBinding>(
    R.layout.fragment_order_result
) {

    private val args by navArgs<OrderResultFragmentArgs>()
    private val order by lazy { args.order }

    override fun initView() = with(binding) {
        applyWindowInsets(binding)
        fragment = this@OrderResultFragment
    }

    fun navigateToOrderDetailFragment() {
        val action = OrderResultFragmentDirections.actionOrderResultFragmentToOrderDetailFragment(order)
        findNavController().navigate(action)
    }

    fun navigateToHomeFragment() {
        val action = OrderResultFragmentDirections.actionOrderResultFragmentToHomeFragment()
        findNavController().navigate(action)
    }
}