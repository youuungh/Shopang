package com.ninezero.shopang.view.main.order

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentOrderBinding
import com.ninezero.shopang.model.Order
import com.ninezero.shopang.util.LOADING
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class OrderFragment : BaseFragment<FragmentOrderBinding>(
    R.layout.fragment_order
), OrderAdapter.OnOrderClickListener {

    private val orderViewModel by viewModels<OrderViewModel>()

    @Inject
    lateinit var orderAdapter: OrderAdapter

    @Inject
    @Named(LOADING)
    lateinit var loading: Dialog

    override fun initView() = with(binding) {
        fragment = this@OrderFragment
        adapter = orderAdapter
    }

    override fun initViewModel() {
        observeListener()
    }

    private fun observeListener() {
        orderViewModel.ordersLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    loading.hide()
                }

                is ResponseWrapper.Error -> {
                    loading.hide()
                    binding.root.showSnack(getString(R.string.error_msg))
                }

                is ResponseWrapper.Loading -> loading.show()
                is ResponseWrapper.Idle -> {}
            }
        }
    }

    fun onBackPressed() = closeFragment()

    override fun onOrderClicked(order: Order) {
        navigateToOrderDetailFragment(order)
    }

    private fun navigateToOrderDetailFragment(order: Order) {
        val action = OrderFragmentDirections.actionOrderFragmentToOrderDetailFragment(order)
        findNavController().navigate(action)
    }
}