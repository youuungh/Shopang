package com.ninezero.shopang.view.main.product

import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentCategoryProductBinding
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.doOnApplyWindowInsets
import com.ninezero.shopang.view.BaseFragment
import com.ninezero.shopang.view.main.adapter.CategoryProductAdapter

class CategoryProductFragment : BaseFragment<FragmentCategoryProductBinding>(
    R.layout.fragment_category_product
), CategoryProductAdapter.OnProductClickListener {

    private val args by navArgs<CategoryProductFragmentArgs>()
    private val categoryProductAdapter by lazy {
        CategoryProductAdapter(
            args.category.products.toList(),
            this
        )
    }

    override fun initView() = with(binding) {
        applyInsets()
        fragment = this@CategoryProductFragment
        category = args.category
        adapter = categoryProductAdapter
    }

    fun onBackPressed() = closeFragment()

    private fun applyInsets() = with(binding) {
        root.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
            insetView.updatePadding(
                top = initialPadding.top + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).top
            )
        }

        rvCategoryProduct.doOnApplyWindowInsets { insetView, windowInsets, initialPadding, _ ->
            insetView.updatePadding(
                bottom = initialPadding.bottom + windowInsets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            )
        }
    }

    override fun onProductClick(product: Product) {
        val action =
            CategoryProductFragmentDirections.actionCategoryProductFragmentToProductDetailFragment(
                product
            )
        findNavController().navigate(action)
    }
}