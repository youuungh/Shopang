package com.ninezero.shopang.ui.onboarding

import android.content.res.Configuration
import android.view.ViewTreeObserver
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentOnBoardingPageBinding
import com.ninezero.shopang.ui.BaseFragment
import com.ninezero.shopang.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingPageFragment : BaseFragment<FragmentOnBoardingPageBinding>(
    R.layout.fragment_on_boarding_page
) {

    private var position: Int = 0
    var bgImageResource: Int = 0
    var titleResource: Int = 0
    var descriptResource: Int = 0

    override fun initView() {
        position = arguments?.getInt(Constants.Argument.ONBOARDING_POSITION) ?: 0

        bgImageResource = getBgImage(position)
        titleResource = getTitle(position)
        descriptResource = getDescription(position)

        with(binding) {
            fragment = this@OnBoardingPageFragment

            container.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener  {
                override fun onGlobalLayout() {
                    updateLayout(true)
                    container.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }

    fun updateLayout(force: Boolean) {
        if (context == null) return

        val orientation = resources.configuration.orientation
        val params = binding.container.layoutParams

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (!force && (params?.height ?: 0) > 0) return
            params?.height = binding.container.width
        } else {
            if (!force && (params?.width ?: 0) > 0) return
            params?.width = binding.container.height
        }

        with(binding) {
            container.requestLayout()
            if (force) container.forceLayout()
        }
    }

    fun setOffset(position: Int, offset: Float) {
        val bgOffset = -200
        val titleOffset = 150

        with(binding) {
            bgImage.translationX =
                if (position == this@OnBoardingPageFragment.position) offset * -bgOffset.toFloat() else (1 - offset) * bgOffset
            title.translationX =
                if (position == this@OnBoardingPageFragment.position) offset * -titleOffset.toFloat() else (1 - offset) * titleOffset
        }
    }

    override fun onResume() {
        super.onResume()
        updateLayout(true)
    }

    companion object {
        private fun getBgImage(position: Int) = when (position) {
            1 -> R.drawable.bg_onboarding_2
            2 -> R.drawable.bg_onboarding_3
            else -> R.drawable.bg_onboarding_1
        }
        fun getTitle(position: Int) = when (position) {
            1 -> R.string.title_2
            2 -> R.string.title_3
            else -> R.string.title_1
        }
        fun getDescription(position: Int) = when (position) {
            1 -> R.string.description_2
            2 -> R.string.description_3
            else -> R.string.description_1
        }
    }
}