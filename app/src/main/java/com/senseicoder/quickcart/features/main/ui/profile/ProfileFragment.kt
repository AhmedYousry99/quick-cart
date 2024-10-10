package com.senseicoder.quickcart.features.main.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.ConfirmationDialogFragment
import com.senseicoder.quickcart.core.global.enums.DialogType
import com.senseicoder.quickcart.databinding.FragmentProfileBinding
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).apply {
            toolbarVisibility(false)
            showBottomNavBar()
        }
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).apply{
            Log.d(TAG, "onStop: ${binding.root.findNavController().currentDestination!!}")
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnSettings.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(R.id.action_profileFragment_to_settingsFragment)
            }
            btnOrderHistory.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(R.id.action_profileFragment_to_orderFragment)
            }
            btnHowToUse.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(R.id.action_profileFragment_to_howToUseFragment)
            }
            btnLogOut.setOnClickListener {
                ConfirmationDialogFragment(DialogType.LOGOUT) {
                    //TODO: logout
                }.show(childFragmentManager, null)
            }
        }
    }

    companion object{
        private const val TAG = "ProfileFragment"
    }
}