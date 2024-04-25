package com.ninezero.shopang.view.auth

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentAuthBinding
import com.ninezero.shopang.model.PhoneVerification
import com.ninezero.shopang.util.AuthState
import com.ninezero.shopang.util.LOADING
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.view.BaseFragment
import com.ninezero.shopang.view.dialog.CustomDialog
import com.ninezero.shopang.view.dialog.CustomDialogInterface
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class AuthFragment : BaseFragment<FragmentAuthBinding>(
    R.layout.fragment_auth
), CustomDialogInterface {
    private val authViewModel by activityViewModels<AuthViewModel>()

    @Inject
    lateinit var fAuth: FirebaseAuth

    @Inject
    lateinit var googleSignInOptions: GoogleSignInOptions

    @Inject
    @Named(LOADING)
    lateinit var loading: Dialog

    private var id: String? = null
    private var token: PhoneAuthProvider.ForceResendingToken? = null
    private var timeOut: Long = 0
    private var validPhoneNumber: String = ""

    private val googleSignInLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            authViewModel.processGoogleSignInResult(task, getString(R.string.error_msg))
        }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        with(binding) {
            fragment = this@AuthFragment
            root.setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    hideKeyBoard(v)
                }
                true
            }
        }
    }

    override fun initViewModel() {
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
                            binding.root.showSnack(getString(R.string.error_msg))
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
        authViewModel.googleAuthLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    navigateToHomeFragment()
                    loading.hide()
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
        hideKeyBoard(view)
        val input = phoneNumber.text.toString().trim()
        val countryCode = 82
        when {
            input.isBlank() -> {
                root.showSnack(getString(R.string.chk_empty_phone_number))
            }
            input.length < 11 || !isValidPhoneNumber(input) -> {
                root.showSnack(getString(R.string.chk_valid_phone_number))
            }
            else -> {
                if (authViewModel.isTimerRunning.value) {
                    showAlertDialog()
                } else {
                    val preNumber = input.substring(1, 3)
                    val postNumber = input.substring(3)
                    validPhoneNumber = "+$countryCode$preNumber$postNumber"
                    if (timeOut == 0L || timeOut < System.currentTimeMillis()) {
                        sendVerificationCode(validPhoneNumber)
                    } else {
                        navigateToPhoneAuthFragment(id!!, token!!)
                    }
                }
            }
        }
    }

    fun startGoogleSignInIntent() {
        loading.show()
        val mGoogleSignInClient = GoogleSignIn.getClient(
            requireContext(),
            googleSignInOptions
        )
        val signInIntent = mGoogleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        val regex = "^01(0|1|[6-9])(\\d{3,4})(\\d{4})$".toRegex()
        return regex.matches(phoneNumber)
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

    private fun showAlertDialog() {
        activity?.let {
            val dialog = CustomDialog(
                this,
                getString(R.string.auth_dialog_title),
                getString(R.string.auth_dialog_msg),
                null,
                "확인",
                true
            )
            dialog.show(it.supportFragmentManager, "AlertDialog")
        }
    }

    private fun hideKeyBoard(view: View?) {
        view?.let {
            val imm = requireContext().getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
            binding.phoneNumber.clearFocus()
        }
    }

    private fun navigateToPhoneAuthFragment(
        id: String,
        token: PhoneAuthProvider.ForceResendingToken
    ) {
        val verification = PhoneVerification(id, token, validPhoneNumber)
        val action = AuthFragmentDirections.actionAuthFragmentToPhoneAuthFragment(verification)
        findNavController().navigate(action)
    }

    private fun navigateToHomeFragment() {
        val action = AuthFragmentDirections.actionAuthFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    override fun negativeClickListener() { }
    override fun positiveClickListener() { }
}