package com.senseicoder.quickcart.features.main.ui.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.ConfirmationDialogFragment
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.global.enums.DialogType
import com.senseicoder.quickcart.core.global.showErrorSnackbar
import com.senseicoder.quickcart.core.global.showSnackbar
import com.senseicoder.quickcart.core.model.Currency
import com.senseicoder.quickcart.core.model.CurrencyResponse
import com.senseicoder.quickcart.core.model.Meta
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.network.currency.CurrencyRemoteImpl
import com.senseicoder.quickcart.core.repos.address.AddressRepoImpl
import com.senseicoder.quickcart.core.repos.currency.CurrencyRepoImpl
import com.senseicoder.quickcart.core.repos.customer.CustomerRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.BottomSheetAboutUsBinding
import com.senseicoder.quickcart.databinding.BottomSheetCurrencyBinding
import com.senseicoder.quickcart.databinding.FragmentProfileBinding
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModelFactory
import com.senseicoder.quickcart.features.main.ui.profile.viewmodel.ProfileViewModel
import com.senseicoder.quickcart.features.main.ui.profile.viewmodel.ProfileViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    private var isConnecting = false
    private var isConnect: Boolean = false
    private lateinit var viewModel: ProfileViewModel
    private val globalCoroutineScope = CoroutineScope(Dispatchers.Main)
    private val buttons: MutableList<RadioButton> = mutableListOf()
    private lateinit var bottomSheetCurrencyBinding: BottomSheetCurrencyBinding
    private lateinit var bottomSheetAboutUsBinding: BottomSheetAboutUsBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    var code: String = SharedPrefsService.getSharedPrefString(
        Constants.CURRENCY,
        Constants.CURRENCY_DEFAULT
    )
    private lateinit var binding: FragmentProfileBinding
    private val mainViewmodel: MainActivityViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            MainActivityViewModelFactory(
                CurrencyRepoImpl(
                    CurrencyRemoteImpl
                ),
                AddressRepoImpl(
                    StorefrontHandlerImpl,
                    SharedPrefsService
                )
            )
        )[MainActivityViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val factory = ProfileViewModelFactory(
            CustomerRepoImpl.getInstance(),
            currencyRepo = CurrencyRepoImpl(CurrencyRemoteImpl)
        )
        viewModel = ViewModelProvider(this, factory)[ProfileViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            lifecycleScope.launch {
                MainActivity.isNetworkAvailable.collect {
                    isConnect = it
                    if (isConnect) {
                        setDataAndListener()
                        binding.apply {
                            readyToUse.visibility = View.VISIBLE
                            networkLottie.visibility = View.GONE
                        }
                    } else {
                        deleteListener()
                        binding.apply {
                            readyToUse.visibility = View.GONE
                            networkLottie.visibility = View.VISIBLE
                        }
                        globalCoroutineScope.cancel()
                    }
                }
            }
            btnAboutUs.setOnClickListener {
                showAboutUsBottomSheet()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).showBottomNavBar()
    }

    override fun onStop() {
        super.onStop()
        (requireActivity() as MainActivity).apply {
            if (findNavController().currentDestination!!.id == R.id.homeFragment
                || findNavController().currentDestination!!.id == R.id.favoriteFragment
                || findNavController().currentDestination!!.id == R.id.shoppingCartFragment
                || findNavController().currentDestination!!.id == R.id.profileFragment
            ) {
                showBottomNavBar()
            } else {
                hideBottomNavBar()
            }
        }
    }

    private fun setUserInfo() {
        binding.apply {
            SharedPrefsService.apply {
                Constants.apply {
                    "HI!\n${
                        getSharedPrefString(
                            USER_DISPLAY_NAME,
                            USER_DISPLAY_NAME_DEFAULT
                        )
                    }".also { txtNameOfPerson.text = it }
                    txtEmailOfPerson.text = getSharedPrefString(USER_EMAIL, USER_EMAIL_DEFAULT)
                }
            }

        }
    }

    private fun showCurrencyBottomSheet() {
        bottomSheetCurrencyBinding = BottomSheetCurrencyBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        bottomSheetDialog.window?.apply {
            val layoutParams = ViewGroup.LayoutParams(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                (resources.displayMetrics.heightPixels * 0.9).toInt()
            )
            setLayout(layoutParams.width, layoutParams.height)
        }
        bottomSheetDialog.setContentView(bottomSheetCurrencyBinding.root)
        prepareCurrencyDataAndSetListener()
        bottomSheetDialog.show()
    }

    private fun showAboutUsBottomSheet() {
        bottomSheetAboutUsBinding = BottomSheetAboutUsBinding.inflate(layoutInflater)
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        bottomSheetDialog.window?.apply {
            val layoutParams = ViewGroup.LayoutParams(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                (resources.displayMetrics.heightPixels * 0.9).toInt()
            )
            setLayout(layoutParams.width, layoutParams.height)
        }
        bottomSheetDialog.setContentView(bottomSheetAboutUsBinding.root)
        bottomSheetDialog.show()
    }

    private fun prepareCurrencyDataAndSetListener() {
        code = SharedPrefsService.getSharedPrefString(
            Constants.CURRENCY,
            Constants.CURRENCY_DEFAULT
        )
        Log.d(
            TAG, "onViewCreated: ${
                SharedPrefsService.getSharedPrefString(
                    Constants.CURRENCY,
                    Constants.CURRENCY_DEFAULT
                )
            } - ${
                SharedPrefsService.getSharedPrefFloat(
                    Constants.PERCENTAGE_OF_CURRENCY_CHANGE,
                    Constants.PERCENTAGE_OF_CURRENCY_CHANGE_DEFAULT
                )
            }"
        )
        startCollectCurrencyChange()
        bottomSheetCurrencyBinding.apply {
            Constants.Currency.currencyMap.forEach {
                rdGroup.addView(
                    RadioButton(binding.root.context).apply {
                        buttons.add(this)
                        text = String.format("${it.key} - ${it.value}")
                        textSize = 24f
                        buttonTintList =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.primary
                                )
                            )
                        val params = LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 16, 0, 16)
                            setPadding(16, 0, 0, 0)
                        }
                        layoutParams = params // Apply the LayoutParams to the RadioButton
                    }
                )
            }
            buttons.forEach {
                if (it.text.toString().substring(0, 3) == code) {
                    it.isChecked = true
                }
            }
            rdGroup.setOnCheckedChangeListener { l, p ->
                val rdBtnChecked = l.findViewById<RadioButton>(p)
                buttons.forEach {
                    it.isChecked = false
                }
                rdBtnChecked.isChecked = true
                code = rdBtnChecked.text.toString().substring(0, 3)
                Log.d(TAG, "prepareCurrencyDataAndSetListener:")
                showSnackbar("Wait. . . .", color = R.color.black)
                mainViewmodel.getCurrencyRate(code)
                rdGroup.removeAllViews()
                bottomSheetDialog.dismiss()
            }
        }
    }

    private fun setChangeInCurrency(response: CurrencyResponse) {
        SharedPrefsService.apply {
            Constants.apply {
                Log.d(TAG, "setChangeInCurrency: $response")
                setSharedPrefString(CURRENCY, code)
                Log.d(TAG, "setChangeInCurrency:code: ${code}")
                Log.d(
                    TAG,
                    "setChangeInCurrency:to float ${response.data[code]?.value?.toFloat()}"
                )
                setSharedPrefFloat(
                    PERCENTAGE_OF_CURRENCY_CHANGE,
                    response.data[code]?.value?.toFloat() ?: 1f
                )
            }
        }
    }

    private fun startCollectCurrencyChange() {
        if (isConnect) {
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                    mainViewmodel.currency.collect {
                        when (it) {
                            is ApiState.Success -> {
                                val res = it.data
                                Log.d(TAG, "startCollect: $res")
                                if ((res.data["EGP"]?.code).equals("EGP") ?: false)
                                    setChangeInCurrency(CurrencyResponse.x)
                                else
                                    setChangeInCurrency(res)

                                showSnackbar(
                                    "Success Changed Currency",
                                    color = R.color.secondary
                                )
                            }
                            is ApiState.Failure -> {
                                showErrorSnackbar(it.msg)
                            }

                            else -> {}
                        }
                    }
                }
            }
        } else {
            showSnackbar("Missing Connection", color = R.color.red)
        }
    }

    private fun deleteListener() {
        binding.apply {
            btnChangeAddress.setOnClickListener(null)
            btnChangeCurrency.setOnClickListener(null)
            btnOrderHistory.setOnClickListener(null)
            btnHowToUse.setOnClickListener(null)
            btnLogOut.setOnClickListener(null)
            btnWishList.setOnClickListener(null)
        }
    }

    private fun setDataAndListener() {
        binding.apply {
            setUserInfo()
            btnChangeAddress.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(R.id.action_profileFragment_to_addressFragment)
            }
            btnChangeCurrency.setOnClickListener {
                showCurrencyBottomSheet()
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
                    mainViewmodel.updateAllAddress(ApiState.Loading)
                }.show(childFragmentManager, null)
            }
            btnWishList.setOnClickListener {
                Navigation.findNavController(it)
                    .navigate(R.id.action_profileFragment_to_favoriteFragment)
            }
        }
    }

    companion object {
        private const val TAG = "ProfileFragment"
    }
}