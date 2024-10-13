package com.senseicoder.quickcart.features.signup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.db.remote.FirebaseFirestoreDataSource
import com.senseicoder.quickcart.core.global.Constants
import com.senseicoder.quickcart.core.global.KeyboardUtils
import com.senseicoder.quickcart.core.global.NetworkUtils
import com.senseicoder.quickcart.core.global.isValidEmail
import com.senseicoder.quickcart.core.global.isValidPassword
import com.senseicoder.quickcart.core.global.matchesPassword
import com.senseicoder.quickcart.core.global.showErrorSnackbar
import com.senseicoder.quickcart.core.global.showSnackbar
import com.senseicoder.quickcart.core.network.ApiService
import com.senseicoder.quickcart.core.repos.customer.CustomerRepoImpl
//import com.senseicoder.quickcart.core.network.AdminHandlerImpl
import com.senseicoder.quickcart.core.network.FirebaseHandlerImpl
import com.senseicoder.quickcart.core.network.StorefrontHandlerImpl
import com.senseicoder.quickcart.core.network.customer.CustomerAdminDataSourceImpl
import com.senseicoder.quickcart.core.services.SharedPrefsService
import com.senseicoder.quickcart.core.wrappers.ApiState
import com.senseicoder.quickcart.databinding.FragmentSignupBinding
import com.senseicoder.quickcart.features.main.ui.main_activity.MainActivity
import com.senseicoder.quickcart.features.signup.viewmodel.SignupViewModel
import com.senseicoder.quickcart.features.signup.viewmodel.SignupViewModelFactory
import kotlinx.coroutines.launch


class SignupFragment : Fragment() {


    private lateinit var binding: FragmentSignupBinding
    private lateinit var signupViewModel: SignupViewModel
//    private lateinit var progressBar: CircularProgressIndicatorDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignupBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory = SignupViewModelFactory(
            CustomerRepoImpl.getInstance(
                FirebaseHandlerImpl,
                StorefrontHandlerImpl,
                SharedPrefsService,
                FirebaseFirestoreDataSource,
            )
        )
        signupViewModel = ViewModelProvider(this, factory)[SignupViewModel::class]
//        progressBar = CircularProgressIndicatorDialog(requireActivity())

        /*binding.emailSignupEditText.doOnTextChanged { text, start, before, count ->
            binding.emailSignupLayout.error = if(text!!.length> 2){
                "no more!"
            }else{
                null
            }
        }*/
        subscribeToObservables()
        setupListeners()
    }

    override fun onStart() {
        super.onStart()
        (requireActivity() as MainActivity).hideBottomNavBar()
        binding.firstNameSignupLayout.requestFocus()
        Log.d(TAG, "onStart: ${Navigation.findNavController(requireView()).currentDestination}, ${Navigation.findNavController(requireView()).currentBackStackEntry}")
        KeyboardUtils.showKeyboard(requireActivity(), binding.firstNameSignupLayout)
    }


    /*fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        // If triggered by an enter key, this is the event; otherwise, this is null.
        if (event != null) {
            // if shift key is down, then we want to insert the '\n' char in the TextView;
            // otherwise, the default action is to send the message.
            if (!event.isShiftPressed) {
                if (isPreparedForSending()) {
                    confirmSendMessageIfNeeded()
                }
                return true
            }
            return false
        }

        if (isPreparedForSending()) {
            confirmSendMessageIfNeeded()
        }
        return true
    }*/

    private fun subscribeToObservables(){
        lifecycleScope.launch{
            repeatOnLifecycle(Lifecycle.State.CREATED){
                signupViewModel.signUpState.collect{
                    when(it){
                        ApiState.Init ->{
                        enableButtons()
//                        progressBar.dismissProgressBar(this@SignupFragment)
                        }
                        ApiState.Loading -> {
                            disableButtons()
//                            progressBar.startProgressBar()
                        }
                        is ApiState.Success -> {
                            enableButtons()
//                            progressBar.dismissProgressBar(this@SignupFragment)
                            showSnackbar("${it.data.displayName}, ${getString(R.string.account_created_successfully)}")
                        }
                        is ApiState.Failure -> {
                            enableButtons()
//                            progressBar.dismissProgressBar(this@SignupFragment)
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
        binding.signupButton.isEnabled = false
    }

    private fun enableButtons(){
        binding.signupButton.isEnabled = true
    }

    private fun setupListeners(){
        binding.apply {
            signupButton.setOnClickListener{
                validateFields()
            }
            signupText.setOnClickListener{
//                findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
                findNavController().navigateUp()
            }
            confirmPasswordSignupEditText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
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
        clearFocueses()
        binding.apply {
            val firstName: String = firstNameSignupEditText.text.toString()
            val lastName: String = lastNameSignupEditText.text.toString()
            val email: String = emailSignupEditText.text.toString()
            val password: String = passwordSignupEditText.text.toString()
            val confirmPassword: String = confirmPasswordSignupEditText.text.toString()

            val areFieldsValid = email.isValidEmail() && password.isValidPassword() && confirmPassword.matchesPassword(password)
            //TODO: handle rest of signup validation errors
            if(areFieldsValid){
                if(NetworkUtils.isConnected(requireContext())){
                    signupViewModel.signUpUsingEmailAndPassword(email, firstName, lastName, password)
                }else{
                    binding.root.showSnackbar(getString(R.string.no_internet_connection))
                }
            }else{
                if(!email.isValidEmail()){
                    emailSignupLayout.error = getString(R.string.email_invalid)
                }
                if(!password.isValidPassword()){
                    /** Minimum six characters, at least one letter and one number:*/
                    passwordSignupLayout.error = if(password.length < 6){
                        getString(R.string.password_invalid_length_small)
                    }else {
                        getString(R.string.password_invalid_at_least_1_letter_1_number)
                    }
                }
                if(!confirmPassword.matchesPassword(password)){
                    confirmPasswordSignupLayout.error = getString(R.string.confirm_password_invalid)
                }
            }
        }
    }


    private fun clearFocueses(){
        binding.apply {
            passwordSignupLayout.clearFocus()
            lastNameSignupLayout.clearFocus()
            firstNameSignupLayout.clearFocus()
            emailSignupLayout.clearFocus()
            confirmPasswordSignupLayout.clearFocus()
        }
    }

    private fun hideValidationErrors() {
        binding.apply {
            passwordSignupLayout.error = null
            lastNameSignupLayout.error = null
            firstNameSignupLayout.error = null
            emailSignupLayout.error = null
            confirmPasswordSignupLayout.error = null
        }
    }

    companion object {
        private const val TAG = "SignupFragment"
    }
}