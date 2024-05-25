package com.ninezero.shopang.view.main.payment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentPaymentBinding
import com.ninezero.shopang.model.Order
import com.ninezero.shopang.model.Payment
import com.ninezero.shopang.model.UserInfo
import com.ninezero.shopang.util.LOADING
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.view.auth.UserInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class PaymentFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    private val userInfoViewModel by activityViewModels<UserInfoViewModel>()
    private val paymentViewModel by activityViewModels<PaymentViewModel>()
    private val args by navArgs<PaymentFragmentArgs>()
    private val totalPrice by lazy { args.totalPrice }
    private val cartList by lazy { args.cartList }

    private var userInfo: UserInfo? = null
    private lateinit var order: Order
    private var isDataIntegrity = false

    @Inject
    @Named(LOADING)
    lateinit var loading: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(inflater, R.layout.fragment_payment, container, false)
        binding.apply {
            fragment = this@PaymentFragment
            isProgress = false
            return this.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeListener()
    }

    private fun observeListener() {
        userInfoViewModel.getUserInfoRealTime()
        userInfoViewModel.userInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    userInfo = it.data
                    isDataIntegrity = userInfo?.userAddress != null

                    if (userInfo != null) {
                        val payment = Payment(
                            userInfo?.userAddress,
                            getString(R.string.payment),
                            totalPrice = totalPrice
                        )
                        binding.payment = payment
                    }
                }

                is ResponseWrapper.Error -> {
                    binding.root.showSnack(it.msg!!)
                }

                else -> {}
            }
        }
        paymentViewModel.orderProductsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    order = it.data!!
                    loading.hide()
                    navigateToOrderResultFragment()
                    binding.isProgress = false
                }

                is ResponseWrapper.Error -> {
                    loading.hide()
                    binding.root.showSnack(it.msg!!)
                }

                is ResponseWrapper.Loading -> loading.show()
                is ResponseWrapper.Idle -> loading.hide()
            }
        }
    }

    fun toPay() {
        if (!isDataIntegrity) {
            binding.root.showSnack(getString(R.string.error_set_address), anchor = binding.anchor)
            return
        }
        binding.isProgress = true
        paymentViewModel.submitUserOrder(
            cartList,
            userAddress = userInfo?.userAddress!!,
            totalPrice = totalPrice
        )
    }

    fun navigateToAddressFragment() {
        val action = PaymentFragmentDirections.actionPaymentFragmentToAddressFragment()
        findNavController().navigate(action)
    }

    private fun navigateToOrderResultFragment() {
        val action = PaymentFragmentDirections.actionPaymentFragmentToOrderResultFragment(order)
        findNavController().navigate(action)
    }

    fun closePaymentBottomSheet() = closeFragment()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}