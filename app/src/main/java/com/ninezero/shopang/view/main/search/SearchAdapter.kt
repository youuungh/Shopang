package com.ninezero.shopang.view.main.search

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ninezero.shopang.BR
import com.ninezero.shopang.R
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.ProductDiffCallback

class SearchAdapter(
    private val productClickListener: OnProductClickListener
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    private var searchList = emptyList<Product>()

    fun updateSearchList(newList: List<Product>) {
        val diffResult = DiffUtil.calculateDiff(ProductDiffCallback(searchList, newList))
        searchList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder =
        SearchViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_search_product,
                parent,
                false
            )
        )

    override fun getItemCount(): Int = searchList.size

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(searchList[position])
    }

    inner class SearchViewHolder(private val binding: ViewDataBinding) :
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