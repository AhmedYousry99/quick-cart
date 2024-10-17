package com.senseicoder.quickcart.features.main.ui.address

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.ConfirmationDialogFragment
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.global.enums.DialogType
import com.senseicoder.quickcart.core.global.showErrorSnackbar
import com.senseicoder.quickcart.core.global.showSnackbar
import com.senseicoder.quickcart.core.model.AddressOfCustomer
import com.senseicoder.quickcart.core.model.fromEdges
import com.senseicoder.quickcart.core.model.toMailingAddressInput
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.repos.address.AddressRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.BottomSheetAddressBinding
import com.senseicoder.quickcart.databinding.FragmentAddressBinding
import com.senseicoder.quickcart.features.main.ui.address.viewmodel.AddressViewModel
import com.senseicoder.quickcart.features.main.ui.address.viewmodel.AddressViewModelFactory
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import com.storefront.type.MailingAddressInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Locale

class AddressFragment : Fragment(), OnAddressClickListener {
    private var isConnecting = false
    var flagToPopUp = false
    var flagToPopUFromEdit = false
    private val globalCoroutineScope = CoroutineScope(Dispatchers.Main)
    lateinit var binding: FragmentAddressBinding
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var bottomSheetBinding: BottomSheetAddressBinding
    private val geoCoder: Geocoder by lazy {
        Geocoder(requireContext(), Locale.getDefault())
    }
    lateinit var addresses: List<Address>
    lateinit var address: Address
    private val mainViewModel: MainActivityViewModel by lazy {
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
    private lateinit var addressAdapter: AddressAdapter
    private var label: String? = null

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
        lifecycleScope.launch {
            MainActivity.isNetworkAvailable.collect {
                isConnecting = it
                if (isConnecting) {
                    binding.apply {
                        readytoShow.visibility = View.VISIBLE
                        animationViewNetwork.visibility = View.GONE
                    }
                    collector()
                    setListenerForFloatingBtn()
                    viewModel.getCustomerAddresses()
                } else {
                    binding.apply {
                        readytoShow.visibility = View.GONE
                        animationViewNetwork.visibility = View.VISIBLE
                    }
                    globalCoroutineScope.cancel()
                    binding.floatBtnAddAddress.setOnClickListener(null)
                }
            }
        }
        SharedPrefsService.logAllSharedPref(TAG, "onViewCreated")
        label = arguments?.getString(Constants.LABEL, null)
        if (!label.isNullOrEmpty()) {
            if (label == Constants.MAPS_FRAGMENT || label == Constants.CART_FRAGMENT_TO_CHECKOUT || label == Constants.FROM_ADD) {
                shoBottomSheet()
            } else if (label.equals(Constants.CART_FRAGMENT_TO_ADD))
                findNavController().navigate(
                    R.id.action_addressFragment_to_mapsFragment,
                    bundleOf(Constants.LABEL to Constants.CART_FRAGMENT_TO_CHECKOUT)
                )
        }

        binding.apply {
            imgBtnBack.setOnClickListener {
                Navigation.findNavController(it).popBackStack()
            }

        }
    }

    private fun collector() {
        globalCoroutineScope.launch {
            viewModel.allAddresses.collect {
                when (it) {
                    is ApiState.Loading -> {
                        ifLoading()
                    }

                    is ApiState.Success -> {
                        val res = it.data.addresses.edges
                        if (res.isNotEmpty()) {
                            mainViewModel.updateAllAddress(ApiState.Success(it.data))
                            ifSuccessAndData()
                            addressAdapter = AddressAdapter(
                                sortDefault(res.fromEdges(), it.data.defaultAddress?.id ?: ""),
                                this@AddressFragment
                            )
                            binding.rvAddress.adapter = addressAdapter
                            if (label.equals(Constants.CART_FRAGMENT_TO_ADD) || label.equals(
                                    Constants.CART_FRAGMENT_TO_CHECKOUT
                                ) ||
                                flagToPopUp
                            ) findNavController().popBackStack()

                            if (label.equals(Constants.FROM_ADD))
                                flagToPopUFromEdit = true
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

    override fun onDeleteClick(addressOfCustomer: AddressOfCustomer) {
        showSnackbar("Wait. . .", color = R.color.black)
        ConfirmationDialogFragment(
            DialogType.DEL_ADDRESS
        ) {
            //DELETE ADDRESS
            viewModel.deleteAddress(addressOfCustomer.id)
            globalCoroutineScope.launch {
                viewModel.deletedAddress.collect {
                    when (it) {
                        is ApiState.Success -> {
                            showSnackbar("Address deleted", color = R.color.secondary)
                        }

                        is ApiState.Failure -> {
                            showErrorSnackbar("Something went wrong")
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

    override fun onDefaultClick(addressOfCustomer: AddressOfCustomer) {
        showSnackbar("Wait. .  .  .", color = R.color.black)
        globalCoroutineScope.launch {
            viewModel.updateDefaultAddressProcess.collect {
                when (it) {
                    true -> {
                        showSnackbar("Address Changed", color = R.color.secondary)
                    }

                    false -> {
                        showErrorSnackbar("Something went wrong")
                    }
                }
            }
        }
        addressAdapter.updateList(emptyList())
        viewModel.updateDefaultAddress(addressOfCustomer.id)
        if (label.equals(Constants.CART_FRAGMENT_TO_ADD) ||
            label.equals(Constants.CART_FRAGMENT_TO_CHECKOUT) ||
            label.equals(Constants.FROM_ADD) ||
            label.equals(Constants.CART_FRAGMENT_TO_EDIT)
        )
            flagToPopUp = true

    }

    private fun shoBottomSheet() {
        bottomSheetBinding = BottomSheetAddressBinding.inflate(layoutInflater)
        bottomSheetBinding.apply {
            globalCoroutineScope.launch {
                mainViewModel.location.collect {
                    geocodeLocation(it.first, it.second)
                }
            }
            bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
            bottomSheetDialog.setContentView(this.root)
            bottomSheetDialog.show()
            resetError(this)
            setupBottomSheet(this)

        }


    }

    private fun setupBottomSheet(bottomBinding: BottomSheetAddressBinding) {
        bottomBinding.apply {
            submitAddressButton.setOnClickListener {
                clearFocus(this)
                if (checkFields(this)) {
                    viewModel.createAddress(readyAddressToSave())
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

    private fun resetError(bottomBinding: BottomSheetAddressBinding) {
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
            txtCountryEditText.addTextChangedListener {
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
            if (txtCityEditText.text.toString().isBlank() ||
                txtCountryEditText.text.toString().isBlank()
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
            shimmerFrameLayout.apply {
                visibility = View.VISIBLE
                shimmerFrameLayout.startShimmer()
            }
            dataGroup.visibility = View.GONE
            animationView.visibility = View.GONE
        }
    }

    private fun ifSuccessAndData() {
        binding.apply {
            shimmerFrameLayout.visibility = View.GONE
            shimmerFrameLayout.stopShimmer()
            animationView.visibility = View.GONE
            dataGroup.visibility = View.VISIBLE
        }
    }

    private fun isSuccessAndNoData() {
        binding.apply {
            shimmerFrameLayout.visibility = View.GONE
            shimmerFrameLayout.stopShimmer()
            dataGroup.visibility = View.VISIBLE
            animationView.visibility = View.VISIBLE
        }
    }
    fun geocodeLocation(latitude: Double, longitude: Double) {
        globalCoroutineScope.launch {
            try {
                Log.d(TAG, "geocodeLocation: ${latitude},${longitude}")
                addresses = geoCoder.getFromLocation(latitude, longitude, 1) ?: emptyList()
                if (addresses.isNotEmpty()) {
                    address = addresses.get(0);

                    val addressLine: String = address.getAddressLine(0); // Full address
                    Log.d(TAG, "geocodeLocation: ${addressLine}")
                    val strings = addressLine.split(",")
                    bottomSheetBinding.apply {
                        txtCountryEditText.setText(strings.last())
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
                txtCountryEditText.text?.trimStart().toString(),
                phoneEditText.text.toString(),
                firstNameEditText.text.toString(),
                lastNameEditText.text.toString()
            ).toMailingAddressInput()
        }


    }
    fun sortDefault(list: List<AddressOfCustomer>, id: String): List<AddressOfCustomer> {
        val res: MutableList<AddressOfCustomer> = mutableListOf()
        list.forEach {
            if (it.id == id)
                res.add(0, it)
            else
                res.add(it)
        }
        return res
    }
    private fun setListenerForFloatingBtn(){
        binding.apply {
            floatBtnAddAddress.setOnClickListener {
                if (label.equals(Constants.CART_FRAGMENT_TO_EDIT))
                    Navigation.findNavController(it)
                        .navigate(
                            R.id.action_addressFragment_to_mapsFragment,
                            bundleOf(Constants.LABEL to Constants.CART_FRAGMENT_TO_EDIT)
                        )
                else
                    Navigation.findNavController(it)
                        .navigate(R.id.action_addressFragment_to_mapsFragment)
            }
        }
    }

    companion object {
        private const val TAG = "AddressFragment"
    }

}
