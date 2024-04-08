package com.ninezero.shopang.view.onboarding

import android.annotation.SuppressLint
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentOnBoardingBinding
import com.ninezero.shopang.util.PrefsUtil
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OnBoardingFragment : BaseFragment<FragmentOnBoardingBinding>(
    R.layout.fragment_on_boarding
) {
    private val fragments = arrayOfNulls<OnBoardingPageFragment>(3)

    @Inject
    lateinit var prefsUtil: PrefsUtil

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        updateOnBoardingUI(0, false)

        with(binding) {
            skip.setOnClickListener {
                viewpager.currentItem = 2
            }
            getStart.setOnClickListener {
                prefsUtil.onboardShown = true
                val action = OnBoardingFragmentDirections.actionOnBoardingFragmentToNavLogin()
                findNavController().navigate(action)
            }
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
                        for (i in 0..2) {
                            if (fragments[i] == null) {
                                fragments[i] = childFragmentManager.findFragmentByTag("f$i") as? OnBoardingPageFragment
                                fragments[i]?.updateLayout(false)
                            }
                        }
                        fragments[position]?.setOffset(position, positionOffset)
                        if (position > 0)
                            fragments[position - 1]?.setOffset(position, positionOffset)
                        if (position < (binding.viewpager.adapter?.itemCount?.minus(1) ?: 0))
                            fragments[position + 1]?.setOffset(position, positionOffset)

                        val targetPosition = if (positionOffset < 0.5f) position else position + 1
                        container.alpha = if (positionOffset < 0.5f) 1 - 2 * positionOffset else 2 * positionOffset - 1
                        title.setText(OnBoardingPageFragment.getTitle(targetPosition))
                        description.setText(OnBoardingPageFragment.getDescription(targetPosition))
                    }
                })
            }
            indicator.attachTo(viewpager)
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
}