package com.ninezero.shopang.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ninezero.shopang.BR
import com.ninezero.shopang.R
import com.ninezero.shopang.model.Product

class AllProductAdapter(
    private val productList: List<Product>,
    private val onProductClick: OnProductClickListener
): RecyclerView.Adapter<AllProductAdapter.AllProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllProductViewHolder =
        AllProductViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_all_product,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: AllProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    inner class AllProductViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) = with(binding) {
            setVariable(BR.product, product)
            setVariable(BR.onProductClick, onProductClick)
        }
    }

    interface OnProductClickListener {
        fun onProductClick(product: Product)
    }
}