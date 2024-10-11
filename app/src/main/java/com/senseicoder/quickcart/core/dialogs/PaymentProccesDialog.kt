package com.senseicoder.quickcart.core.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.databinding.PaymentProccessDialogBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentProccesDialog : DialogFragment() {
    lateinit var binding: PaymentProccessDialogBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dialog?.setCancelable(false)
        binding.apply {
            lifecycleScope.launch {
                delay(500)
                progressBar1.visibility = View.VISIBLE
                textView.setTextColor(R.color.black)
                delay(3000)
                imgStepOne.setImageResource(R.drawable.green_check)
                delay(100)
                progressBar1.visibility = View.GONE
                progressBar2.visibility = View.VISIBLE
                textView2.setTextColor(R.color.black)
                delay(3000)
                imgStepTwo.setImageResource(R.drawable.green_check)
                delay(100)
                progressBar2.visibility =View.GONE
                progressBar3.visibility=View.VISIBLE
                textView3.setTextColor(R.color.black)
                delay(3000)
                imgStepThree.setImageResource(R.drawable.green_check)
                delay(100)
                progressBar3.visibility = View.GONE
                button2.apply {
                    visibility=View.VISIBLE
                    setOnClickListener{
                        dismiss()
                    }
                }
            }
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PaymentProccessDialogBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

}