package com.senseicoder.quickcart.features.main.ui.category

import android.os.Bundle
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
import androidx.recyclerview.widget.RecyclerView
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.global.NetworkUtils
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.network.currency.CurrencyRemoteImpl
import com.senseicoder.quickcart.core.repos.address.AddressRepo
import com.senseicoder.quickcart.core.repos.address.AddressRepoImpl
import com.senseicoder.quickcart.core.repos.currency.CurrencyRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.NetworkConnectivity
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.FragmentCategoryBinding
import com.senseicoder.quickcart.features.main.ui.category.viewmodel.CategoryViewModel
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CategoryFragment : Fragment(), OnItemProductClicked {

    private var _binding: FragmentCategoryBinding? = null
    private lateinit var categoryViewModel: CategoryViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private var subCategory = "kid"
    private val binding get() = _binding!!

    private val networkConnectivity by lazy {
        NetworkConnectivity.getInstance(requireActivity().application)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        categoryViewModel =
            ViewModelProvider(this)[CategoryViewModel::class.java]

        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefresher.setColorSchemeResources(R.color.primary_faint)

        binding.swipeRefresher.setOnRefreshListener {
            refresh()
        }

        binding.shoesCategory.setBackgroundResource(R.color.primary_faint)
        binding.accessoriesCategory.setBackgroundResource(R.color.white)
        binding.shirtCategory.setBackgroundResource(R.color.white)
        binding.shoesMainCategoryText.setTextColor(resources.getColor(R.color.white))
        binding.accessoriesMainCategoryText.setTextColor(resources.getColor(R.color.primary_faint))
        binding.shirtMainCategoryText.setTextColor(resources.getColor(R.color.primary_faint))

        binding.kid.setBackgroundResource(R.color.primary_faint)
        binding.men.setBackgroundResource(R.color.white)
        binding.women.setBackgroundResource(R.color.white)
        binding.sale.setBackgroundResource(R.color.white)
        binding.kid.setTextColor(resources.getColor(R.color.white))
        binding.men.setTextColor(resources.getColor(R.color.primary_faint))
        binding.women.setTextColor(resources.getColor(R.color.primary_faint))
        binding.sale.setTextColor(resources.getColor(R.color.primary_faint))

        binding.shoesCategory.setOnClickListener {
            binding.shoesCategory.setBackgroundResource(R.color.primary_faint)
            binding.accessoriesCategory.setBackgroundResource(R.color.white)
            binding.shirtCategory.setBackgroundResource(R.color.white)
            binding.shoesMainCategoryText.setTextColor(resources.getColor(R.color.white))
            binding.accessoriesMainCategoryText.setTextColor(resources.getColor(R.color.primary_faint))
            binding.shirtMainCategoryText.setTextColor(resources.getColor(R.color.primary_faint))
            categoryViewModel.filterMainCategory = true
            categoryViewModel.filterByMainCategory("SHOES")
            categoryViewModel.filterBySubCategory(subCategory)
        }

        binding.accessoriesCategory.setOnClickListener {
            binding.shoesCategory.setBackgroundResource(R.color.white)
            binding.accessoriesCategory.setBackgroundResource(R.color.primary_faint)
            binding.shirtCategory.setBackgroundResource(R.color.white)
            binding.shoesMainCategoryText.setTextColor(resources.getColor(R.color.primary_faint))
            binding.accessoriesMainCategoryText.setTextColor(resources.getColor(R.color.white))
            binding.shirtMainCategoryText.setTextColor(resources.getColor(R.color.primary_faint))
            categoryViewModel.filterMainCategory = true
            categoryViewModel.filterByMainCategory("ACCESSORIES")
            categoryViewModel.filterBySubCategory(subCategory)
        }

        binding.shirtCategory.setOnClickListener {
            binding.shoesCategory.setBackgroundResource(R.color.white)
            binding.accessoriesCategory.setBackgroundResource(R.color.white)
            binding.shirtCategory.setBackgroundResource(R.color.primary_faint)
            binding.shoesMainCategoryText.setTextColor(resources.getColor(R.color.primary_faint))
            binding.accessoriesMainCategoryText.setTextColor(resources.getColor(R.color.primary_faint))
            binding.shirtMainCategoryText.setTextColor(resources.getColor(R.color.white))
            categoryViewModel.filterMainCategory = true
            categoryViewModel.filterByMainCategory("T-SHIRTS")
            categoryViewModel.filterBySubCategory(subCategory)
        }

        binding.kid.setOnClickListener {
            binding.kid.setBackgroundResource(R.color.primary_faint)
            binding.men.setBackgroundResource(R.color.white)
            binding.women.setBackgroundResource(R.color.white)
            binding.sale.setBackgroundResource(R.color.white)
            binding.kid.setTextColor(resources.getColor(R.color.white))
            binding.men.setTextColor(resources.getColor(R.color.primary_faint))
            binding.women.setTextColor(resources.getColor(R.color.primary_faint))
            binding.sale.setTextColor(resources.getColor(R.color.primary_faint))
            categoryViewModel.filterSubCategory = true
            subCategory = "kid"
            categoryViewModel.filterBySubCategory("kid")
            // Reset to SHOES when subcategory is selected
            binding.shoesCategory.performClick()
        }

        binding.men.setOnClickListener {
            binding.kid.setBackgroundResource(R.color.white)
            binding.men.setBackgroundResource(R.color.primary_faint)
            binding.women.setBackgroundResource(R.color.white)
            binding.sale.setBackgroundResource(R.color.white)
            binding.kid.setTextColor(resources.getColor(R.color.primary_faint))
            binding.men.setTextColor(resources.getColor(R.color.white))
            binding.women.setTextColor(resources.getColor(R.color.primary_faint))
            binding.sale.setTextColor(resources.getColor(R.color.primary_faint))
            categoryViewModel.filterSubCategory = true
            subCategory = "men"
            categoryViewModel.filterBySubCategory("men")
            // Reset to SHOES when subcategory is selected
            binding.shoesCategory.performClick()
        }

        binding.women.setOnClickListener {
            binding.kid.setBackgroundResource(R.color.white)
            binding.men.setBackgroundResource(R.color.white)
            binding.women.setBackgroundResource(R.color.primary_faint)
            binding.sale.setBackgroundResource(R.color.white)
            binding.kid.setTextColor(resources.getColor(R.color.primary_faint))
            binding.men.setTextColor(resources.getColor(R.color.primary_faint))
            binding.women.setTextColor(resources.getColor(R.color.white))
            binding.sale.setTextColor(resources.getColor(R.color.primary_faint))
            categoryViewModel.filterSubCategory = true
            subCategory = "women"
            categoryViewModel.filterBySubCategory("women")
            // Reset to SHOES when subcategory is selected
            binding.shoesCategory.performClick()
        }

        binding.sale.setOnClickListener {
            binding.kid.setBackgroundResource(R.color.white)
            binding.men.setBackgroundResource(R.color.white)
            binding.women.setBackgroundResource(R.color.white)
            binding.sale.setBackgroundResource(R.color.primary_faint)
            binding.kid.setTextColor(resources.getColor(R.color.primary_faint))
            binding.men.setTextColor(resources.getColor(R.color.primary_faint))
            binding.women.setTextColor(resources.getColor(R.color.primary_faint))
            binding.sale.setTextColor(resources.getColor(R.color.white))
            categoryViewModel.filterSubCategory = true
            subCategory = "sale"
            categoryViewModel.filterBySubCategory("sale")
            // Reset to SHOES when subcategory is selected
            binding.shoesCategory.performClick()
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                categoryViewModel.products.collectLatest {
                    when (it) {
                        is ApiState.Loading -> {
                            if (networkConnectivity.isOnline()) {
                                binding.recyclerView.visibility = View.GONE
                                binding.shimmerFrameLayout.startShimmer()
                                binding.noConnectivity.visibility = View.GONE
                            } else {
                                binding.noConnectivity.visibility = View.VISIBLE
                                binding.connectivity.visibility = View.GONE
                            }
                        }

                        is ApiState.Success -> {
                            binding.shimmerFrameLayout.visibility = View.GONE
                            binding.shimmerFrameLayout.stopShimmer()
                            if(NetworkUtils.isConnected(requireContext())){
                                binding.recyclerView.visibility = View.VISIBLE
                                if (!categoryViewModel.filterMainCategory) {
                                    if (it.data.isNotEmpty()) {
                                        categoryViewModel.allData = it.data
                                        categoryViewModel.filterMainCategory = true
                                        categoryViewModel.filterByMainCategory("SHOES")
                                        categoryViewModel.filterSubCategory = true
                                        subCategory = "kid"
                                        categoryViewModel.filterBySubCategory(subCategory)
                                    } else {
                                        categoryViewModel.getProducts()
                                    }
                                }

                                categoryAdapter = CategoryAdapter(this@CategoryFragment)
                                binding.recyclerView.apply {
                                    adapter = categoryAdapter
                                    categoryAdapter.submitList(it.data)
                                    layoutManager = GridLayoutManager(context, 2).apply {
                                        orientation = RecyclerView.VERTICAL
                                    }
                                }
                            }else{
                                binding.noConnectivity.visibility = View.VISIBLE
                                binding.connectivity.visibility = View.GONE
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
        Log.d(TAG, "onStart: ")
        (requireActivity() as MainActivity).showBottomNavBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        (requireActivity() as MainActivity).apply{
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun productClicked(id: Long) {
        ViewModelProvider(requireActivity(),
            MainActivityViewModelFactory(
                CurrencyRepoImpl(
                    CurrencyRemoteImpl
                ),
                AddressRepoImpl(
                    StorefrontHandlerImpl,
                    SharedPrefsService
                )
            ))[MainActivityViewModel::class.java].setCurrentProductId(id.toString())
        findNavController().navigate(R.id.action_categoryFragment_to_productDetailsFragment)
    }

    private fun refresh() {
        if (networkConnectivity.isOnline()) {
            binding.connectivity.visibility = View.VISIBLE
            binding.noConnectivity.visibility = View.GONE

            binding.shoesCategory.setBackgroundResource(R.color.primary_faint)
            binding.accessoriesCategory.setBackgroundResource(R.color.white)
            binding.shirtCategory.setBackgroundResource(R.color.white)
            binding.shoesMainCategoryText.setTextColor(resources.getColor(R.color.white))
            binding.accessoriesMainCategoryText.setTextColor(resources.getColor(R.color.primary_faint))
            binding.shirtMainCategoryText.setTextColor(resources.getColor(R.color.primary_faint))

            binding.kid.setBackgroundResource(R.color.primary_faint)
            binding.men.setBackgroundResource(R.color.white)
            binding.women.setBackgroundResource(R.color.white)
            binding.sale.setBackgroundResource(R.color.white)
            binding.kid.setTextColor(resources.getColor(R.color.white))
            binding.men.setTextColor(resources.getColor(R.color.primary_faint))
            binding.women.setTextColor(resources.getColor(R.color.primary_faint))
            binding.sale.setTextColor(resources.getColor(R.color.primary_faint))

            categoryViewModel.filterMainCategory = false
            categoryViewModel.getProducts()

        } else {
            binding.connectivity.visibility = View.GONE
            binding.noConnectivity.visibility = View.VISIBLE
        }

        binding.swipeRefresher.isRefreshing = false
    }

    companion object{
        private const val TAG = "CategoryFragment"
    }
}
