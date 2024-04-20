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
    customDialogInterface: CustomDialogInterface,
    title: String,
    msg: String,
    negativeButtonText: String,
    positiveButtonText: String,
    id: Int
): DialogFragment() {

    private var _binding: AlertDialogLayoutBinding? = null
    private val binding get() = _binding!!
    private var title: String? = null
    private var msg: String? = null
    private var negativeButtonText: String? = null
    private var positiveButtonText: String? = null
    private var id: Int? = null

    private var customDialogInterface: CustomDialogInterface? = null

    init {
        this.title = title
        this.msg = msg
        this.negativeButtonText = negativeButtonText
        this.positiveButtonText = positiveButtonText
        this.id = id
        this.customDialogInterface = customDialogInterface
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AlertDialogLayoutBinding.inflate(inflater, container, false)

        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.title.text = title

        if (msg == null) {
            binding.msg.visibility = View.GONE
        } else {
            binding.msg.text = msg
        }

        binding.negative.text = negativeButtonText
        binding.positive.text = positiveButtonText

        if (id == -1) {
            binding.negative.visibility = View.GONE
        }

        binding.negative.setOnClickListener {
            this.customDialogInterface?.negativeClickListener()
            dismiss()
        }
        binding.positive.setOnClickListener {
            this.customDialogInterface?.positiveClickListener()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}