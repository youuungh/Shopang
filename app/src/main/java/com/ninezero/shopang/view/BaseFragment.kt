package com.ninezero.shopang.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.transition.MaterialSharedAxis

abstract class BaseFragment<T : ViewDataBinding>(
    @LayoutRes val resId: Int
): Fragment() {

    private var _binding: T? = null
    val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate<T>(inflater, resId, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true).addTarget(view)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).addTarget(view)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ true).addTarget(view)
        reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, /* forward= */ false).addTarget(view)
        super.onViewCreated(view, savedInstanceState)
        initView()
        initViewModel()
        initListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected open fun initView() {}
    protected open fun initViewModel() {}
    protected open fun initListener() {}
}