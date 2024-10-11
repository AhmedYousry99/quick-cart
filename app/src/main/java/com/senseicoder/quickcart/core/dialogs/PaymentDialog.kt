package com.senseicoder.quickcart.core.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.databinding.NewlayBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentDialog:DialogFragment() {
    lateinit var binding :NewlayBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = NewlayBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
            val wid = (resources.displayMetrics.widthPixels * .9).toInt()
            val hei = (resources.displayMetrics.widthPixels * .9).toInt()
            setLayout(wid,hei)
        }
        CoroutineScope(Dispatchers.Main).launch {
            binding.apply{// Step 1: Processing
                statusMessage.text = "Processing Step 1..."
                step1Indicator.setImageResource(R.drawable.bag) // Update Step 1 as active
                delay(5000)  // Simulate processing delay

                // Step 2: Processing
                statusMessage.text = "Processing Step 2..."
                step1Indicator.setImageResource(R.drawable.ic_cart)  // Step 1 complete
                step2Indicator.setImageResource(R.drawable.appicon)    // Step 2 active
                delay(5000)  // Simulate step 2 processing

                // Verification complete
                step2Indicator.setImageResource(R.drawable.baseline_person_24)  // Step 2 complete
                statusMessage.text = "Verification Complete!"
                dismiss()
            }
        }
    }
}