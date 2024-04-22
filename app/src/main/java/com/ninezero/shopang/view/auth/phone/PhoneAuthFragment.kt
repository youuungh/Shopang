package com.ninezero.shopang.view.auth.phone

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.core.content.getSystemService
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
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
import com.ninezero.shopang.util.extension.showKeyBoard
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.util.extension.showToast
import com.ninezero.shopang.view.BaseFragment
import com.ninezero.shopang.view.auth.AuthViewModel
import com.ninezero.shopang.view.dialog.CustomDialog
import com.ninezero.shopang.view.dialog.CustomDialogInterface
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class PhoneAuthFragment : BaseFragment<FragmentPhoneAuthBinding>(
    R.layout.fragment_phone_auth
), CustomDialogInterface {
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val args by navArgs<PhoneAuthFragmentArgs>()
    private val verification by lazy { args.phoneVerificationData }

    @Inject
    lateinit var fAuth: FirebaseAuth

    @Inject
    @Named(Constants.LOADING)
    lateinit var loading: Dialog

    private var validPhoneNumber: String = ""
    private var isResendTextEnabled = false
    private val inputEditTexts: List<TextInputEditText> by lazy {
        with(binding) {
            listOf(et1, et2, et3, et4, et5, et6)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() {
        binding.fragment = this@PhoneAuthFragment
        authViewModel.startCountDown()
        formatPhoneNumber()
        setupCodeInputWatcher()
    }

    override fun initListener() {
        super.initListener()
        binding.back.setOnClickListener {
            if (authViewModel.isTimerRunning.value) {
                showAlertDialog()
            } else {
                closeFragment()
            }
        }
    }

    override fun initViewModel() {
        super.initViewModel()
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

    private fun formatPhoneNumber() {
        val phoneNumber = verification.phoneNumber
        val countryCode = phoneNumber.substring(0,3)
        val areaCode = phoneNumber.substring(3, 5)
        val middle = phoneNumber.substring(5, 9)
        val last = phoneNumber.substring(9)
        validPhoneNumber = "$countryCode $areaCode-$middle-$last"

        val formattedString = getString(R.string.chk_code_from_valid_phone_number, validPhoneNumber)
        val spannableString = SpannableString(formattedString)
        val start = formattedString.indexOf(validPhoneNumber)
        val end = start + validPhoneNumber.length
        spannableString.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.description.text = spannableString
    }

    private fun setupCodeInputWatcher() {
        inputEditTexts[0].apply {
            postDelayed({
                showKeyBoard()
            }, 250)
        }
        inputEditTexts.forEachIndexed { index, editText ->
            editText.doAfterTextChanged { editable ->
                if (editable?.length == 1) {
                    moveFocusToNext(index)
                } else if (editable.isNullOrEmpty() && index > 0) {
                    moveFocusToPreviousAndClear(index)
                }

                if (index == inputEditTexts.size - 1 && editable?.length == 1) {
                    val inputCode = inputEditTexts.joinToString("") { editText ->
                        editText.text.toString()
                    }
                    if (inputCode.length == inputEditTexts.size) {
                        editText.clearFocus()
                        hideKeyBoard(editText)
                        binding.root.showSnack("확인")
                    }
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

    private fun showAlertDialog() {
        activity?.let {
            val dialog = CustomDialog(
                this,
                getString(R.string.phone_auth_dialog_title),
                getString(R.string.phone_auth_dialog_msg),
                "나가기",
                "계속")
            dialog.show(it.supportFragmentManager, "AlertDialog")
        }
    }

    private fun hideKeyBoard(view: View?) {
        view?.let {
            val imm = requireContext().getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun navigateToHomeFragment() {
        val action = PhoneAuthFragmentDirections.actionPhoneAuthFragmentToHomeFragment()
        findNavController().navigate(action)
    }

    override fun negativeClickListener() { closeFragment() }
    override fun positiveClickListener() { }
}