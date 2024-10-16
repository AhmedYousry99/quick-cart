package com.senseicoder.quickcart.core.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.global.toColor
import com.senseicoder.quickcart.databinding.PaymentProccessDialogBinding
import com.senseicoder.quickcart.features.main.ui.shopping_cart.OnCartItemClickListener
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PaymentProcessesDialog(listener : OnCartItemClickListener) : DialogFragment() {
    lateinit var binding: PaymentProccessDialogBinding
    lateinit var job2:Job
    lateinit var job1:Job
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PaymentProccessDialogBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            val layoutParams = ViewGroup.LayoutParams((resources.displayMetrics.widthPixels * 0.9).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            setLayout(layoutParams.width, layoutParams.height)
            setBackgroundDrawableResource(android.R.color.transparent)
            setCancelable(false)
        }
        stepOne()

    }
    fun stepOne(){
        job1= lifecycleScope.launch{
            binding.apply{
                progressBar1.visibility = View.VISIBLE
                textView.setTextColor("black".toColor())
            }
        }
    }
    fun stepTwo(){
        job2 = lifecycleScope.launch{
            job1.join()
            binding.apply{
                imgStepOne.setImageResource(R.drawable.green_check)
                progressBar1.visibility = View.GONE
                progressBar2.visibility = View.VISIBLE
                textView2.setTextColor("black".toColor())
                stepThree()
            }
        }
    }
    fun stepThree(){
        lifecycleScope.launch{
            binding.apply{
                delay(1500)
                imgStepTwo.setImageResource(R.drawable.green_check)
                delay(100)
                progressBar2.visibility =View.GONE
                progressBar3.visibility=View.VISIBLE
                textView3.setTextColor("black".toColor())
                delay(1000)
                imgStepThree.setImageResource(R.drawable.green_check)
                delay(100)
                progressBar3.visibility = View.GONE
                button2.apply {
                    animationView.visibility = View.GONE
                    visibility=View.VISIBLE
                    setOnClickListener{
                        dismiss()
                    }
                }
                this@PaymentProcessesDialog.dialog?.setCancelable(true)
                delay(500)
                dismiss()
            }
        }
    }
    fun errorOccur(numberOfStep:Int){
        when(numberOfStep){
            1-> {
                binding.apply {
                    progressBar1.visibility = View.GONE
                    textView.setTextColor("red".toColor())
                    imgStepOne.setImageResource(R.drawable.ic_cross_red)
                    animationView.visibility = View.GONE
                }
            }
            2-> {
                binding.apply {
                    progressBar2.visibility = View.GONE
                    textView2.setTextColor("red".toColor())
                    imgStepTwo.setImageResource(R.drawable.ic_cross_red)
                    animationView.visibility = View.GONE
                }
            }
            3-> {
                binding.apply {
                    progressBar3.visibility = View.GONE
                    textView3.setTextColor("red".toColor())
                    imgStepThree.setImageResource(R.drawable.ic_cross_red)
                    animationView.visibility = View.GONE
                }
            }
        }
        binding.button2.apply {
            visibility=View.VISIBLE

            setBackgroundColor("red".toColor())
            text =String.format("Failed")
            setOnClickListener{
                dismiss()
            }
        }
        this@PaymentProcessesDialog.dialog?.setCancelable(true)
    }
}