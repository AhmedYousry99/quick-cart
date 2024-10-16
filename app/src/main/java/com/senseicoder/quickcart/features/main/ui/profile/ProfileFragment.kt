package com.senseicoder.quickcart.features.main.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.ConfirmationDialogFragment
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.global.enums.DialogType
import com.senseicoder.quickcart.core.repos.customer.CustomerRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.databinding.FragmentProfileBinding
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity
import com.senseicoder.quickcart.features.main.ui.profile.viewmodel.ProfileViewModel
import com.senseicoder.quickcart.features.main.ui.profile.viewmodel.ProfileViewModelFactory

class ProfileFragment : Fragment() {


        private lateinit var viewModel: ProfileViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ProfileViewModelFactory(
            CustomerRepoImpl.getInstance()
        )
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

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
            setUserInfo()
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
                    viewModel.signOut()
                    Navigation.findNavController(requireView()).apply {
                        navigate(R.id.action_profileFragment_to_splashFragment)
                        graph.setStartDestination(R.id.loginFragment)
                    }
                }.show(childFragmentManager, null)
            }
        }
    }
    private fun setUserInfo(){
        binding.apply {
            SharedPrefsService.apply {
                Constants.apply {
                    "HI!\n${getSharedPrefString(USER_DISPLAY_NAME, USER_DISPLAY_NAME_DEFAULT)}".also { txtNameOfPerson.text = it }
                    txtEmailOfPerson.text = getSharedPrefString(USER_EMAIL, USER_EMAIL_DEFAULT)
                }
            }

        }
    }

    companion object{
        private const val TAG = "ProfileFragment"
    }
}