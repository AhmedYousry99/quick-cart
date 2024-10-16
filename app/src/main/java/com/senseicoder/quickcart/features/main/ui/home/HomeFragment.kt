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
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.MyDialog
import com.senseicoder.quickcart.core.global.NetworkUtils
import com.senseicoder.quickcart.core.model.CouponsForDisplay
import com.senseicoder.quickcart.core.wrappers.NetworkConnectivity
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.FragmentHomeBinding
import com.senseicoder.quickcart.features.main.ui.home.viewmodel.HomeViewModel
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

class HomeFragment : Fragment(), OnItemBrandClicked {

    companion object {
        private const val TAG = "HomeFragment"
    }
    private lateinit var binding: FragmentHomeBinding
    private lateinit var brandAdapter: HomeBrandAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var couponPagerAdapter: CouponPagerAdapter
    private val customCoroutine = CoroutineScope(Dispatchers.Main)
    private val swipeInterval: Long = 3000 // 3 seconds
    private var currentPage = 0
    private lateinit var handler: Handler
    private val HomePager2AnimationRunnable: Runnable = object : Runnable {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(Looper.getMainLooper())
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

        binding.swipeRefresher.setOnRefreshListener {
            refresh()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.brands.collectLatest {
                    when (it) {
                        is ApiState.Loading -> {
                            binding.connectivity.visibility = View.GONE
                            if (networkConnectivity.isOnline()) {
                                // Show shimmer and hide the recycler view during loading
                                binding.shimmerFrameLayout.visibility = View.VISIBLE
                                binding.shimmerFrameLayout.startShimmer()
                                binding.connectivity.visibility = View.GONE
                                binding.noConnectivity.visibility = View.GONE
                            } else {
                                // Handle no connectivity state
                                binding.noConnectivity.visibility = View.VISIBLE
                            }
                        }

                        is ApiState.Success -> {
                            if(NetworkUtils.isConnected(requireContext())){
                                brandAdapter = HomeBrandAdapter(requireContext(), this@HomeFragment)
                                binding.brandRecycle.apply {
                                    brandAdapter.submitList(it.data)
                                    adapter = brandAdapter
                                }
                                delay(1000)
                                binding.shimmerFrameLayout.stopShimmer()
                                binding.shimmerFrameLayout.visibility = View.GONE
                                binding.connectivity.visibility = View.VISIBLE
                            }else{
                                binding.connectivity.visibility = View.GONE
                                binding.noConnectivity.visibility = View.VISIBLE
                            }
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

    }

    override fun onStop() {
        Log.d(TAG, "onStop: ")
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
            toolbarVisibility(false)
        }
    }

    override fun onResume() {
        super.onResume()
        (requireActivity() as MainActivity).apply {
            showBottomNavBar()
            toolbarVisibility(true)
        }

        if(::couponPagerAdapter.isInitialized)
            handler.postDelayed(HomePager2AnimationRunnable, swipeInterval)

    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(HomePager2AnimationRunnable)
    }

    private fun setupPagerListener(){
        binding.couponPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            private var isUserInteracting = false
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
            }
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                when (state) {
                    ViewPager2.SCROLL_STATE_DRAGGING -> {
                        // The user started interacting with the pager (scrolling)
                        isUserInteracting = true
                        handler.removeCallbacks(HomePager2AnimationRunnable)
                    }
                    ViewPager2.SCROLL_STATE_IDLE -> {
                        if (isUserInteracting) {
                            // The pager has stopped after user interaction
                            Log.d(TAG, "onPageScrollStateChanged: ")
                            handler.postDelayed(HomePager2AnimationRunnable, swipeInterval)
                            isUserInteracting = false
                        }
                    }
                }
            }
        })
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
        customCoroutine.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED){
                homeViewModel.coupons.collect{
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
                                    customCoroutine.cancel()
                                }
                            }
                            binding.couponPager.adapter = couponPagerAdapter
                            binding.dotsIndicator.attachTo(binding.couponPager)
                            startSwipingAnimation()
                            setupPagerListener()
                        }

                        is ApiState.Failure -> {
//                            Snackbar.make(requireView(), it.msg, Snackbar.LENGTH_LONG).show()
                            binding.connectivity.visibility = View.GONE
                            binding.shimmerFrameLayout.visibility = View.GONE
                            Log.d(TAG, "setupCouponViewPager: ${it.msg}")
                        }
                        else -> {
                            Log.d(TAG, "setupCouponViewPager: LOADING")
                            handler.removeCallbacks(HomePager2AnimationRunnable)
                        }
                    }
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
            homeViewModel.fetchCoupons()
        } else {
            binding.connectivity.visibility = View.GONE
            binding.noConnectivity.visibility = View.VISIBLE
        }

        binding.swipeRefresher.isRefreshing = false
    }


    private fun startSwipingAnimation() {
        handler.removeCallbacks(HomePager2AnimationRunnable)
        handler.postDelayed(HomePager2AnimationRunnable, swipeInterval)
    }
//    private fun startRandomSwiping() {
//        val swipeRunnable = object : Runnable {
//            override fun run() {
//                val current = binding.couponPager.currentItem
//                val count = couponPagerAdapter.itemCount
////                Log.d(TAG, "run: ${count}")
//                var next = current + if (nextBoolean()) 1 else -1
//                if (next < 0) next = count - 1
//                else if (next >= count) next = 0
//                binding.couponPager.currentItem = next
//                handel.postDelayed(this, 4000L)
//            }
//        }
//        handel.postDelayed(swipeRunnable, 4000)
//    }
}
