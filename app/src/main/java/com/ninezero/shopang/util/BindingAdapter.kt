package com.ninezero.shopang.util

import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.ninezero.shopang.model.Banner
import com.ninezero.shopang.model.Category
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.extension.loadImageFromUrl
import com.ninezero.shopang.util.extension.setTintColor
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

@BindingAdapter("collapsingToolbar", "frameLayout", "collapsingToolbarTitle", "backgroundColor", requireAll = false)
fun AppBarLayout.setToolbarCollapseListener(
    collapsingToolbar: CollapsingToolbarLayout,
    frameLayout: FrameLayout,
    collapsingToolbarTitle: String,
    backgroundColor: Int
) {
    var isShow = true
    var scrollRange = -1

    addOnOffsetChangedListener { appBarLayout, verticalOffset ->
        if (scrollRange == -1) scrollRange = appBarLayout?.totalScrollRange!!

        if (scrollRange + verticalOffset == 0) {
            frameLayout.isVisible = false
            collapsingToolbar.setCollapsedTitleTextColor(backgroundColor.setTintColor())
            collapsingToolbar.title = collapsingToolbarTitle
            isShow = true
        } else if (isShow) {
            frameLayout.isVisible = isShow
            collapsingToolbar.title = " "
            isShow = false
        }
    }
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