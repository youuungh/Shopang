package com.ninezero.shopang.view.auth.google

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentGoogleAuthBinding
import com.ninezero.shopang.util.LOADING
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class GoogleAuthFragment : BaseFragment<FragmentGoogleAuthBinding>(
    R.layout.fragment_google_auth
) {

    @Inject
    @Named(LOADING)
    lateinit var loading: Dialog

    override fun initView() {
        super.initView()
    }

    override fun initViewModel() {
        super.initViewModel()
    }
}