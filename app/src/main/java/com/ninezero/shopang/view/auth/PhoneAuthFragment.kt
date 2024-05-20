package com.ninezero.shopang.view.auth

import android.app.Dialog
import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.getSystemService
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentPhoneAuthBinding
import com.ninezero.shopang.util.LOADING
import com.ninezero.shopang.util.MAX_ATTEMPTS
import com.ninezero.shopang.util.PHONE
import com.ninezero.shopang.util.PrefsUtil
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.generateRandomNickname
import com.ninezero.shopang.util.extension.hide
import com.ninezero.shopang.util.extension.show
import com.ninezero.shopang.util.extension.showKeyBoard
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.view.BaseFragment
import com.ninezero.shopang.view.dialog.CustomDialog
import com.ninezero.shopang.view.dialog.CustomDialogInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class PhoneAuthFragment : BaseFragment<FragmentPhoneAuthBinding>(
    R.layout.fragment_phone_auth
), CustomDialogInterface {
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val userInfoViewModel by viewModels<UserInfoViewModel>()
    private val args by navArgs<PhoneAuthFragmentArgs>()
    private val verification by lazy { args.phoneVerificationData }
    private lateinit var callback: OnBackPressedCallback

    @Inject
    lateinit var prefsUtil: PrefsUtil

    @Inject
    lateinit var fAuth: FirebaseAuth

    @Inject
    @Named(LOADING)
    lateinit var loading: Dialog

    private val inputEditTexts: List<TextInputEditText> by lazy {
        with(binding) {
            listOf(et1, et2, et3, et4, et5, et6)
        }
    }

    private var validPhoneNumber: String = ""
    private var last: String = ""
    private var isResendTextEnabled = false
    private var isAttempts = 0

    private var userInfo: com.ninezero.shopang.model.UserInfo? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (authViewModel.isTimerRunning.value) {
                    showDialog(
                        getString(R.string.phone_auth_dialog_title),
                        getString(R.string.phone_auth_dialog_msg),
                        getString(R.string.action_exit),
                        getString(R.string.action_keep)
                    )
                } else {
                    closeFragment()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun initView() = with(binding) {
        applySystemWindowInsets(root)
        fragment = this@PhoneAuthFragment
        authViewModel.startCountDown()
        formatPhoneNumber()
        setupCodeInputWatcher()
    }

    override fun initListener() {
        binding.back.setOnClickListener {
            if (authViewModel.isTimerRunning.value) {
                showDialog(
                    getString(R.string.phone_auth_dialog_title),
                    getString(R.string.phone_auth_dialog_msg),
                    getString(R.string.action_exit),
                    getString(R.string.action_keep)
                )
            } else {
                closeFragment()
            }
        }
    }

    override fun initViewModel() {
        observeTimer()
        observeListener()
    }

    private fun observeTimer() {
        viewLifecycleOwner.lifecycleScope.launch {
            authViewModel.timerSharedFlow.collect { millisUntilFinished ->
                binding.timer.text = getString(R.string.count_down, (millisUntilFinished / 1000))
                toggleResendCode(millisUntilFinished > 0L)
            }
        }
    }

    private fun observeListener() {
        authViewModel.authStatusLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    if (prefsUtil.phoneSignedIn) {
                        loadUserInfo()
                    } else {
                        uploadUserInfo()
                    }
                }

                is ResponseWrapper.Error -> {
                    loading.hide()
                    binding.root.showSnack(getString(R.string.error_auth_failed))
                }

                else -> loading.show()
            }
        }
        authViewModel.authInfoLiveData.observe(viewLifecycleOwner) {
            if (it is ResponseWrapper.Success) {
                closeFragment()
                loading.hide()
            }
        }
    }

    private fun loadUserInfo() {
        userInfoViewModel.getUserInfo()
        userInfoViewModel.userInfoLiveData.observe(viewLifecycleOwner) { response ->
            if (response is ResponseWrapper.Success) {
                userInfo = response.data
                userInfo?.let {
                    authViewModel.uploadUserInfo(
                        PHONE,
                        userName = it.userName,
                        userAddress = it.userAddress,
                        profileImageUri = it.profileImageUrl?.let { uri -> Uri.parse(uri) },
                        isUpload = false,
                        isUpdate = true
                    )
                    authViewModel.checkSignInPlatform(PHONE)
                }
            }
        }
    }

    private fun uploadUserInfo() {
        val userName = last.generateRandomNickname()
        authViewModel.uploadUserInfo(
            PHONE,
            userName,
            userAddress = null,
            profileImageUri = null,
            isUpload = true,
            isUpdate = false
        )
        authViewModel.checkSignInPlatform(PHONE)
    }

    private fun setupCodeInputWatcher() {
        inputEditTexts[0].apply {
            postDelayed({
                showKeyBoard()
            }, 300)
        }
        inputEditTexts.forEachIndexed { index, editText ->
            editText.doAfterTextChanged { editable ->
                if (editable?.length == 1) {
                    moveFocusToNext(index)
                }
                if (index == inputEditTexts.size - 1 && editable?.length == 1) {
                    val inputCode = inputEditTexts.joinToString("") { editText ->
                        editText.text.toString()
                    }
                    if (inputCode.length == inputEditTexts.size) {
                        editText.clearFocus()
                        hideKeyBoard(editText)
                        verifyCode(inputCode)
                    }
                }
            }

            editText.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && index > 0) {
                    if (editText.text.isNullOrEmpty()) {
                        moveFocusToPreviousAndClear(index)
                    } else {
                        editText.text?.clear()
                    }
                    true
                } else if (event.action == KeyEvent.ACTION_UP) {
                    if (editText.text.toString().length == 1) {
                        val newText = event.unicodeChar.toChar().toString()
                        editText.setText(newText)
                        editText.setSelection(newText.length)
                        moveFocusToNext(index)
                    }
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun moveFocusToNext(index: Int) {
        if (index < inputEditTexts.size - 1) {
            inputEditTexts[index + 1].requestFocus()
        }
    }

    private fun moveFocusToPreviousAndClear(index: Int) {
        if (index > 0) {
            val previousEditText = inputEditTexts[index - 1]
            previousEditText.requestFocus()
            previousEditText.text?.clear()
        }
    }

    private fun verifyCode(inputCode: String) {
        isAttempts++
        if (isAttempts > MAX_ATTEMPTS) {
            showDialog(
                getString(R.string.phone_auth_failed_dialog_title),
                getString(R.string.phone_auth_failed_dialog_msg),
                null,
                getString(R.string.action_confirm),
                true
            )
        } else {
            val credential = PhoneAuthProvider.getCredential(verification.id, inputCode)
            authViewModel.signInAuthCredential(credential)
        }
    }

    private fun toggleResendCode(timerExpired: Boolean) = with(binding) {
        isResendTextEnabled = if (timerExpired) {
            timer.show()
            resend.hide()
            false
        } else {
            timer.hide()
            resend.show()
            true
        }
    }

    fun resendCode() {
        if (isResendTextEnabled) {
            toggleResendCode(true)
            authViewModel.startCountDown()
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

    private fun formatPhoneNumber() {
        val phoneNumber = verification.phoneNumber
        val countryCode = phoneNumber.substring(0, 3)
        val areaCode = phoneNumber.substring(3, 5)
        val middle = phoneNumber.substring(5, 9)
        last = phoneNumber.substring(9)
        validPhoneNumber = "$countryCode $areaCode-$middle-$last"

        val formattedString = getString(R.string.chk_code_from_valid_phone_number, validPhoneNumber)
        val spannableString = SpannableString(formattedString)
        val start = formattedString.indexOf(validPhoneNumber)
        val end = start + validPhoneNumber.length
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.description.text = spannableString
    }

    private fun showDialog(
        title: String,
        message: String,
        negativeButton: String?,
        positiveButton: String,
        showNegativeButton: Boolean = false
    ) {
        activity?.let {
            val dialog = CustomDialog(
                this,
                title,
                message,
                negativeButton,
                positiveButton,
                showNegativeButton
            )
            dialog.show(it.supportFragmentManager, "Dialog")
        }
    }

    private fun hideKeyBoard(view: View?) {
        view?.let {
            val imm = requireContext().getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    override fun negativeClickListener() {
        closeFragment()
    }

    override fun positiveClickListener() {
        if (isAttempts > MAX_ATTEMPTS) closeFragment()
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}