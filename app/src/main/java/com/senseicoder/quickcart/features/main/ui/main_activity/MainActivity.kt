package com.senseicoder.quickcart.features.main.ui.main_activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val mainViewModel : MainActivityViewModel by lazy{
        ViewModelProvider(this)[MainActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration.Builder(
            setOf(
                R.id.homeFragment,
                R.id.categoryFragment,
                R.id.shoppingCartFragment,
                R.id.profileFragment,
            )
        ).build()
        navController = findNavController(this, R.id.nav_host)
        if(SharedPrefsService.getSharedPrefString(Constants.USER_TOKEN, Constants.USER_TOKEN_DEFAULT) != Constants.USER_TOKEN_DEFAULT){
            navController.navigate(R.id.action_loginFragment_to_homeFragment)
            navController.graph.setStartDestination(R.id.homeFragment)
        }

//        progressBar.startProgressBar()

        setSupportActionBar(binding.toolbar)

        mainViewModel
        setupWithNavController(binding.toolbar, navController, appBarConfiguration)
        setupWithNavController(binding.navView, navController)
        if(SharedPrefsService.getSharedPrefString(Constants.USER_ID, Constants.USER_ID_DEFAULT) != Constants.USER_ID_DEFAULT) {
            navController.navigate(R.id.action_loginFragment_to_homeFragment)
            navController.graph.setStartDestination(R.id.homeFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d(TAG, "onSupportNavigateUp: ${supportFragmentManager.backStackEntryCount}")
        return (navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp())
    }


    fun hideBottomNavBar() {
        binding.navView.visibility = View.GONE
    }

    fun showBottomNavBar() {
        binding.navView.visibility = View.VISIBLE
    }

    fun toolbarVisibility(flag: Boolean){
        if(flag)
            binding.toolbar.visibility = View.VISIBLE
        else
            binding.toolbar.visibility = View.GONE
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}