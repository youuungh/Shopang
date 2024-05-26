package com.ninezero.shopang.view.main.product

import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentAllProductBinding
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.doOnApplyWindowInsets
import com.ninezero.shopang.view.BaseFragment
import com.ninezero.shopang.view.main.adapter.AllProductAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AllProductFragment : BaseFragment<FragmentAllProductBinding>(
    R.layout.fragment_all_product
), AllProductAdapter.OnProductClickListener {

    private val args by navArgs<AllProductFragmentArgs>()
    private val allProductAdapter by lazy { AllProductAdapter(args.productList.toList(), this) }

    override fun initView() = with(binding) {
        applyInsets()
        fragment = this@AllProductFragment
        adapter = allProductAdapter
    }

    private fun applyInsets() = with(binding) {
        root.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
            insetView.updatePadding(
                top = initialPadding.top + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            )
        }

        rvAllProduct.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
            insetView.updatePadding(
                bottom = initialPadding.bottom + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
        }
    }

    fun onBackPressed() = closeFragment()

    override fun onProductClick(product: Product) {
        val action = AllProductFragmentDirections.actionAllProductFragmentToProductDetailFragment(product)
        findNavController().navigate(action)
    }
}