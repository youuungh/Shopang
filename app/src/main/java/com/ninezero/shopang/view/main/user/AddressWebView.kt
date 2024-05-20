package com.ninezero.shopang.view.main.user

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.AddressWebViewBinding
import com.ninezero.shopang.util.extension.closeFragment
import com.ninezero.shopang.view.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressWebView : BaseFragment<AddressWebViewBinding>(
    R.layout.address_web_view
) {

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        with(binding) {
            applySystemWindowInsets(root)
            webView.apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        view?.loadUrl("javascript:sample2_execDaumPostcode();")
                    }
                }
                settings.javaScriptEnabled = true
                addJavascriptInterface(AddressWebViewInterface(), "Android")
                loadUrl("https://kakaopostcodeservice.web.app/")
            }
        }
    }

    inner class AddressWebViewInterface {
        @JavascriptInterface
        fun processDATA(data: String) {
            requireActivity().runOnUiThread {
                val postcode = data.substring(0, 5)
                val address = data.substring(7)

                findNavController().previousBackStackEntry?.savedStateHandle?.set("data", data)
                findNavController().previousBackStackEntry?.savedStateHandle?.set("postcode", postcode)
                findNavController().previousBackStackEntry?.savedStateHandle?.set("address", address)
                closeFragment()
            }
        }
    }
}