package com.ninezero.shopang.view.main.user

import android.app.Dialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentUserBinding
import com.ninezero.shopang.util.LOADING
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.showToast
import com.ninezero.shopang.view.BaseFragment
import com.ninezero.shopang.view.auth.UserInfoViewModel
import com.ninezero.shopang.view.dialog.CustomDialog
import com.ninezero.shopang.view.dialog.CustomDialogInterface
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class UserFragment : BaseFragment<FragmentUserBinding>(
    R.layout.fragment_user
), CustomDialogInterface {
    private val userInfoViewModel by activityViewModels<UserInfoViewModel>()

    @Inject
    lateinit var fAuth: FirebaseAuth

    @Inject
    @Named(LOADING)
    lateinit var loading: Dialog

    private var userInfo: com.ninezero.shopang.model.UserInfo? = null

    override fun initViewModel() = with(binding) {
        fragment = this@UserFragment
        observeListener()
    }

    private fun observeListener() {
        userInfoViewModel.userInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    userInfo = it.data
                    binding.userInfo = userInfo
                    loading.hide()
                }
                is ResponseWrapper.Error -> {
                    loading.hide()
                }
                else -> loading.show()
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
        navigateToAuthFragment()
        showToast(getString(R.string.success_sign_out))
    }
}