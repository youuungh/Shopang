package com.ninezero.shopang.view.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ninezero.shopang.util.Argument

class OnBoardingPagerAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        val bundle = Bundle()
        bundle.putInt(Argument.ONBOARDING_POSITION, position)
        val fragment = OnBoardingPageFragment()
        fragment.arguments = bundle
        return fragment
    }
}