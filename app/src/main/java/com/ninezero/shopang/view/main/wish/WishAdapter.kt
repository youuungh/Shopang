package com.ninezero.shopang.view.main.wish

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ninezero.shopang.R
import com.ninezero.shopang.model.Product
import javax.inject.Inject

class WishAdapter @Inject constructor() : RecyclerView.Adapter<WishAdapter.WishViewHolder>() {

    private val wishList = mutableListOf<Product>()
    private lateinit var wishClickListener: OnWishClickListener

    fun setWishList(list: List<Product>, listener: OnWishClickListener) {
        wishClickListener = listener
        wishList.apply {
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    fun getWishList() = wishList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishViewHolder =
        WishViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_wish,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: WishViewHolder, position: Int) {
        holder.bind(wishList[position])
    }

    override fun getItemCount(): Int = wishList.size

    inner class WishViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(wishProduct: Product) = with(binding) {
            setVariable(BR.product, wishProduct)
            setVariable(BR.onWishClick, wishClickListener)
            setVariable(BR.onDeleteClick, object : View.OnClickListener {
                override fun onClick(v: View?) {
                    wishClickListener.onDeleteClick(wishProduct)
                }
            })
            setVariable(BR.onAddCartClick, object : View.OnClickListener {
                override fun onClick(v: View?) {
                    wishClickListener.onAddCartClick(wishProduct)
                }
            })
        }
    }

    interface OnWishClickListener {
        fun onWishClick(product: Product)
        fun onDeleteClick(product: Product)
        fun onAddCartClick(product: Product)
    }
}