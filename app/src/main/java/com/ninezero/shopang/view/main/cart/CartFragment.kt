package com.ninezero.shopang.view.main.cart

import android.app.Dialog
import android.os.Bundle
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentCartBinding
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.LOADING
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.doOnApplyWindowInsets
import com.ninezero.shopang.util.extension.hide
import com.ninezero.shopang.util.extension.show
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class CartFragment : BaseFragment<FragmentCartBinding>(
    R.layout.fragment_cart
), CartAdapter.CartListener {
    private val cartViewModel by viewModels<CartViewModel>()
    private val cartList = mutableListOf<Product>()

    @Inject
    lateinit var cartAdapter: CartAdapter

    @Inject
    @Named(LOADING)
    lateinit var loading: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cartViewModel.getUserCarts()
    }

    override fun initView() = with(binding) {
        applyInsets()
        fragment = this@CartFragment
        adapter = cartAdapter
        cartAdapter.setOnQuantityChangedListener(object : CartAdapter.OnQuantityChangedListener {
            override fun onQuantityChanged() {
                updateTotalPrice()
            }
        })
    }

    override fun initViewModel() {
        observeListener()
    }

    private fun observeListener() {
        cartViewModel.cartProductsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    if (it.data.isNullOrEmpty()) {
                        binding.emptyContainer.show()
                        binding.getOrderContainer.hide()
                        binding.rvCart.hide()
                    } else {
                        cartAdapter.setCartList(it.data, this)
                        updateTotalPrice()
                        binding.emptyContainer.hide()
                        binding.getOrderContainer.show()
                        binding.rvCart.show()
                    }
                    loading.hide()
                }

                is ResponseWrapper.Error -> {
                    loading.hide()
                    binding.root.showSnack(
                        getString(R.string.error_msg),
                        anchor = binding.anchor
                    )
                }

                is ResponseWrapper.Loading -> loading.show()
                is ResponseWrapper.Idle -> {}
            }
        }
    }

    fun startPayment() {
        val totalPrice = getTotalPrice()
        openPaymentBottomSheet(totalPrice)
    }

    private fun updateTotalPrice() {
        val totalPrice = getTotalPrice()
        binding.totalPrice.text = getString(R.string.price, totalPrice)
    }

    private fun getTotalPrice(): Int {
        val orderList = cartAdapter.getOrderList()
        return orderList.sumOf { product -> product.price * product.quantity }.toInt()
    }

    private fun openPaymentBottomSheet(totalPrice: Int) {
        cartList.clear()
        cartList.addAll(cartAdapter.getOrderList())
        val action = CartFragmentDirections.actionCartFragmentToPaymentFragment(
            totalPrice,
            cartList.toTypedArray()
        )
        findNavController().navigate(action)
    }

    private fun applyInsets() = with(binding) {
        root.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
            insetView.updatePadding(
                top = initialPadding.top + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            )
        }

        rvCart.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
            insetView.updatePadding(
                bottom = initialPadding.bottom + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
        }
    }

    override fun onDeleteClick(product: Product) {
        cartViewModel.deleteProduct(product)
    }
}