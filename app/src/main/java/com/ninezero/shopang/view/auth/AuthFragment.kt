package com.ninezero.shopang.view.auth

import android.app.Dialog
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentAuthBinding
import com.ninezero.shopang.model.PhoneVerification
import com.ninezero.shopang.util.AuthState
import com.ninezero.shopang.util.Constants
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class AuthFragment : BaseFragment<FragmentAuthBinding>(
    R.layout.fragment_auth
) {
    private val authViewModel by viewModels<AuthViewModel>()

    @Inject
    lateinit var fAuth: FirebaseAuth

    @Inject
    @Named(Constants.LOADING)
    lateinit var loading: Dialog

    private var id: String? = null
    private var token: PhoneAuthProvider.ForceResendingToken? = null
    private var timeOut: Long = 0
    private var validPhoneNumber: String = ""

    override fun initView() {
        binding.fragment = this@AuthFragment
    }

    override fun initListener() {
        super.initListener()
        observeListener()
    }

    private fun observeListener() {
        authViewModel.authLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is AuthState.SuccessWithCode -> {
                    id = it.id
                    token = it.token
                    navigateToPhoneAuthFragment(it.id, it.token)
                    authViewModel.setAuthLiveData(AuthState.Idle)
                    loading.hide()
                }
                is AuthState.SuccessWithCredential -> {
                    authViewModel.signInAuthCredential(it.data)
                    authViewModel.setAuthLiveData(AuthState.Idle)
                    loading.hide()
                }
                is AuthState.Error -> {
                    loading.hide()
                    when (it.error){
                        is FirebaseAuthInvalidCredentialsException -> {
                            binding.root.showSnack(getString(R.string.chk_internet_connection))
                        }
                        else -> {
                            binding.root.showSnack(getString(R.string.errorMsg))
                        }
                    }
                }
                is AuthState.Loading -> loading.show()
                is AuthState.Idle -> loading.hide()
            }
        }
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

    fun checkPhoneNumber() = with(binding) {
        val phoneNumber = phoneNumber.text.toString().trim()
        val countryCode = 82
        when {
            phoneNumber.isBlank() -> {
                root.showSnack(getString(R.string.chk_empty_phone_number))
            }
            phoneNumber.toInt() < 11 -> {
                root.showSnack(getString(R.string.chk_valid_phone_number))
            }
            else -> {
                validPhoneNumber = "+$countryCode$phoneNumber"
                if (timeOut == 0L || timeOut < System.currentTimeMillis()) {
                    sendVerificationCode(validPhoneNumber)
                } else {
                    navigateToPhoneAuthFragment(id!!, token!!)
                }
            }
        }
    }

    private fun sendVerificationCode(validPhoneNumber: String) {
        authViewModel.setAuthLiveData(AuthState.Loading)
        signInWithPhoneNumber(validPhoneNumber)
    }

    private fun signInWithPhoneNumber(validPhoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(fAuth)
            .setPhoneNumber(validPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(requireActivity())
            .setCallbacks(authViewModel.authCallBack())
            .build()
        timeOut = (System.currentTimeMillis() + 60000L)
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun navigateToPhoneAuthFragment(
        id: String,
        token: PhoneAuthProvider.ForceResendingToken
    ) {
        val verification = PhoneVerification(id, token, validPhoneNumber)
        val action = AuthFragmentDirections.actionAuthFragmentToPhoneAuthFragment(verification)
        findNavController().navigate(action)
    }

    fun navigateToGoogleAuthFragment() {
        val action = AuthFragmentDirections.actionAuthFragmentToGoogleAuthFragment()
        findNavController().navigate(action)
    }

    fun navigateToNaverAuthFragment() {
        val action = AuthFragmentDirections.actionAuthFragmentToNaverAuthFragment()
        findNavController().navigate(action)
    }

    private fun navigateToHomeFragment() {
        val action = AuthFragmentDirections.actionAuthFragmentToHomeFragment()
        findNavController().navigate(action)
    }
}