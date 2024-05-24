package com.ninezero.shopang.view.main.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.navArgs
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentOrderDetailBinding
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.doOnApplyWindowInsets
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderDetailFragment : BaseFragment<FragmentOrderDetailBinding>(
    R.layout.fragment_order_detail
) {

    private val args by navArgs<OrderDetailFragmentArgs>()
    private val orderProduct by lazy { args.order }
    private val orderDetailAdapter by lazy { OrderDetailAdapter(orderProduct.cartList) }

    override fun initView() = with(binding) {
        applyWindowInsets()
        fragment = this@OrderDetailFragment
        order = orderProduct
        adapter = orderDetailAdapter
    }

    fun onBackPressed() = closeFragment()

    private fun applyWindowInsets() = with(binding) {
        layout.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
            insetView.updatePadding(
                bottom = initialPadding.bottom + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
        }

        rvOrder.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
            insetView.updatePadding(
                bottom = initialPadding.bottom + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
        }
    }
}