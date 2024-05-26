package com.ninezero.shopang.view

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.ActivityMainBinding
import com.ninezero.shopang.util.NetworkStatusViewModel
import com.ninezero.shopang.util.PrefsUtil
import com.ninezero.shopang.util.ThemeUtil
import com.ninezero.shopang.util.extension.hide
import com.ninezero.shopang.util.extension.hideSystemUI
import com.ninezero.shopang.util.extension.show
import com.ninezero.shopang.view.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var networkStatusViewModel: NetworkStatusViewModel
    private val authViewModel by viewModels<AuthViewModel>()

    @Inject
    lateinit var fAuth: FirebaseAuth

    @Inject
    lateinit var prefsUtil: PrefsUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        hideSystemUI()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ThemeUtil.setTheme(this, prefsUtil.getSharedPrefs())
        setNetworkStatusObserver()
        setNavigationController()
    }

    private fun setNetworkStatusObserver() {
        networkStatusViewModel = NetworkStatusViewModel(applicationContext)
        networkStatusViewModel.observe(this) { isConnected ->
            if (isConnected) {
                binding.lostContainer.hide()
            } else {
                binding.lostContainer.show()
            }
        }
    }

    private fun setNavigationController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.findNavController()
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        if (prefsUtil.onboardingState) {
            if (authViewModel.isUserLoggedIn()) {
                navGraph.setStartDestination(R.id.homeFragment)
            } else {
                navGraph.setStartDestination(R.id.authFragment)
            }
        } else {
            navGraph.setStartDestination(R.id.onBoardingFragment)
        }

        navController.graph = navGraph
        navController.addOnDestinationChangedListener(this)
        binding.bottomBar.setupWithNavController(navController)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when (destination.id) {
            R.id.homeFragment, R.id.wishFragment, R.id.cartFragment, R.id.userFragment,
            R.id.paymentFragment -> {
                setBottomNavVisibility(visible = true)
            }

            else -> setBottomNavVisibility(visible = false)
        }
    }

    private fun setBottomNavVisibility(visible: Boolean) = with(binding) {
        if (visible) {
            bottomBar.show()
        } else {
            bottomBar.hide()
        }
    }
}