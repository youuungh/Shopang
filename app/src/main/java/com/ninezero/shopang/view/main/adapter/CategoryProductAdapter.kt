package com.ninezero.shopang.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ninezero.shopang.BR
import com.ninezero.shopang.R
import com.ninezero.shopang.model.Product

class CategoryProductAdapter(
    private val productList: List<Product>,
    private val productClickListener: OnProductClickListener
) : RecyclerView.Adapter<CategoryProductAdapter.CategoryProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryProductViewHolder =
        CategoryProductViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_category_product,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: CategoryProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    inner class CategoryProductViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) = with(binding) {
            setVariable(BR.product, product)
            setVariable(BR.onProductClick, productClickListener)
        }
    }

    interface OnProductClickListener {
        fun onProductClick(product: Product)
    }
}