package com.ninezero.shopang.view.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.ninezero.shopang.databinding.AlertDialogLayoutBinding

interface CustomDialogInterface {
    fun negativeClickListener()
    fun positiveClickListener()
}

class CustomDialog(
    private val customDialogInterface: CustomDialogInterface?,
    private val title: String?,
    private val msg: String? = null,
    private val negativeButtonText: String? = null,
    private val positiveButtonText: String,
    private val showNegativeButton: Boolean = false
): DialogFragment() {

    private lateinit var binding: AlertDialogLayoutBinding

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AlertDialogLayoutBinding.inflate(inflater, container, false)

        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.title.text = title
        binding.msg.text = msg ?: ""
        binding.negative.text = negativeButtonText
        binding.positive.text = positiveButtonText

        binding.msg.visibility = msg?.let { View.VISIBLE } ?: View.GONE
        binding.negative.visibility = if (showNegativeButton) View.GONE else View.VISIBLE

        binding.negative.setOnClickListener {
            customDialogInterface?.negativeClickListener()
            dismiss()
        }
        binding.positive.setOnClickListener {
            customDialogInterface?.positiveClickListener()
            dismiss()
        }
        return binding.root
    }

    private fun resize() {
        val width = resources.displayMetrics.widthPixels * 0.8
        dialog?.window?.setLayout(width.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onResume() {
        super.onResume()
        resize()
    }
}