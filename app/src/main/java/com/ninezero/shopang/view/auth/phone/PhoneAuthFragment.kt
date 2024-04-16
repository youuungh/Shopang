package com.ninezero.shopang.view.auth.phone

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentPhoneAuthBinding
import com.ninezero.shopang.util.Constants
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.hide
import com.ninezero.shopang.util.extension.show
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.view.BaseFragment
import com.ninezero.shopang.view.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class PhoneAuthFragment : BaseFragment<FragmentPhoneAuthBinding>(
    R.layout.fragment_phone_auth
) {
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val args by navArgs<PhoneAuthFragmentArgs>()
    private val verification by lazy { args.phoneVerificationData }

    @Inject
    lateinit var fAuth: FirebaseAuth

    @Inject
    @Named(Constants.LOADING)
    lateinit var loading: Dialog

    private var isResendTextEnabled = false

    override fun initView() {
        binding.fragment = this@PhoneAuthFragment
    }

    override fun initListener() {
        super.initListener()
        binding.back.setOnClickListener { closeFragment() }

        observeListener()
    }

    private fun observeListener() {
        authViewModel.authStatusLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    loading.hide()
                    navigateToHomeFragment()
                }
                is ResponseWrapper.Error -> {
                    loading.hide()
                    binding.root.showSnack(it.msg!!)
                }
                else -> loading.show()
            }
        }
    }

    private fun startCountDown() {
        object : CountDownTimer(
            Constants.COUNT_DOWN_DURATION_MILLIS,
            Constants.COUNT_DOWN_INTERVAL
        ) {
            override fun onTick(millisUntilFinished: Long) {
                binding.timer.text = getString(R.string.countdown, (millisUntilFinished / 1000))
            }

            override fun onFinish() {
                toggleResendCode(false)
            }
        }.start()
    }

    private fun toggleResendCode(timerExpired: Boolean) = with(binding) {
        if (timerExpired) {
            timer.show()
            resend.hide()
            isResendTextEnabled = false
        } else {
            timer.hide()
            resend.show()
            isResendTextEnabled = true
        }
    }

//    fun verifyPhoneNumber() {
//        val code = codeNum
//        if (code.isEmpty()) {
//            binding.root.showSnack(getString(R.string.
//        } else {
//            val credential = PhoneAuthProvider.getCredential(verificationData.verificationId, code)
//            authViewModel.signInAuthCredential(credential)
//        }
//    }

    fun resendCode() {
        if (isResendTextEnabled) {
            toggleResendCode(true)
            startCountDown()
        }
        resendVerificationCode()
    }

    private fun resendVerificationCode() {
        val options = PhoneAuthOptions.newBuilder(fAuth)
            .setPhoneNumber(verification.phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(authViewModel.authCallBack())
            .setForceResendingToken(verification.token)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun navigateToHomeFragment() {
        val action = PhoneAuthFragmentDirections.actionPhoneAuthFragmentToHomeFragment()
        findNavController().navigate(action)
    }
}