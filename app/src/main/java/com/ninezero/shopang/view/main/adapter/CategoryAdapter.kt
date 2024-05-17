package com.ninezero.shopang.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ninezero.shopang.BR
import com.ninezero.shopang.R
import com.ninezero.shopang.model.Category

class CategoryAdapter(
    private val categoryList: List<Category>,
    private val onCategoryClick: OnCategoryClickListener
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_category,
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int = categoryList.size

    inner class CategoryViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) = with(binding) {
            setVariable(BR.category, category)
            setVariable(BR.onCategoryClick, onCategoryClick)
            executePendingBindings()
        }
    }

    interface OnCategoryClickListener {
        fun onCategoryClick(category: Category)
    }
}