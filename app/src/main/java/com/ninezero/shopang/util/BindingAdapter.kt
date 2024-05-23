package com.ninezero.shopang.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ninezero.shopang.model.Banner
import com.ninezero.shopang.model.Category
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.extension.loadImageFromUrl
import com.ninezero.shopang.view.main.adapter.BannerAdapter
import com.ninezero.shopang.view.main.adapter.CategoryAdapter
import com.ninezero.shopang.view.main.adapter.ProductAdapter
import com.smarteist.autoimageslider.SliderView

@BindingAdapter("imageResource")
fun setImageResource(view: ImageView, resource: Int) {
    view.setImageResource(resource)
}

@BindingAdapter("text")
fun setText(view: TextView, textResId: Int) {
    view.setText(textResId)
}

@BindingAdapter("imageUrl")
fun ImageView.setImageFromUrl(url: String?) {
    if (!url.isNullOrEmpty())
        loadImageFromUrl(url)
}

@BindingAdapter("bannerImage", "listener")
fun bannerImage(
    banner: SliderView,
    bannerList: List<Banner>,
    listener: BannerAdapter.OnBannerClickListener
) {
    val adapter = BannerAdapter(bannerList, listener)
    banner.setSliderAdapter(adapter)
    banner.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
    banner.scrollTimeInSec = 3
    banner.isAutoCycle = true
    banner.startAutoCycle()
}

@BindingAdapter("categoryList", "listener")
fun RecyclerView.setCategoryAdapter(
    categoryList: List<Category>?,
    listener: CategoryAdapter.OnCategoryClickListener
) {
    categoryList?.let {
        adapter = CategoryAdapter(it, listener)
    }
}

@BindingAdapter("productList", "listener")
fun RecyclerView.setProductAdapter(
    productList: List<Product>?,
    listener: ProductAdapter.OnProductClickListener
) {
    productList?.let {
        layoutManager = GridLayoutManager(context, 2)
        adapter = ProductAdapter(it, listener)
    }
}

@BindingAdapter("quantityText", "isPlus")
fun changeProductQuantity(
    imageView: ImageView,
    quantityText: TextView,
    isPlus: Boolean
) {
    imageView.setOnClickListener {
        var quantity = quantityText.text.toString().toInt()

        if (isPlus && quantity < 10) {
            quantity++
        } else if (!isPlus && quantity > 1) {
            quantity--
        }

        quantityText.text = quantity.toString()
    }
}