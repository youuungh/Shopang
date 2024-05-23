package com.ninezero.shopang.view.main.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ninezero.shopang.BR
import com.ninezero.shopang.R
import com.ninezero.shopang.model.Product
import javax.inject.Inject

class CartAdapter @Inject constructor() : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val cartList = mutableListOf<Product>()
    private var orderList = mutableListOf<Product>()
    private lateinit var cartListener: CartListener

    interface OnQuantityChangedListener {
        fun onQuantityChanged()
    }

    private var onQuantityChangedListener: OnQuantityChangedListener? = null

    fun setOnQuantityChangedListener(listener: OnQuantityChangedListener) {
        onQuantityChangedListener = listener
    }


    fun setCartList(list: List<Product>, listener: CartListener) {
        cartListener = listener
        cartList.clear()
        cartList.addAll(list)
        orderList = list.toMutableList()
        notifyDataSetChanged()
    }

    fun getOrderList() = orderList

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

    fun onQuantityTextChanged(
        text: CharSequence,
        product: Product,
        priceText: TextView
    ) {
        val quantity = text.toString().toIntOrNull() ?: 1
        val orderProduct = orderList.find { it.id == product.id }
        orderProduct?.let {
            it.quantity = quantity
            priceText.text = priceText.context.getString(
                R.string.price,
                (product.price * quantity).toInt()
            )
            onQuantityChangedListener?.onQuantityChanged()
        }
    }

    inner class CartViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) = with(binding) {
            setVariable(BR.adapter, this@CartAdapter)
            setVariable(BR.product, product)
            setVariable(BR.cartListener, cartListener)
        }
    }

    interface CartListener {
        fun onDeleteClick(product: Product)
    }
}