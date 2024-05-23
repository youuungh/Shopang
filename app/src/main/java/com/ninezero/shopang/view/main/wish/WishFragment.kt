package com.ninezero.shopang.view.main.wish

import android.app.Dialog
import android.os.Bundle
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentWishBinding
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
class WishFragment : BaseFragment<FragmentWishBinding>(
    R.layout.fragment_wish
), WishAdapter.OnWishClickListener {

    private val wishViewModel by viewModels<WishViewModel>()

    @Inject
    lateinit var wishAdapter: WishAdapter

    @Inject
    @Named(LOADING)
    lateinit var loading: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wishViewModel.getAllWishes()
    }

    override fun initView() = with(binding) {
        applySystemWindowInsets(root)
        applyBottomInsets()
        fragment = this@WishFragment
        adapter = wishAdapter
    }

    override fun initViewModel() {
        observeListener()
    }

    private fun observeListener() {
        wishViewModel.wishProductsLiveData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.emptyContainer.show()
                binding.addAllToCartContainer.hide()
                binding.rvWish.hide()
            } else {
                wishAdapter.setWishList(it, this)
                binding.emptyContainer.hide()
                binding.addAllToCartContainer.hide()
                binding.rvWish.show()
            }
        }
        wishViewModel.cartProductsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    binding.root.showSnack(
                        getString(R.string.success_add_to_cart),
                        anchor = binding.anchor
                    )
                    loading.hide()
                }

                is ResponseWrapper.Error -> {
                    binding.root.showSnack(
                        getString(R.string.error_msg),
                        anchor = binding.anchor
                    )
                    loading.hide()
                }

                is ResponseWrapper.Loading -> loading.show()
                is ResponseWrapper.Idle -> loading.hide()
            }
        }
    }

    fun addWishesToCart() {
        wishViewModel.addWishesToCart(wishAdapter.getWishList())
    }

    private fun applyBottomInsets() = with(binding) {
        addAllToCartContainer.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
            insetView.updatePadding(
                bottom = initialPadding.bottom + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
        }
    }

    private fun navigateToProductDetailFragment(product: Product) {
        val action = WishFragmentDirections.actionWishFragmentToProductDetailFragment(product)
        findNavController().navigate(action)
    }

    override fun onWishClick(product: Product) {
        navigateToProductDetailFragment(product)
    }

    override fun onDeleteClick(product: Product) {
        wishViewModel.deleteWish(product)
    }

    override fun onAddCartClick(product: Product) {
        wishViewModel.addWishToCart(product)
        binding.root.showSnack(
            getString(R.string.success_add_to_cart),
            anchor = binding.anchor
        )
    }
}