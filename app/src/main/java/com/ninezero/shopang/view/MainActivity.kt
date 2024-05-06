package com.ninezero.shopang.view

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.ActivityMainBinding
import com.ninezero.shopang.util.PrefsUtil
import com.ninezero.shopang.util.ThemeUtil
import com.ninezero.shopang.util.extension.hide
import com.ninezero.shopang.util.extension.show
import com.ninezero.shopang.view.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var binding: ActivityMainBinding
    private val authViewModel by viewModels<AuthViewModel>()

    @Inject
    lateinit var fAuth: FirebaseAuth

    @Inject
    lateinit var prefsUtil: PrefsUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        ThemeUtil.setTheme(this, prefsUtil.getSharedPrefs())

        setupNavigationController()
    }

    private fun setupNavigationController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        val navController = navHostFragment.findNavController()
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        if (prefsUtil.onboardingState) {
            val isUserLoggedIn = authViewModel.isUserLoggedIn()
            if (isUserLoggedIn) {
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
            R.id.homeFragment, R.id.wishFragment, R.id.cartFragment, R.id.userFragment -> {
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