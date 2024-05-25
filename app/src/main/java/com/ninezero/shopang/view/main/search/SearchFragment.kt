package com.ninezero.shopang.view.main.search

import android.annotation.SuppressLint
import android.app.Dialog
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentSearchBinding
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.util.LOADING
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.hide
import com.ninezero.shopang.util.extension.show
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class SearchFragment : BaseFragment<FragmentSearchBinding>(
    R.layout.fragment_search
), SearchAdapter.OnProductClickListener {

    private val searchViewModel by viewModels<SearchViewModel>()
    private val searchAdapter by lazy { SearchAdapter(this) }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() = with(binding) {
        fragment = this@SearchFragment
        adapter = searchAdapter

        search.doAfterTextChanged { text ->
            text?.toString()?.let(searchViewModel::searchProducts)
        }

        root.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                hideKeyBoard(v)
            }
            true
        }
    }

    override fun initViewModel() {
        observeListener()
    }

    private fun observeListener() {
        searchViewModel.searchProductsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    if (it.data.isNullOrEmpty()) {
                        binding.emptyContainer.show()
                    } else {
                        searchAdapter.updateSearchList(it.data)
                        binding.emptyContainer.hide()
                    }
                }

                is ResponseWrapper.Error -> {}
                is ResponseWrapper.Loading -> {}
                is ResponseWrapper.Idle -> searchAdapter.updateSearchList(emptyList())
            }
        }
    }

    private fun hideKeyBoard(view: View?) {
        view?.let {
            val imm = requireContext().getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
            binding.search.clearFocus()
        }
    }

    fun onBackPressed() = closeFragment()
    override fun onProductClick(product: Product) {
        val action = SearchFragmentDirections.actionSearchFragmentToProductDetailFragment(product)
        findNavController().navigate(action)
    }
}