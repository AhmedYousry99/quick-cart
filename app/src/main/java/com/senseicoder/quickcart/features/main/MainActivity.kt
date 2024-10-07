package com.senseicoder.quickcart.features.main

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.CircularProgressIndicatorDialog
import com.senseicoder.quickcart.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var progressBar: CircularProgressIndicatorDialog

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
        progressBar = CircularProgressIndicatorDialog(this)
//        progressBar.startProgressBar()

        setSupportActionBar(binding.toolbar)

        setupWithNavController(binding.toolbar, navController, appBarConfiguration)
        setupWithNavController(binding.navView, navController)
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

    fun showLoading() {
        progressBar.startProgressBar()
    }

    fun hideLoading() {
        progressBar.dismissProgressBar()
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