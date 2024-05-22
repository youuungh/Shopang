package com.ninezero.shopang.view.main.cart

import android.app.Dialog
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentCartBinding
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.LOADING
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.doOnApplyWindowInsets
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>(
    R.layout.fragment_cart
) {
    private val cartViewModel by viewModels<CartViewModel>()
    private val cartList = mutableListOf<Product>()

    @Inject
    lateinit var cartAdapter: CartAdapter

    @Inject
    @Named(LOADING)
    lateinit var loading: Dialog

    override fun initView() = with(binding) {
        applySystemWindowInsets(root)
        applyBottomInsets()
        fragment = this@CartFragment
        adapter = cartAdapter
    }

    override fun initViewModel() {
        cartViewModel.getUserCarts()
        observeListener()
    }

    private fun observeListener() {
        cartViewModel.cartProductsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    loading.hide()
                }

                is ResponseWrapper.Error -> {
                    loading.hide()
                    binding.root.showSnack(getString(R.string.error_msg), anchor = binding.anchor)
                }

                is ResponseWrapper.Loading -> loading.show()
                is ResponseWrapper.Idle -> {}
            }
        }
    }

    private fun applyBottomInsets() = with(binding) {
        getOrderContainer.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
            insetView.updatePadding(
                bottom = initialPadding.bottom + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
        }
    }
}