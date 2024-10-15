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


class ConfirmationDialogFragment(val code: DialogType, val function: (()->Unit)? = null) :
    DialogFragment() {
    private lateinit var binding: FragmentConfirmationDialogBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfirmationDialogBinding.inflate(inflater, container, false)
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
                DialogType.DEL_ADDRESS -> {
                    setDialogDetails(
                        getString(R.string.delete_address),
                        R.drawable.ic_delete,
                       getString( R.string.delete_address_message),
                        getString(R.string.delete)
                    )
                }

                DialogType.DEL_PRODUCT -> {
                    setDialogDetails(
                        getString(R.string.delete_product),
                        R.drawable.ic_delete,
                        getString(R.string.delete_product_message),
                        getString(R.string.delete)
                    )
                }

                DialogType.LOGOUT -> {
                    setDialogDetails(
                        getString(R.string.sign_out),
                        R.drawable.ic_logout,
                        getString(R.string.sign_out_message),
                        getString(R.string.sign_out)
                    )
                }

                DialogType.SAVE_ADDRESS -> {
                    setDialogDetails(
                        getString(R.string.save_address),
                        R.drawable.ic_edit_address,
                        getString(R.string.save_address_message),
                        getString(R.string.add)
                    )
                }
                DialogType.DEL_FAV -> {
                    setDialogDetails(
                        getString(R.string.remove_from_favorite),
                        R.drawable.ic_delete,
                        getString(R.string.remove_from_favorite_confirmation),
                        getString(R.string.delete))

                }
                DialogType.GUEST_MODE ->{
                    setDialogDetails(
                        getString(R.string.guest_mode),
                        R.drawable.baseline_person_24,
                        getString(R.string.you_wont_have_access_to_features),
                        getString(R.string.continue_as_guest))
                }

                DialogType.SIGN_UP_FIRST_NAME -> {
                    setDialogDetails(
                        getString(R.string.notice),
                        R.drawable.ic_info_outlined,
                        getString(R.string.signup_first_name_will_be_used),

                        getString(R.string.dialog_on_confirm_default)
                    )
                }
                DialogType.SIGN_UP_LAST_NAME -> {
                    setDialogDetails(
                        getString(R.string.notice),
                        R.drawable.ic_info_outlined,
                        getString(R.string.signup_last_name_will_be_used),
                        getString(R.string.dialog_on_confirm_default))
                }
                DialogType.SIGN_UP_EMAIL -> {
                    setDialogDetails(
                        getString(R.string.notice),
                        R.drawable.ic_info_outlined,
                        getString(R.string.signup_email_will_be_used),
                        getString(R.string.dialog_on_confirm_default))
                }

                DialogType.PERMISSION_DENIED_GUEST_MODE -> {
                    setDialogDetails(
                        getString(R.string.permission_denied_title),
                        R.drawable.ic_info_outlined,
                        getString(R.string.permission_denied_body),
                        getString(R.string.login_button))
                }
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

    private fun setDialogDetails(title: String, icon: Int, message: String, btnOkText: String) {
        binding.apply {
            txtTitleDialog.text = title
            txtMessageForDialog.text = message
            btnOk.text = btnOkText
            imgIcLogOut.setImageResource(icon)
        }
    }

}