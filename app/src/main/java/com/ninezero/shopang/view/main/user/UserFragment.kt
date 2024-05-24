package com.ninezero.shopang.view.main.user

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentUserBinding
import com.ninezero.shopang.model.UserInfo
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.util.extension.showToast
import com.ninezero.shopang.view.BaseFragment
import com.ninezero.shopang.view.auth.AuthViewModel
import com.ninezero.shopang.view.auth.UserInfoViewModel
import com.ninezero.shopang.view.dialog.CustomDialog
import com.ninezero.shopang.view.dialog.CustomDialogInterface
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserFragment : BaseFragment<FragmentUserBinding>(
    R.layout.fragment_user
), CustomDialogInterface {
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val userInfoViewModel by viewModels<UserInfoViewModel>()

    @Inject
    lateinit var fAuth: FirebaseAuth

    private var userInfo: UserInfo? = null

    override fun initViewModel() = with(binding) {
        fragment = this@UserFragment
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
                }

                is ResponseWrapper.Error -> {
                    binding.root.showSnack(it.msg!!, anchor = binding.anchor)
                }

                else -> {}
            }
        }
    }

    fun signOut() {
        showDialog(
            getString(R.string.sign_out),
            getString(R.string.user_dialog_msg),
            getString(R.string.action_no),
            getString(R.string.action_yes)
        )
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

    fun navigateToEditProfileFragment() {
        val action = UserFragmentDirections.actionUserFragmentToEditProfileFragment()
        findNavController().navigate(action)
    }

    fun navigateToOrderFragment() {
        val action = UserFragmentDirections.actionUserFragmentToOrderFragment()
        findNavController().navigate(action)
    }

    fun navigateToAddressFragment() {
        val action = UserFragmentDirections.actionUserFragmentToAddressFragment()
        findNavController().navigate(action)
    }

    private fun navigateToAuthFragment() {
        val action = UserFragmentDirections.actionUserFragmentToAuthFragment()
        findNavController().navigate(action)
    }

    override fun negativeClickListener() { }
    override fun positiveClickListener() {
        fAuth.signOut()
        authViewModel.resetUserInfoLiveData()
        navigateToAuthFragment()
        showToast(getString(R.string.success_sign_out))
    }
}