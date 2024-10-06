package com.senseicoder.quickcart.features.main.ui.currency

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.databinding.FragmentCurrencyBinding

class CurrencyFragment : Fragment() {
    private lateinit var binding: FragmentCurrencyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentCurrencyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            imgBtnBack.setOnClickListener{
                Navigation.findNavController(it).popBackStack()
            }
        }
    }

}