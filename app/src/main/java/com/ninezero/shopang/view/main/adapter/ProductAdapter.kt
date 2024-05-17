package com.ninezero.shopang.view.main.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ninezero.shopang.BR
import com.ninezero.shopang.R
import com.ninezero.shopang.model.Product

class ProductAdapter(
    private val productList: List<Product>,
    private val onProductClick: OnProductClickListener
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_product,
            parent,
            false
        )
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductAdapter.ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    inner class ProductViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) = with(binding) {
            setVariable(BR.product, product)
            setVariable(BR.onProductClick, onProductClick)
            executePendingBindings()
        }
    }

    interface OnProductClickListener {
        fun onProductClick(product: Product)
    }
}