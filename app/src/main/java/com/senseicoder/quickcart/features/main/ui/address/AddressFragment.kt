package com.senseicoder.quickcart.features.main.ui.address

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.senseicoder.quickcart.databinding.FragmentAddressBinding

class AddressFragment: Fragment() {
    lateinit var binding:FragmentAddressBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAddressBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            imgBtnBack.setOnClickListener{
                Navigation.findNavController(it).popBackStack()
            }
            floatBtnAddAddress.setOnClickListener{
                //TODO: add address
            }
        }
    }
}