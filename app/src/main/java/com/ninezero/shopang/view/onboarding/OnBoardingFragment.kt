package com.ninezero.shopang.view.onboarding

import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentOnBoardingBinding
import com.ninezero.shopang.util.PrefsUtil
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding>(
    R.layout.fragment_on_boarding
) {
    private val fragments = mutableListOf<OnBoardingPageFragment>()

    override fun initView() = with(binding) {
        updateOnBoardingUI(0, false)

        viewpager.apply {
            adapter = OnBoardingPagerAdapter(this@OnBoardingFragment)
            currentItem = 0
            getChildAt(0).overScrollMode = View.OVER_SCROLL_NEVER
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    updateOnBoardingUI(position, true)
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    val itemCount = binding.viewpager.adapter?.itemCount ?: return
                    for (i in 0 until itemCount) {
                        if (fragments.size <= i) {
                            val fragment = childFragmentManager.findFragmentByTag("f$i") as? OnBoardingPageFragment
                            fragment?.let {
                                fragments.add(it)
                                it.updateLayout(false)
                            }
                        }
                    }

                    fragments.getOrNull(position)?.setOffset(position, positionOffset)
                    if (position > 0) {
                        fragments.getOrNull(position - 1)?.setOffset(position, positionOffset)
                    }
                    if (position < fragments.size - 1) {
                        fragments.getOrNull(position + 1)?.setOffset(position, positionOffset)
                    }

                    val targetPosition = if (positionOffset < 0.5f) position else position + 1
                    container.alpha = if (positionOffset < 0.5f) 1 - 2 * positionOffset else 2 * positionOffset - 1
                    title.setText(OnBoardingPageFragment.getTitle(targetPosition))
                    description.setText(OnBoardingPageFragment.getDescription(targetPosition))
                }
            })
        }
        indicator.attachTo(viewpager)
    }

    override fun initListener() = with(binding) {
        skip.setOnClickListener {
            viewpager.currentItem = 2
        }
        getStart.setOnClickListener {
            PrefsUtil.onBoardingState = true
            navigateToAuthFragment()
        }
    }

    private fun updateOnBoardingUI(position: Int, animated: Boolean) = with(binding) {
        val skipAlpha = if (position < 2) 1f else 0f
        val buttonAlpha = if (position == 2) 1f else 0f

        if (animated) {
            skip.animate().alpha(skipAlpha).setDuration(200).start()
            getStart.animate().alpha(buttonAlpha).setDuration(200).start()
        } else {
            skip.alpha = skipAlpha
            getStart.alpha = buttonAlpha
        }
        skip.isEnabled = position < 2
        getStart.isEnabled = position == 2
    }

    private fun navigateToAuthFragment() {
        val action = OnBoardingFragmentDirections.actionOnBoardingFragmentToAuthFragment()
        findNavController().navigate(action)
    }
}