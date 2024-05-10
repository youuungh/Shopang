package com.ninezero.shopang.view.main.user

import android.annotation.SuppressLint
import android.app.Dialog
import android.net.Uri
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentEditProfileBinding
import com.ninezero.shopang.util.LOADING
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.view.BaseFragment
import com.ninezero.shopang.view.auth.AuthViewModel
import com.ninezero.shopang.view.auth.UserInfoViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(
    R.layout.fragment_edit_profile
) {
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val userInfoViewModel by viewModels<UserInfoViewModel>()

    @Inject
    @Named(LOADING)
    lateinit var loading: Dialog

    private var userInfo: com.ninezero.shopang.model.UserInfo? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        it?.let {
            binding.profileImage.setImageURI(it)
            binding.profileImage.tag = it
        }
        loading.hide()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun initView() = with(binding) {
        fragment = this@EditProfileFragment
        scrollView.setOnTouchListener { v, event ->
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
        userInfoViewModel.getUserInfoRealTime()
        userInfoViewModel.userInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    userInfo = it.data
                    binding.userInfo = userInfo
                }

                is ResponseWrapper.Error -> {
                    binding.root.showSnack(it.msg!!, anchor = binding.save)
                }

                else -> {}
            }
        }
    }

    fun editProfileImage() {
        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        loading.show()
    }

    fun saveProfile() {
        with(binding) {
            val editedName = userName.text.toString()
            if (editedName.isEmpty()) {
                root.showSnack(getString(R.string.chk_empty_user_name), anchor = save)
                return@with
            }

            userInfoViewModel.updateUserName(editedName)

            val editedProfileImageUri = if (profileImage.tag != null) {
                profileImage.tag as? Uri
            } else if (userInfo?.profileImageUrl != null) {
                Uri.parse(userInfo?.profileImageUrl)
            } else {
                null
            }

            editedProfileImageUri?.let {
                authViewModel.uploadUserInfo(
                    platform = userInfo!!.platform,
                    userName = editedName,
                    userAddress = userInfo!!.userAddress,
                    profileImageUri = editedProfileImageUri,
                    isUpload = true,
                    isUpdate = true
                )
            }
        }
        closeFragment()
    }

    private fun hideKeyBoard(view: View?) {
        view?.let {
            val imm = requireContext().getSystemService<InputMethodManager>()
            imm?.hideSoftInputFromWindow(it.windowToken, 0)
            binding.userName.clearFocus()
        }
    }

    fun navigateToAddressFragment() {
        val action = EditProfileFragmentDirections.actionEditProfileFragmentToAddressFragment()
        findNavController().navigate(action)
    }

    fun onBackPressed() = closeFragment()
}