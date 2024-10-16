package com.senseicoder.quickcart.features.main.ui

import androidx.fragment.app.Fragment
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.ConfirmationDialogFragment
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.global.enums.DialogType
import com.senseicoder.quickcart.databinding.FragmentMapsBinding
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import kotlinx.coroutines.launch

class MapsFragment : Fragment() {
    lateinit var binding: FragmentMapsBinding
    var mapFragment: SupportMapFragment? = null
    var latLng: LatLng = LatLng(-30.0, -30.0)
    var label: String? = null
    private val mainViewModel: MainActivityViewModel by lazy {
        ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
    }

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = latLng
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        googleMap.setOnMapClickListener {
            latLng = it
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(it))
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 9f))
            mainViewModel.setLocation(it.latitude, it.longitude)
            binding.button.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed({
                binding.button.visibility = View.GONE
            }, 3000)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        label = arguments?.getString(Constants.LABEL, null)
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        lifecycleScope.launch {
            mainViewModel.location.collect {
                latLng = LatLng(it.first, it.second)
                mapFragment?.getMapAsync(callback)
            }
        }
        binding.button.setOnClickListener {
            ConfirmationDialogFragment(
                DialogType.SAVE_ADDRESS
            ) {
                if (label.equals(Constants.CART_FRAGMENT_TO_CHECKOUT))
                    Navigation.findNavController(it)
                        .navigate(
                            R.id.action_mapsFragment_to_addressFragment,
                            bundleOf(Constants.LABEL to Constants.CART_FRAGMENT_TO_CHECKOUT),
                        )
                else if(label.equals(Constants.CART_FRAGMENT_TO_EDIT))
                    Navigation.findNavController(it)
                        .navigate(
                            R.id.action_mapsFragment_to_addressFragment,
                            bundleOf(Constants.LABEL to Constants.FROM_ADD)
                        )
                else
                    Navigation.findNavController(it)
                        .navigate(
                            R.id.action_mapsFragment_to_addressFragment,
                            bundleOf(Constants.LABEL to Constants.MAPS_FRAGMENT)
                        )
            }.show(childFragmentManager, "")
        }

    }
}