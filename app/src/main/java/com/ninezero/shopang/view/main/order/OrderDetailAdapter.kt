package com.ninezero.shopang.view.main.order

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ninezero.shopang.BR
import com.ninezero.shopang.R
import com.ninezero.shopang.model.Product

class OrderDetailAdapter(
    private val productList: List<Product>
) : RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailViewHolder =
        OrderDetailViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_order_product,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: OrderDetailViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    inner class OrderDetailViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) = with(binding) {
            setVariable(BR.product, product)
        }
    }
}