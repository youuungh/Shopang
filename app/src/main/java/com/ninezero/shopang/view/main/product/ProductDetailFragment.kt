package com.ninezero.shopang.view.main.product

import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentProductDetailBinding
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.view.BaseFragment
import com.ninezero.shopang.view.main.home.HomeViewModel

class ProductDetailFragment : BaseFragment<FragmentProductDetailBinding>(
    R.layout.fragment_product_detail
) {
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val args by navArgs<ProductDetailFragmentArgs>()
    private val productData by lazy { args.product }

    override fun initView() = with(binding) {
        fragment = this@ProductDetailFragment
        product = productData
    }

    override fun initViewModel() {
        observeListener()
    }

    private fun observeListener() {
        homeViewModel.cartProductsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    binding.root.showSnack(getString(R.string.success_add_to_cart))
                    homeViewModel.setCartProductValue()
                }

                is ResponseWrapper.Error -> {
                    binding.root.showSnack(it.msg!!)
                }

                else -> {}
            }
        }
    }

    fun addProductToCart() {

    }

    fun onBackPressed() = closeFragment()
}