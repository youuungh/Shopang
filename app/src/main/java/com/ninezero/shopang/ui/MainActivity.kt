package com.ninezero.shopang.ui

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavInflater
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ninezero.shopang.R
import com.ninezero.shopang.databinding.ActivityMainBinding
import com.ninezero.shopang.util.Constants
import com.ninezero.shopang.util.PrefsUtil
import com.ninezero.shopang.util.ThemeUtil
import com.ninezero.shopang.util.extension.hide
import com.ninezero.shopang.util.extension.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        ThemeUtil.setTheme(this, PrefsUtil.getSharedPreferences())

        setupWindowInsets()
        setupNavigationController()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.navHost) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(systemBars.left, systemBars.top, systemBars.right)
            insets
        }
    }

    private fun setupNavigationController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host) as NavHostFragment
        navController = navHostFragment.navController
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph)

        if (!PrefsUtil.onboardShown) {
            navGraph.setStartDestination(R.id.onBoardingFragment)
        } else {
            navGraph.setStartDestination(R.id.homeFragment)
        }
        navController.graph = navGraph
        binding.bottomBar.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.wishFragment, R.id.userFragment -> {
                    setBottomNavVisibility(visible = true)
                }
                else -> setBottomNavVisibility(visible = false)
            }
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