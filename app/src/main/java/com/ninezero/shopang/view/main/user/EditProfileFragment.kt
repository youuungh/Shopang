package com.ninezero.shopang.view.main.user

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.getSystemService
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.FragmentEditProfileBinding
import com.ninezero.shopang.util.ResponseWrapper
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.util.extension.showSnack
import com.ninezero.shopang.util.extension.showToast
import com.ninezero.shopang.view.BaseFragment
import com.ninezero.shopang.view.auth.AuthViewModel
import com.ninezero.shopang.view.auth.UserInfoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(
    R.layout.fragment_edit_profile
) {
    private val authViewModel by activityViewModels<AuthViewModel>()
    private val userInfoViewModel by activityViewModels<UserInfoViewModel>()

    private var userInfo: com.ninezero.shopang.model.UserInfo? = null

    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        it?.let {
            binding.profileImage.setImageURI(it)
            binding.profileImage.tag = it
        }
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

    override fun initViewModel()  {
        observeListener()
    }

    private fun observeListener() {
        userInfoViewModel.userInfoLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseWrapper.Success -> {
                    userInfo = it.data
                    binding.userInfo = userInfo
                }
                else -> {}
            }
        }
    }

    fun editProfileImage() {
        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    fun saveProfile() = with(binding) {
        val editedName =  userName.text.toString()
        if (editedName.isEmpty()) {
            root.showSnack(getString(R.string.chk_empty_user_name), anchor = save)
            return@with
        }

        val editedProfileImageUri = if (profileImage.tag != null) {
            profileImage.tag as? Uri
        } else if (userInfo?.profileImageUrl != null) {
            Uri.parse(userInfo?.profileImageUrl)
        } else {
            null
        }

        authViewModel.uploadUserInfo(
            platform = userInfo!!.platform,
            userName = editedName,
            userAddress = userInfo!!.userAddress,
            profileImageUri = editedProfileImageUri,
            isUpdate = true
        )
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