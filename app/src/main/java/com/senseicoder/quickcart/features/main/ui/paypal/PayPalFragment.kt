package com.senseicoder.quickcart.features.main.ui.paypal

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.paypal.android.sdk.payments.PayPalConfiguration
import com.paypal.android.sdk.payments.PayPalPayment
import com.paypal.android.sdk.payments.PayPalService
import com.paypal.android.sdk.payments.PaymentActivity
import com.paypal.android.sdk.payments.PaymentConfirmation
import com.senseicoder.quickcart.BuildConfig
import com.senseicoder.quickcart.databinding.PayPalPaymentBinding
import org.json.JSONObject
import java.math.BigDecimal


class PayPalFragment : Fragment() {
    lateinit var configuration :PayPalConfiguration
    private lateinit var binding: PayPalPaymentBinding
    private val REQ_CODE_PAYMENT: Int
        get() = 123

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PayPalPaymentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configuration = PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(BuildConfig.pay_pal_client_id).acceptCreditCards(true)
            .rememberUser(true)

        binding.apply {
            button3.setOnClickListener {
                getPayment()
            }
//            editTextText.setText(arguments?.getString("amount",""))
        }
    }
    private fun getPayment(){
        val amount = binding.editTextText.text.toString()
        val payment :PayPalPayment = PayPalPayment(
            BigDecimal(amount),
            "EUR","Shopify Store",PayPalPayment.PAYMENT_INTENT_ORDER)
        val intent = Intent(requireActivity(), PaymentActivity::class.java)
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration)
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment)
        startActivityForResult(intent, REQ_CODE_PAYMENT)



    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == REQ_CODE_PAYMENT){
            val paymentConfirmation : PaymentConfirmation? = data?.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION)
            if(paymentConfirmation != null){
                try {
                    val paymentDetails = paymentConfirmation.toJSONObject().toString()
                    val obj : JSONObject = JSONObject(paymentDetails)
                }catch (e :Exception){
                    Snackbar.make(binding.root, "Error", Snackbar.LENGTH_SHORT).show()
                }
            }else if(resultCode == Activity.RESULT_CANCELED)
                Snackbar.make(binding.root, "Canceled", Snackbar.LENGTH_SHORT).show()
        }else if(requestCode == PaymentActivity.RESULT_EXTRAS_INVALID){
            Snackbar.make(binding.root, "Invalid", Snackbar.LENGTH_SHORT).show()

        }

    }
}