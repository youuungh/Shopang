package com.ninezero.shopang.view.main.about

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentAboutBinding
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.doOnApplyWindowInsets
import com.ninezero.shopang.util.extension.openUrl
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding>(
    R.layout.fragment_about
) {
    override fun initView() = with(binding) {
        applySystemWindowInsets(root)
        fragment = this@AboutFragment
    }

    fun openGit() {
        openUrl("https://github.com/youuungh")
    }

    fun openSource() {
        OssLicensesMenuActivity.setActivityTitle(getString(R.string.open_source))
        startActivity(Intent(requireActivity(), OssLicensesMenuActivity::class.java))
    }

    fun onBackPressed() = closeFragment()
}