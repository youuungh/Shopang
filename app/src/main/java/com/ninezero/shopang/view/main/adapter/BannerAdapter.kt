package com.ninezero.shopang.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.ninezero.shopang.BR
import com.ninezero.shopang.R
import com.ninezero.shopang.model.Banner
import com.smarteist.autoimageslider.SliderViewAdapter

class BannerAdapter(
    private val bannerList: List<Banner>,
    private val onBannerClick: OnBannerClickListener
) : SliderViewAdapter<BannerAdapter.SliderViewHolder>() {

    override fun getCount(): Int = bannerList.size

    override fun onCreateViewHolder(parent: ViewGroup): SliderViewHolder =
        SliderViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_banner,
                parent,
                false
            )
        )

    override fun onBindViewHolder(viewHolder: SliderViewHolder, position: Int) {
        viewHolder.bind(bannerList[position])
    }

    inner class SliderViewHolder(private val binding: ViewDataBinding) : ViewHolder(binding.root) {
        fun bind(banner: Banner) = with(binding) {
            setVariable(BR.banner, banner)
            setVariable(BR.onBannerClick, onBannerClick)
        }
    }

    interface OnBannerClickListener {
        fun onBannerClick(banner: Banner)
    }
}