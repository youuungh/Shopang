package com.ninezero.shopang.view.main.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.ninezero.shopang.BR
import com.ninezero.shopang.R
import com.ninezero.shopang.model.Banner
import com.ninezero.shopang.model.Category
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.view.main.adapter.BannerAdapter
import com.ninezero.shopang.view.main.adapter.CategoryAdapter
import com.ninezero.shopang.view.main.adapter.ProductAdapter
import com.smarteist.autoimageslider.SliderView

class HomeAdapter(
    private val onBannerClick: BannerAdapter.OnBannerClickListener,
    private val onCategoryClick: CategoryAdapter.OnCategoryClickListener,
    private val onProductClick: ProductAdapter.OnProductClickListener,
    private val onAllProductClick: ProductAdapter.OnAllProductClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val bannerList: MutableList<Banner> = mutableListOf()
    private val categoryList: MutableList<Category> = mutableListOf()
    private val productList: MutableList<Product> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun addBannerList(list: MutableList<Banner>?) {
        if (list == null || list == bannerList) return
        bannerList.clear()
        bannerList.addAll(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addCategoryList(list: List<Category>?) {
        if (list == null || list == categoryList) return
        categoryList.clear()
        categoryList.addAll(list)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addProductList(list: List<Product>?) {
        if (list == null || list == productList) return
        productList.clear()
        productList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> VIEW_TYPE_BANNER
            1 -> VIEW_TYPE_CATEGORY
            else -> VIEW_TYPE_PRODUCT
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_BANNER -> BannerViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.banner_layout,
                    parent,
                    false
                )
            )

            VIEW_TYPE_CATEGORY -> CategoryViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.category_layout,
                    parent,
                    false
                )
            )

            VIEW_TYPE_PRODUCT -> ProductViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.product_layout,
                    parent,
                    false
                ),
                productList
            )

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int = 3

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is BannerViewHolder -> holder.bind()
            is CategoryViewHolder -> holder.bind()
            is ProductViewHolder -> holder.bind()
        }
    }

    inner class BannerViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind() = with(binding) {
            setVariable(BR.bannerList, bannerList)
            setVariable(BR.onBannerClick, onBannerClick)

            val indicator = root.findViewById<TextView>(R.id.indicator)
            val sliderView = root.findViewById<SliderView>(R.id.imageBanner)

            sliderView.setCurrentPageListener { position ->
                indicator.text = "${position + 1} / ${bannerList.size}"
            }
            indicator.text = "1 / ${bannerList.size}"
        }
    }

    inner class CategoryViewHolder(private val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() = with(binding) {
            setVariable(BR.categoryList, categoryList)
            setVariable(BR.onCategoryClick, this@HomeAdapter.onCategoryClick)
            executePendingBindings()
        }
    }

    inner class ProductViewHolder(
        private val binding: ViewDataBinding,
        private val productList: List<Product>
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind() = with(binding) {
            setVariable(BR.productList, productList)
            setVariable(BR.onProductClick, this@HomeAdapter.onProductClick)
            setVariable(BR.onAllProductClick, this@HomeAdapter.onAllProductClick)
            executePendingBindings()
        }
    }

    companion object {
        private const val VIEW_TYPE_BANNER = 0
        private const val VIEW_TYPE_CATEGORY = 1
        private const val VIEW_TYPE_PRODUCT = 2
    }
}