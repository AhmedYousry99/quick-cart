package com.senseicoder.quickcart.features.main.ui.shopping_cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.repos.cart.CartRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.databinding.FragmentShoppingCartBinding
import com.senseicoder.quickcart.features.main.MainActivity
import com.senseicoder.quickcart.features.main.ui.shopping_cart.viewmodel.ShoppingCartViewModel
import com.senseicoder.quickcart.features.main.ui.shopping_cart.viewmodel.ShoppingCartViewModelFactory

class ShoppingCartFragment : Fragment() {

    private lateinit var binding: FragmentShoppingCartBinding
    private val viewModel: ShoppingCartViewModel by lazy {
        ViewModelProvider(
            this,
            ShoppingCartViewModelFactory(
                CartRepoImpl(
                    StorefrontHandlerImpl,
                    SharedPrefsService
                )
            )
        )[ShoppingCartViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentShoppingCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (/*TODO: check if cart is empty*/false)
            binding.dataGroup.visibility = View.VISIBLE
        else
            binding.dataGroup.visibility = View.GONE

    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).toolbarVisibility(false)
    }
}