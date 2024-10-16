package com.senseicoder.quickcart.core.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.global.enums.DialogType
import com.senseicoder.quickcart.databinding.FragmentConfirmationDialogBinding
import com.senseicoder.quickcart.databinding.FragmentConfirmationUpdateDialogBinding


class ConfirmationUpdateDialogFragment(val code: DialogType, val function: (()->Unit)? = null) :
    DialogFragment() {
    private lateinit var binding: FragmentConfirmationUpdateDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfirmationUpdateDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.apply {
            val layoutParams = ViewGroup.LayoutParams((resources.displayMetrics.widthPixels * 0.9).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            setLayout(layoutParams.width, layoutParams.height)
            setBackgroundDrawableResource(android.R.color.transparent)
        }

        binding.apply {
            when (code) {


                DialogType.SAVE_ADDRESS -> {
                    setDialogDetails(
                        getString(R.string.save_address),
                        R.drawable.ic_edit_address,
                        getString(R.string.save_address_message)
                    )
                }
                DialogType.SIGN_UP_FIRST_NAME -> {
                    setDialogDetails(
                        getString(R.string.notice),
                        R.drawable.ic_info_outlined,
                        getString(R.string.signup_first_name_will_be_used)
                    )
                }
                DialogType.SIGN_UP_LAST_NAME -> {
                    setDialogDetails(
                        getString(R.string.notice),
                        R.drawable.ic_info_outlined,
                        getString(R.string.signup_last_name_will_be_used))
                }
                DialogType.SIGN_UP_EMAIL -> {
                    setDialogDetails(
                        getString(R.string.notice),
                        R.drawable.ic_info_outlined,
                        getString(R.string.signup_email_will_be_used))
                }

                DialogType.PERMISSION_DENIED_GUEST_MODE -> {
                    setDialogDetails(
                        getString(R.string.permission_denied_title),
                        R.drawable.ic_info_outlined,
                        getString(R.string.permission_denied_body))
                    btnOk.text = getString(R.string.login_button)
                }
                else ->{}
            }
            btnCancel.setOnClickListener{
                dismiss()
            }
            btnOk.setOnClickListener{
                function?.invoke()
                dismiss()
            }
        }
    }

    private fun setDialogDetails(title: String, icon: Int, message: String) {
        binding.apply {
            txtTitleDialog.text = title
            txtMessageForDialog.text = message
            imgIcLogOut.setImageResource(icon)
        }
    }

}
