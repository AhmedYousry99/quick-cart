package com.senseicoder.quickcart.features.main.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.databinding.FragmentSettingsBinding
import com.senseicoder.quickcart.features.main.MainActivity

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            imgBtnBack.setOnClickListener{
                Navigation.findNavController(it).popBackStack()
            }
            btnChangeAddress.setOnClickListener{
                Navigation.findNavController(it).navigate(R.id.action_settingsFragment_to_addressFragment)
            }
            btnChangeCurrency.setOnClickListener{
                Navigation.findNavController(it).navigate(R.id.action_settingsFragment_to_currencyFragment)
            }
        }
    }


}