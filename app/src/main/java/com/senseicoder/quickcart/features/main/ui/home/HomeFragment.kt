
package com.senseicoder.quickcart.features.main.ui.home

import android.os.Bundle
import android.view.LayoutInflater
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
import kotlinx.coroutines.flow.collectLatest
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.MyDialog
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.NetworkConnectivity
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.FragmentHomeBinding
import com.senseicoder.quickcart.features.main.ui.home.viewmodel.HomeViewModel
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), OnItemBrandClicked {

    private lateinit var _binding: FragmentHomeBinding
    private val binding get() = _binding

    private lateinit var brandAdapter: HomeBrandAdapter
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var couponPagerAdapter: CouponPagerAdapter

    private val onDestinationChangedListener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            if (!canNavigate(destination.id)){
                controller.popBackStack()
                Toast.makeText(requireContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
            }
        }

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

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        brandAdapter = HomeBrandAdapter(requireContext(), this)

        // Set up RecyclerView
        binding.brandRecycle.apply {
            adapter = brandAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        // Setup coupon view pager
        setupCouponViewPager()

        binding.swipeRefresher.setColorSchemeResources(R.color.black)

        findNavController().addOnDestinationChangedListener(onDestinationChangedListener)

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
                            // Hide shimmer and show the recycler view when data is loaded
                            binding.brandRecycle.visibility = View.VISIBLE
                            binding.shimmerFrameLayout.visibility = View.GONE
                            binding.shimmerFrameLayout.stopShimmer()

                            // Set up your adapter and layout manager
                            brandAdapter = HomeBrandAdapter(requireContext(), this@HomeFragment)
                            binding.brandRecycle.apply {
                                adapter = brandAdapter
                                brandAdapter.submitList(it.data)
                                layoutManager = GridLayoutManager(context, 2).apply {
                                    orientation = RecyclerView.VERTICAL
                                }
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
            toolbarVisibility(false)
            if (binding.root.findNavController().currentDestination!!.id == R.id.homeFragment
                || binding.root.findNavController().currentDestination!!.id == R.id.favoriteFragment
                || binding.root.findNavController().currentDestination!!.id == R.id.shoppingCartFragment
                || binding.root.findNavController().currentDestination!!.id == R.id.profileFragment
            ){
                showBottomNavBar()
            }else{
                hideBottomNavBar()
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
            ){
                showBottomNavBar()
            }else{
                hideBottomNavBar()
            }
        }
        findNavController().removeOnDestinationChangedListener(onDestinationChangedListener)
    }


    private fun setupCouponViewPager() {

        val couponImages = listOf(
            R.drawable.coupon10bg,
            R.drawable.coupon20bg
        )

        // Initialize the adapter
        couponPagerAdapter = CouponPagerAdapter(couponImages)

        // Set the adapter to ViewPager2
        binding.couponPager.adapter = couponPagerAdapter

        // Link the ViewPager2 with the DotsIndicator
        binding.dotsIndicator.setViewPager2(binding.couponPager)
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

    private fun canNavigate(destinationId: Int): Boolean {
        if (destinationId != R.id.shoppingCartFragment || destinationId == R.id.profileFragment) {
            val isUserGuest = SharedPrefsService.getSharedPrefString(Constants.USER_ID, Constants.USER_ID_DEFAULT) == Constants.USER_ID_DEFAULT
            return !isUserGuest
        }
        return true
    }


}