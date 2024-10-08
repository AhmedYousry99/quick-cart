package com.senseicoder.quickcart.features.main.ui.product_details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.quickcart.core.model.ReviewDTO
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.repos.cart.CartRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.databinding.FragmentProductDetailsBinding
import com.senseicoder.quickcart.features.main.ui.product_details.viewmodel.ProductDetailsViewModel
import com.senseicoder.quickcart.features.main.ui.product_details.viewmodel.ProductDetailsViewModelFactory

class ProductDetailsFragment : Fragment() {

    private lateinit var binding: FragmentProductDetailsBinding
    private lateinit var viewModel: ProductDetailsViewModel
    private val reviews :List<ReviewDTO> = listOf(
        ReviewDTO(name = "Kareem", description = "Quality is not as advertised.", rating = 2.5),
        ReviewDTO(name = "Dina", description = "Very happy with my purchase.", rating = 4.5),
        ReviewDTO(name = "Ali", description = "Not bad.", rating = 3.0),
        ReviewDTO(name = "Layla", description = "Absolutely love it! Great buy.", rating = 5.0),
        ReviewDTO(name = "Tamer", description = "Product arrived in terrible condition.", rating = 1.0),
        ReviewDTO(name = "Adel", description = "Not worth the price.", rating = 2.0),
        ReviewDTO(name = "Salma", description = "Exceeded my expectations!", rating = 5.0),
        ReviewDTO(name = "Ibrahim", description = "Wouldn't recommend.", rating = 2.0),
        ReviewDTO(name = "Reem", description = "Good, but shipping took too long.", rating = 3.5),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = ProductDetailsViewModelFactory(
            CartRepoImpl.getInstance(
                StorefrontHandlerImpl,
                SharedPrefsService
            )
        )
        viewModel= ViewModelProvider(this, factory)[ProductDetailsViewModel::class.java]
    }

    /*private fun startAutoSwipe() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                val totalPages = viewPager.adapter?.itemCount ?: 0

                // Move to the next page or reset to the first page if at the end
                if (currentPage == totalPages - 1) {
                    currentPage = 0
                } else {
                    currentPage++
                }

                viewPager.setCurrentItem(currentPage, true)

                // Repeat this runnable every `swipeInterval` milliseconds
                handler.postDelayed(this, swipeInterval)
            }
        }, swipeInterval)
    }*/

}