package com.senseicoder.quickcart.features.main.ui.home

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.MyDialog
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.model.CouponsForDisplay
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.NetworkConnectivity
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.FragmentHomeBinding
import com.senseicoder.quickcart.features.main.ui.home.viewmodel.HomeViewModel
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.random.Random.Default.nextBoolean

class HomeFragment : Fragment(), OnItemBrandClicked {

    companion object {
        private const val TAG = "HomeFragment"
    }

    private lateinit var binding: FragmentHomeBinding

    private lateinit var brandAdapter: HomeBrandAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var couponPagerAdapter: CouponPagerAdapter

    private val swipeInterval: Long = 3000 // 3 seconds
    private var currentPage = 0
    private lateinit var handler: Handler
    private val productDetailsPager2AnimationRunnable: Runnable = object : Runnable {
        override fun run() {
            val totalPages = binding.couponPager.adapter?.itemCount ?: 0

            // Move to the next page or reset to the first page if at the end
            if (currentPage == totalPages - 1) {
                currentPage = 0
            } else {
                currentPage++
            }

            binding.couponPager.setCurrentItem(currentPage, true)

            // Repeat this runnable every `swipeInterval` milliseconds
            handler.postDelayed(this, swipeInterval)
        }
    }
    val couponImages = listOf(
        R.drawable.twinty,
        R.drawable.rondom_one,
        R.drawable.fiften,
        R.drawable.twinty_five,
        R.drawable.coupon10bg
    )


    private val networkConnectivity by lazy {
        NetworkConnectivity.getInstance(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeViewModel.fetchCoupons()
        brandAdapter = HomeBrandAdapter(requireContext(), this)

        // Set up RecyclerView
        binding.brandRecycle.apply {
            adapter = brandAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        // Setup coupon view pager
        setupCouponViewPager()

        binding.swipeRefresher.setColorSchemeResources(R.color.black)


        if (networkConnectivity.isOnline()) {
            binding.connectivity.visibility = View.VISIBLE
            binding.noConnectivity.visibility = View.GONE
        } else {
            binding.connectivity.visibility = View.GONE
            binding.noConnectivity.visibility = View.VISIBLE
        }

        binding.swipeRefresher.setOnRefreshListener {
            refresh()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.brands.collectLatest {
                    when (it) {
                        is ApiState.Loading -> {
                            if (networkConnectivity.isOnline()) {
                                // Show shimmer and hide the recycler view during loading
                                binding.brandRecycle.visibility = View.GONE
                                binding.shimmerFrameLayout.visibility = View.VISIBLE
                                binding.shimmerFrameLayout.startShimmer()
                                binding.noConnectivity.visibility = View.GONE
                            } else {
                                // Handle no connectivity state
                                binding.connectivity.visibility = View.GONE
                                binding.noConnectivity.visibility = View.VISIBLE
                            }
                        }

                        is ApiState.Success -> {
                            brandAdapter = HomeBrandAdapter(requireContext(), this@HomeFragment)
                            binding.brandRecycle.apply {
                                brandAdapter.submitList(it.data)
                                adapter = brandAdapter
                            }
                            delay(1000)
                            binding.shimmerFrameLayout.stopShimmer()
                            binding.brandRecycle.visibility = View.VISIBLE
                            binding.shimmerFrameLayout.visibility = View.GONE
                        }

                        else -> {
                            if (!networkConnectivity.isOnline()) {
                                binding.connectivity.visibility = View.GONE
                                binding.noConnectivity.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
        handler = Handler(Looper.getMainLooper())
        setupPagerListener()
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).apply {
            showBottomNavBar()
            toolbarVisibility(true)
        }
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).apply {

            if (binding.root.findNavController().currentDestination!!.id == R.id.homeFragment
                || binding.root.findNavController().currentDestination!!.id == R.id.favoriteFragment
                || binding.root.findNavController().currentDestination!!.id == R.id.shoppingCartFragment
                || binding.root.findNavController().currentDestination!!.id == R.id.profileFragment
            ) {
                showBottomNavBar()
            } else {
                hideBottomNavBar()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).toolbarVisibility(true)
        if(homeViewModel.coupons.value !is ApiState.Success)
            handler.postDelayed(productDetailsPager2AnimationRunnable, swipeInterval)
    }

    override fun onPause() {
        super.onPause()
        (requireActivity() as MainActivity).toolbarVisibility(false)
        handler.removeCallbacks(productDetailsPager2AnimationRunnable)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupPagerListener(){
        binding.couponPager.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // The pager is currently being pressed
                    handler.removeCallbacks(productDetailsPager2AnimationRunnable)
                    true // Indicate that the touch event has been consumed
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    handler.postDelayed(productDetailsPager2AnimationRunnable, swipeInterval)
                    // The pager is currently released
                    true // Indicate that the touch event has been consumed
                }
                else -> false // Let the ViewPager handle other touch events
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as MainActivity).apply {
            if (findNavController().currentDestination!!.id == R.id.homeFragment
                || findNavController().currentDestination!!.id == R.id.favoriteFragment
                || findNavController().currentDestination!!.id == R.id.shoppingCartFragment
                || findNavController().currentDestination!!.id == R.id.profileFragment
            ) {
                showBottomNavBar()
            } else {
                hideBottomNavBar()
            }
        }

    }


    private fun setupCouponViewPager() {
        lifecycleScope.launch {
            homeViewModel.coupons.collect {
                when (it) {
                    is ApiState.Success -> {
                        val coupons = it.data.price_rules
                        val displayCoupons =
                            coupons.zip(couponImages) { coupon, image ->
                                CouponsForDisplay(coupon, image)
                            }
                        couponPagerAdapter = CouponPagerAdapter(displayCoupons) { item ->

                            lifecycleScope.launch {
                                val discountCode = item.title
                                val clipboard =
                                    requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip =
                                    ClipData.newPlainText("Discount Code", discountCode)
                                clipboard.setPrimaryClip(clip)
                                Snackbar.make(
                                    requireView(),
                                    "Coupon code copied to clipboard",
                                    Snackbar.LENGTH_LONG
                                ).show()
                            }
                        }
                        binding.couponPager.adapter = couponPagerAdapter
                        startRandomSwiping()
                        binding.dotsIndicator.setViewPager2(binding.couponPager)
                    }

                    is ApiState.Failure -> Log.d(TAG, "setupCouponViewPager: ${it.msg}")
                    else -> Log.d(TAG, "setupCouponViewPager: LOADING")
                }
            }
        }


        // Initialize the adapter

    }

    override fun brandClicked(brand: String) {
        if (networkConnectivity.isOnline()) {
            val action = HomeFragmentDirections.actionHomeFragmentToBrandFragment(brand)
            binding.root.findNavController().navigate(action)
        } else {
            val dialog = MyDialog()
            dialog.showAlertDialog("Please, check your connection", requireContext())
        }
    }

    private fun refresh() {
        if (networkConnectivity.isOnline()) {
            binding.noConnectivity.visibility = View.GONE
            binding.connectivity.visibility = View.VISIBLE
            homeViewModel.brands.value = ApiState.Loading
            homeViewModel.getBrand()

        } else {
            binding.connectivity.visibility = View.GONE
            binding.noConnectivity.visibility = View.VISIBLE
        }

        binding.swipeRefresher.isRefreshing = false
    }

    private fun startRandomSwiping() {
        handler.removeCallbacks(productDetailsPager2AnimationRunnable)
        handler.postDelayed(productDetailsPager2AnimationRunnable, swipeInterval)
    }
}
