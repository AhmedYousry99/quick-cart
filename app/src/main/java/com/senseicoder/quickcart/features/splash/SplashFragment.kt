package com.senseicoder.quickcart.features.splash

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.databinding.FragmentSplashBinding
import com.senseicoder.quickcart.features.splash.viewmodel.SplashViewModel
import com.senseicoder.quickcart.features.splash.viewmodel.SplashViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : Fragment() {

    private lateinit var handler: Handler
    private lateinit var binding: FragmentSplashBinding

    private val splashRunnable: Runnable = Runnable {



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("Recycle")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(splashRunnable, 2000)
        binding.apply {
            txtRight.translationX = 500f
            txtLeft.translationX = -500f
            networkLottie4.translationX = -500f
            val lastAnime = ObjectAnimator.ofFloat(networkLottie4, "translationX", 0f, 700f).apply {
                duration = 1500
            }
            val lastAnimeL = ObjectAnimator.ofFloat(txtLeft, "translationX", 0f, 500f).apply {
                duration = 1500
            }
            val lastAnimeR = ObjectAnimator.ofFloat(txtRight, "translationX", 0f, 500f).apply {
                duration = 1500
            }
//
            val animAnim = ObjectAnimator.ofFloat(networkLottie4, "translationX", 0f).apply {
                duration = 2000
            }
            val animRight = ObjectAnimator.ofFloat(txtRight, "translationX", 0f).apply {
                duration = 2000
                interpolator = BounceInterpolator()
            }
            val animLeft = ObjectAnimator.ofFloat(txtLeft, "translationX", 0f).apply {
                duration = 2000
                interpolator = BounceInterpolator()
            }
            val animatorSet: AnimatorSet = AnimatorSet().apply {
                playTogether(animRight, animLeft, animAnim)
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        lifecycleScope.launch {
                            delay(500)
                            AnimatorSet().apply {
                                playTogether(lastAnime, lastAnimeL, lastAnimeR)
                                start()
                                addListener(object :AnimatorListener{
                                    override fun onAnimationStart(animation: Animator) {
                                    }

                                    override fun onAnimationEnd(animation: Animator) {
                                        check()
                                    }

                                    override fun onAnimationCancel(animation: Animator) { }

                                    override fun onAnimationRepeat(animation: Animator) { }

                                })
                            }
                        }
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }

                })
                start()
            }
        }
    }
    private fun check(){
        val factory = SplashViewModelFactory(
            SharedPrefsService
        )
        val viewModel = ViewModelProvider(this, factory)[SplashViewModel::class.java]
        val navController = findNavController()

         if (viewModel.isUserLoggedIn()) {
            navController.navigate(R.id.action_splashFragment_to_homeFragment)
            navController.graph.setStartDestination(R.id.homeFragment)
        } else {
            navController.navigate(R.id.action_splashFragment_to_loginFragment)
            navController.graph.setStartDestination(R.id.loginFragment)
        }
    }
}