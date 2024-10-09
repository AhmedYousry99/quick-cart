package com.senseicoder.quickcart.features.main.ui.address

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.ConfirmationDialogFragment
import com.senseicoder.quickcart.core.global.enums.DialogType
import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.senseicoder.quickcart.core.model.toMailingAddressInput
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.repos.address.AddressRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.BottomSheetAddressBinding
import com.senseicoder.quickcart.databinding.FragmentAddressBinding
import com.senseicoder.quickcart.features.main.ui.address.viewmodel.AddressViewModel
import com.senseicoder.quickcart.features.main.ui.address.viewmodel.AddressViewModelFactory
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import com.storefront.type.MailingAddressInput
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class AddressFragment : Fragment(), OnAddressClickListener {
    lateinit var binding: FragmentAddressBinding
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var bottomSheetBinding: BottomSheetAddressBinding
    val geoCoder: Geocoder by lazy {
        Geocoder(requireContext(), Locale.getDefault())
    }
    lateinit var addresses: List<Address>
    lateinit var address: Address
    val mainViewModel: MainActivityViewModel by lazy {
        ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
    }
    val viewModel: AddressViewModel by lazy {
        ViewModelProvider(
            this,
            AddressViewModelFactory(
                AddressRepoImpl(
                    StorefrontHandlerImpl,
                    SharedPrefsService
                )
            )
        )[AddressViewModel::class.java]
    }
    private val addressAdapter: AddressAdapter by lazy {
        AddressAdapter(emptyList(), this)
    }
    var label: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getCustomerAddresses()
        label =
            Navigation.findNavController(requireView()).previousBackStackEntry?.destination?.label as String?
        if (!label.isNullOrEmpty() && label.toString() == "MapsFragment") {
            shoBottomSheet()
        }
        binding.apply {
            imgBtnBack.setOnClickListener {
                Navigation.findNavController(it).popBackStack()
            }
            floatBtnAddAddress.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(R.id.action_addressFragment_to_mapsFragment)
            }
            lifecycleScope.launch {
                viewModel.allAddresses.collect {
                    when (it) {
                        is ApiState.Loading -> {
                            ifLoading()
                        }

                        is ApiState.Success -> {
                            if (it.data.isNotEmpty()) {
                                ifSuccessAndData()
                                addressAdapter.updateList(it.data)
                                rvAddress.adapter = addressAdapter
                            } else {
                                isSuccessAndNoData()
                            }
                        }

                        else -> {
                            isSuccessAndNoData()
                            Snackbar.make(
                                requireView(),
                                "Something went wrong",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDeleteClick(addressOfCustomer: AddressOfCustomer) {
        ConfirmationDialogFragment(
            DialogType.DEL_ADDRESS
        ) {
            //DELETE ADDRESS
            viewModel.deleteAddress(addressOfCustomer.id)
            lifecycleScope.launch {
                viewModel.deletedAddress.collect {
                    when (it) {
                        is ApiState.Success -> {
                            viewModel.getCustomerAddresses()
                            Snackbar.make(requireView(), "Address deleted", Snackbar.LENGTH_LONG)
                                .show()
                        }

                        is ApiState.Failure -> {
                            Snackbar.make(
                                requireView(),
                                "Something went wrong",
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        else -> {
                        }
                    }
                }
            }
        }.show(childFragmentManager, null)
    }

    override fun onEditClick(addressOfCustomer: AddressOfCustomer) {
        // TODO("Not yet implemented")
    }

    private fun shoBottomSheet() {
        bottomSheetBinding = BottomSheetAddressBinding.inflate(layoutInflater)
        bottomSheetBinding.apply {
            if (label.equals("MapsFragment")) {
                lifecycleScope.launch {
                    mainViewModel.location.collect {
                        geocodeLocation(it.first, it.second)
                    }
                }
            }
            bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
            bottomSheetDialog.setContentView(this.root)  // Use binding.root instead of dialogView
            bottomSheetDialog.show()
            setListener(this)
            setupBottomSheet(this)
        }


    }

    private fun setupBottomSheet(bottomBinding: BottomSheetAddressBinding) {
        bottomBinding.apply {
            submitAddressButton.setOnClickListener {
                clearFocus(this)
                if (checkFields(this)) {
                    viewModel.createAddress(readyAddressToSave())
                    lifecycleScope.launch {
                        viewModel.createdAddress.collect {
                            Log.d(TAG, "setupBottomSheet: ${it}")
                        }
                    }

                    viewModel.getCustomerAddresses()
                    bottomSheetDialog.dismiss()
                } else {
                    addError(this)
                }
            }
        }

    }

    private fun checkFields(bottomBinding: BottomSheetAddressBinding): Boolean {
        bottomBinding.apply {
            val boolean = (firstNameEditText.text.toString().isNotEmpty()
                    && lastNameEditText.text.toString().isNotEmpty()
                    && phoneEditText.text.toString().isNotEmpty()
                    && streetEditText.text.toString().isNotEmpty())
            return boolean
        }
    }

    private fun setListener(bottomBinding: BottomSheetAddressBinding) {
        bottomBinding.apply {
            firstNameEditText.addTextChangedListener {
                firstNameTextField.error = null
            }
            lastNameEditText.addTextChangedListener {
                lastNameEditText.error = null
            }
            phoneEditText.addTextChangedListener {
                lastNameEditText.error = null
            }
            streetEditText.addTextChangedListener {
                lastNameEditText.error = null
            }
            txtCityEditText.addTextChangedListener {
                lastNameEditText.error = null
            }
            txtVountryEditText.addTextChangedListener {
                lastNameEditText.error = null
            }

        }
    }

    private fun clearFocus(bottomBinding: BottomSheetAddressBinding) {
        bottomBinding.apply {
            firstNameEditText.clearFocus()
            lastNameEditText.clearFocus()
            phoneEditText.clearFocus()
            streetEditText.clearFocus()
        }
    }

    private fun addError(bottomBinding: BottomSheetAddressBinding) {
        bottomBinding.apply {
            if (txtCityEditText.text.toString().isBlank() || txtVountryEditText.text.toString()
                    .isBlank()
            ) {
                bottomSheetDialog.dismiss()
                Snackbar.make(requireView(), "Please pick from map again", Snackbar.LENGTH_LONG)
                    .show()
            } else {
                if (firstNameEditText.text.toString().isBlank())
                    firstNameTextField.error = "Please enter first name"
                else if (lastNameEditText.text.toString().isBlank())
                    lastNameEditText.error = "Please enter last name"
                else if (phoneEditText.text.toString().isBlank())
                    phoneEditText.error = "Please enter phone number"
                else if (streetEditText.text.toString().isBlank())
                    streetEditText.error = "Please enter street name"
                else {
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show()
                    bottomSheetDialog.dismiss()
                }
            }
        }
    }

    private fun ifLoading() {
        binding.apply {
            shimmerFrameLayout.visibility = View.VISIBLE
            shimmerFrameLayout.startShimmer()
            dataGroup.visibility = View.GONE
            animationView.visibility = View.GONE
        }
    }

    private fun ifSuccessAndData() {
        binding.apply {
            shimmerFrameLayout.visibility = View.GONE
            shimmerFrameLayout.stopShimmer()
            dataGroup.visibility = View.VISIBLE
            animationView.visibility = View.GONE
        }
    }

    private fun isSuccessAndNoData() {
        binding.apply {
            shimmerFrameLayout.visibility = View.GONE
            shimmerFrameLayout.stopShimmer()
            dataGroup.visibility = View.GONE
            animationView.visibility = View.VISIBLE
        }
    }

    fun geocodeLocation(latitude: Double, longitude: Double) {
        lifecycleScope.launch {
            try {
                Log.d(TAG, "geocodeLocation: ${latitude},${longitude}")
                addresses = geoCoder.getFromLocation(latitude, longitude, 1) ?: emptyList()
                if (addresses.isNotEmpty()) {
                    address = addresses.get(0);

                    val addressLine: String = address.getAddressLine(0); // Full address
                    Log.d(TAG, "geocodeLocation: ${addressLine}")
                    val strings = addressLine.split(",")
                    bottomSheetBinding.apply {
                        txtVountryEditText.setText(strings.last())
                        txtCityEditText.setText(strings.first())
                    }
                } else {
                    // Handle case where no address was found
                    Log.d(TAG, "geocodeLocation: No address found")
                }
            } catch (e: Exception) {
                // Handle exception, e.g., display an error message
                Log.d(TAG, "geocodeLocation: ${e.message}")
            }
        }

    }

    private fun readyAddressToSave(): MailingAddressInput {
        bottomSheetBinding.apply {
            return AddressOfCustomer(
                "",
                "address1",
                "address2",
                txtCityEditText.text.toString(),
                txtVountryEditText.text?.trimStart().toString(),
                phoneEditText.text.toString(),
                firstNameEditText.text.toString(),
                lastNameEditText.text.toString()
            ).toMailingAddressInput()
        }


    }

    companion object {
        private const val TAG = "AddressFragment"
    }

}
