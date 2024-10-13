package com.senseicoder.quickcart.features.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.databinding.FragmentSplashBinding
import com.senseicoder.quickcart.features.splash.viewmodel.SplashViewModel
import com.senseicoder.quickcart.features.splash.viewmodel.SplashViewModelFactory


class SplashFragment : Fragment() {

    private lateinit var handler: Handler
    private lateinit var binding: FragmentSplashBinding
    private val splashRunnable: Runnable = Runnable {
        val navController = findNavController()
        val factory = SplashViewModelFactory(
            SharedPrefsService
        )
        val viewModel = ViewModelProvider(this, factory)[SplashViewModel::class.java]
        if(viewModel.isUserLoggedIn()) {
            navController.navigate(R.id.action_splashFragment_to_homeFragment)
            navController.graph.setStartDestination(R.id.homeFragment)
        }else{
            navController.navigate(R.id.action_splashFragment_to_loginFragment)
            navController.graph.setStartDestination(R.id.loginFragment)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(splashRunnable, 2000)
    }


}