package com.senseicoder.quickcart.features.main.ui.main_activity

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.Navigation.findNavController
import androidx.navigation.navOptions
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.ConfirmationDialogFragment
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.global.NetworkUtils
import com.senseicoder.quickcart.core.global.enums.DialogType
import com.senseicoder.quickcart.core.global.showSnackbar
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.network.currency.CurrencyRemoteImpl
import com.senseicoder.quickcart.core.repos.address.AddressRepoImpl
import com.senseicoder.quickcart.core.repos.currency.CurrencyRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.databinding.ActivityMainBinding
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModelFactory
import com.stripe.android.PaymentConfiguration

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private val mainViewModel : MainActivityViewModel by lazy{
        ViewModelProvider(this,
            MainActivityViewModelFactory(CurrencyRepoImpl(CurrencyRemoteImpl),
                AddressRepoImpl(
                    StorefrontHandlerImpl,
                    SharedPrefsService
                )
            )
            )[MainActivityViewModel::class.java]
    }

    private val onDestinationChangedListener =
        NavController.OnDestinationChangedListener { controller, destination, _ ->
            if (!canNavigate(destination.id)) {
                Log.d(TAG, "qwhehq: ${controller.getBackStackEntry(destination.id)}")
                ConfirmationDialogFragment(DialogType.PERMISSION_DENIED_GUEST_MODE) {
                    controller.navigate(R.id.splashFragment, null, navOptions {
                        popUpTo(R.id.loginFragment) {
                            inclusive = true
                        }
                    })
                    controller.graph.setStartDestination(R.id.loginFragment)
                }.show(supportFragmentManager, null)

                controller.popBackStack(destination.id, true)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PaymentConfiguration.init(
            applicationContext,
            BuildConfig.publish_key // Replace with your Stripe publishable key
        /*"pk_test_51Q5iOxB2VRlrbgQ7sEVPYTMVmfplCmtGo5EibD95SbNMis5QoW8IMzkHCloTbbx6uS89wToh9Z3AOqBRF431i44m00nXQ3rTn9"*/)
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

//        progressBar.startProgressBar()

        setSupportActionBar(binding.toolbar)
        binding.toolbar.navigationIcon = null
        mainViewModel
        setupWithNavController(binding.toolbar, navController, appBarConfiguration)
        setupWithNavController(binding.navView, navController)
        binding.imageFavorite.setOnClickListener{
            navController.navigate(R.id.favoriteFragment)
        }
        binding.imageSearch.setOnClickListener{
            navController.navigate(R.id.searchFragment)
        }
        navController.addOnDestinationChangedListener(onDestinationChangedListener)

        // run observable
        NetworkUtils.observeNetworkConnectivity(applicationContext)
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

    private fun canNavigate(destinationId: Int): Boolean {
        if (destinationId == R.id.shoppingCartFragment || destinationId == R.id.profileFragment || destinationId == R.id.favoriteFragment) {
            return SharedPrefsService.getSharedPrefString(Constants.USER_ID, Constants.USER_ID_DEFAULT) != Constants.USER_ID_DEFAULT
        }
        return true
    }

    companion object {
        private const val TAG = "MainActivity"
        val navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
    }
}