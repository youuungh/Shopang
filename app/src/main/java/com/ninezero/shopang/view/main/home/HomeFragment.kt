package com.ninezero.shopang.view.main.home

import android.app.Dialog
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentHomeBinding
import com.ninezero.shopang.model.Banner
import com.ninezero.shopang.model.Category
import com.ninezero.shopang.model.Product
import com.ninezero.shopang.model.UserInfo
import com.ninezero.shopang.util.LOADING
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.util.extension.showToast
import com.ninezero.shopang.view.BaseFragment
import com.ninezero.shopang.view.auth.UserInfoViewModel
import com.ninezero.shopang.view.main.adapter.BannerAdapter
import com.ninezero.shopang.view.main.adapter.CategoryAdapter
import com.ninezero.shopang.view.main.adapter.ProductAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    R.layout.fragment_home
), BannerAdapter.OnBannerClickListener, CategoryAdapter.OnCategoryClickListener, ProductAdapter.OnProductClickListener {
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val userInfoViewModel by activityViewModels<UserInfoViewModel>()
    private val homeAdapter by lazy { HomeAdapter(this, this, this) }

    @Inject
    @Named(LOADING)
    lateinit var loading: Dialog

    private var userInfo: UserInfo? = null

    private val bannerList = mutableListOf(
        Banner("banner1", "https://i.imgur.com/VjUePxW.png"),
        Banner("banner2", "https://i.imgur.com/smPOLFj.png"),
        Banner("banner3", "https://i.imgur.com/zFg5puo.png"),
        Banner("banner4", "https://i.imgur.com/zXrAeE5.png")
    )

    override fun initViewModel() = with(binding) {
        fragment = this@HomeFragment
        applySystemWindowInsets(root)
        observeListener()
    }

    private fun observeListener() {
        userInfoViewModel.getUserInfoRealTime()
        userInfoViewModel.userInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    userInfo = it.data
                    binding.userInfo = userInfo

                    homeAdapter.addBannerList(bannerList)
                    homeViewModel.getProducts()
                    homeViewModel.getCategories()
                }

                is ResponseWrapper.Error -> {
                    loading.hide()
                    binding.root.showSnack(it.msg!!, anchor = binding.anchor)
                }

                is ResponseWrapper.Loading -> loading.show()
                is ResponseWrapper.Idle -> {}
            }
        }
        homeViewModel.productsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    val randomProducts = it.data?.asSequence()?.shuffled()?.take(10)?.toList()
                    homeAdapter.addProductList(randomProducts)
                    loading.hide()
                }

                is ResponseWrapper.Error -> {
                    loading.hide()
                    binding.root.showSnack(it.msg!!, anchor = binding.anchor)
                }

                is ResponseWrapper.Loading -> loading.show()
                is ResponseWrapper.Idle -> {}
            }
        }
        homeViewModel.categoriesLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    homeAdapter.addCategoryList(it.data)

                    initViews()
                    loading.hide()
                }

                is ResponseWrapper.Error -> {
                    loading.hide()
                    binding.root.showSnack(it.msg!!, anchor = binding.anchor)
                }

                is ResponseWrapper.Loading -> loading.show()
                is ResponseWrapper.Idle -> {}
            }
        }
    }

    private fun initViews() = with(binding) {
        rvHome.adapter = homeAdapter
    }

    override fun onProductClick(product: Product) {
        val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(product)
        findNavController().navigate(action)
    }

    override fun onCategoryClick(category: Category) {
        showToast("${category.name} 클릭됨")
    }

    override fun onBannerClick(banner: Banner) {
        showToast("${banner.bannerId} 클릭됨")
    }

    override fun onStop() {
        super.onStop()
        if (loading.isShowing) loading.hide()
    }
}