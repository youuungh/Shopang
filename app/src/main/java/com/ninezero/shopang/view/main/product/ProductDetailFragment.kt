package com.ninezero.shopang.view.main.product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.transition.MaterialSharedAxis
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentProductDetailBinding
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.doOnApplyWindowInsets
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.view.main.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val args by navArgs<ProductDetailFragmentArgs>()
    private val productData by lazy { args.product }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_detail, container, false)
        binding.apply {
            fragment = this@ProductDetailFragment
            product = productData
            return this.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).addTarget(view)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true).addTarget(view)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, /* forward= */ false).addTarget(view)
        applyWindowInsets()
        observeListener()
    }

    private fun observeListener() {
        homeViewModel.wishProductLiveData(productData.id).observe(viewLifecycleOwner) {
            binding.wish.apply {
                if (it != null) {
                    setImageResource(R.drawable.ic_wish_fill)
                    binding.root.showSnack(getString(R.string.success_add_to_wish))
                } else {
                    setImageResource(R.drawable.ic_wish)
                    binding.root.showSnack(getString(R.string.success_delete_from_wish))
                }
            }
        }
        homeViewModel.cartProductsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    binding.root.showSnack(getString(R.string.success_add_to_cart))
                    homeViewModel.setCartProductsIdle()
                }

                is ResponseWrapper.Error -> {
                    binding.root.showSnack(it.msg!!)
                }

                else -> {}
            }
        }
    }

    fun toggleProductInWishlist() {
        homeViewModel.toggleProductInWishlist(productData)
    }

    fun addProductToCart() {
        val updatedProduct = createUpdatedProduct()
        homeViewModel.addProductToCart(updatedProduct)
    }

    private fun createUpdatedProduct(): Product {
        val quantity = binding.quantity.text.toString().trim().toInt()
        return productData.copy(quantity = quantity)
    }

    fun changeProductQuantity(isPlus: Boolean) {
        var quantity = binding.quantity.text.toString().toInt()

        if (isPlus && quantity < 10) {
            quantity++
        } else if (!isPlus && quantity > 1) {
            quantity--
        }

        binding.quantity.text = quantity.toString()

        if (quantity == 10) {
            binding.root.showSnack(getString(R.string.error_max_quantity_reached), anchor = binding.anchor)
        }
    }

    private fun applyWindowInsets() = with(binding) {
        getOrderContainer.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
            insetView.updatePadding(
                bottom = initialPadding.bottom + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
        }

        scrollView.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
            insetView.updatePadding(
                bottom = initialPadding.bottom + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
        }
    }

    fun onBackPressed() = closeFragment()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}