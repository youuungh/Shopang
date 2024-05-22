package com.ninezero.shopang.view.main.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ninezero.shopang.R
import com.ninezero.shopang.model.Product
import javax.inject.Inject

class CartAdapter @Inject constructor() : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val cartList = mutableListOf<Product>()
    private lateinit var cartListener: CartListener

    fun setCartList(list: List<Product>, listener: CartListener) {
        cartListener = listener
        cartList.clear()
        cartList.addAll(list)
        notifyDataSetChanged()
    }

    fun getOrderList() = cartList.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder =
        CartViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_cart,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartList[position])
    }

    override fun getItemCount(): Int = cartList.size

    inner class CartViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) = with(binding) {

        }
    }

    interface CartListener {
        fun onDeleteClick(product: Product)
    }
}