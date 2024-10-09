package com.senseicoder.quickcart.features.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.dialogs.ConfirmationDialog
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.global.KeyboardUtils
import com.senseicoder.quickcart.core.global.NetworkUtils
import com.senseicoder.quickcart.core.global.isValidEmail
import com.senseicoder.quickcart.core.global.isValidPassword
import com.senseicoder.quickcart.core.global.showErrorSnackbar
import com.senseicoder.quickcart.core.global.showSnackbar
//import com.senseicoder.quickcart.core.network.AdminHandlerImpl
import com.senseicoder.quickcart.core.network.FirebaseHandlerImpl
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.repos.customer.CustomerRepoImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.FragmentLoginBinding
import com.senseicoder.quickcart.features.login.viewmodel.LoginViewModel
import com.senseicoder.quickcart.features.login.viewmodel.LoginViewModelFactory
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity
import com.senseicoder.quickcart.features.main.ui.main_activity.viewmodels.MainActivityViewModel
import kotlinx.coroutines.launch


class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var confirmationDialog: ConfirmationDialog
//    private lateinit var progressBar: CircularProgressIndicatorDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        * used when endIcon is set to custom icon to handle on click events*/
        /*binding.emailLoginLayout.setEndIconOnClickListener{

        }*/
        val factory = LoginViewModelFactory(
            CustomerRepoImpl.getInstance(
                FirebaseHandlerImpl,
                StorefrontHandlerImpl,
                SharedPrefsService
            )
        )
//        progressBar = CircularProgressIndicatorDialog(requireActivity())
        loginViewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
        confirmationDialog = ConfirmationDialog(requireActivity(), null){
//            progressBar.startProgressBar()
            loginViewModel.signupAsGuest()
        }
        confirmationDialog.message = getString(R.string.you_wont_have_access_to_features)
       /* binding.emailLoginEditText.doOnTextChanged { text, start, before, count ->
            binding.emailLoginLayout.error = if(text!!.length> 2){
                "no more!"
            }else{
                null
            }
        }*/
        setupListeners()
        subscribeToObservables()
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).hideBottomNavBar()
        Log.d(TAG, "onStart: ${Navigation.findNavController(requireView()).currentDestination}\n ${Navigation.findNavController(requireView()).currentBackStackEntry}")
    }

    private fun subscribeToObservables(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.CREATED){
                loginViewModel.loginState.collect{
                    when(it){
                        ApiState.Init ->{
                            enableButtons()
//                            progressBar.dismissProgressBar(this@LoginFragment)
                        }
                        ApiState.Loading -> {
                            disableButtons()
//                            progressBar.startProgressBar()
                        }
                        is ApiState.Success -> {
                            enableButtons()
                            ViewModelProvider(requireActivity())[MainActivityViewModel::class.java].updateCurrentUser(it.data)
//                            progressBar.dismissProgressBar(this@LoginFragment)
                            try{
                                findNavController().graph.setStartDestination(R.id.homeFragment)
                                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                            }catch(e:Exception){

                            }
                        }
                        is ApiState.Failure -> {
                            enableButtons()
//                            progressBar.dismissProgressBar(this@LoginFragment)
                            showErrorSnackbar(
                                when(it.msg){
                                    Constants.Errors.UNKNOWN -> getString(R.string.something_went_wrong)
                                    else -> it.msg
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    private fun disableButtons(){
        binding.loginButton.isEnabled = false
        binding.loginText.isEnabled = false
    }

    private fun enableButtons(){
        binding.loginButton.isEnabled = true
        binding.loginText.isEnabled = true
    }

    private fun clearFocuses(){
        binding.apply {
            emailLoginLayout.clearFocus()
            emailPasswordLayout.clearFocus()
        }
    }

    private fun setupListeners(){
        binding.apply {
            loginButton.setOnClickListener{
                validateFields()
            }
            continueAsAGuestButton.setOnClickListener {
                confirmationDialog.showDialog()
            }
            loginText.setOnClickListener{
//                findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
                Navigation.findNavController(it).navigate(R.id.action_loginFragment_to_signupFragment)
            }
            passwordLoginEditText.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    validateFields()
                    return@OnEditorActionListener true
                }
                false
            })
        }
    }

    private fun validateFields(){
        hideValidationErrors()
        KeyboardUtils.hideKeyboard(requireActivity())
        clearFocuses()
        binding.apply {
            val email: String = emailLoginEditText.text.toString()
            val password: String = passwordLoginEditText.text.toString()
            val areFieldsValid = email.isValidEmail() && password.isValidPassword()
            //TODO: handle rest of login validation errors
            if(areFieldsValid){
                if(NetworkUtils.isConnected(requireContext())){
                    loginViewModel.loginUsingNormalEmail(email, password)
                }else{
                    binding.root.showSnackbar(getString(R.string.no_internet_connection))
                }
            }else{
                if(!email.isValidEmail()){
                    emailLoginLayout.error = getString(R.string.email_invalid)
                }
                if(!password.isValidPassword()){
                    /** Minimum six characters, at least one letter and one number:*/
                    emailPasswordLayout.error = if(password.length < 6){
                        getString(R.string.password_invalid_length_small)
                    }else {
                        getString(R.string.password_invalid_at_least_1_letter_1_number)
                    }
                }
            }
        }
    }

    private fun hideValidationErrors() {
        binding.apply {
            emailPasswordLayout.error = null
            emailLoginLayout.error = null
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}