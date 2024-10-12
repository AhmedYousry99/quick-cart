package com.senseicoder.quickcart.core.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup.LayoutParams
import androidx.fragment.app.DialogFragment
import com.senseicoder.quickcart.R
import com.senseicoder.quickcart.core.global.enums.DialogType
import com.senseicoder.quickcart.databinding.FragmentConfirmationDialogBinding


class ConfirmationDialogFragment(val code: DialogType, val function: ()->Unit) :
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
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

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
                        "Delete Product",
                        R.drawable.ic_delete,
                        "Are you sure you will deleted this Product",
                        "Delete"
                    )
                }

                DialogType.LOGOUT -> {
                    setDialogDetails(
                        "Sign Out",
                        R.drawable.ic_logout,
                        "Are you sure you will Sign out",
                        "Sign Out"
                    )
                }

                DialogType.SAVE_ADDRESS -> {
                    setDialogDetails(
                        "Save Address",
                        R.drawable.ic_edit_address,
                        "Are you sure you will add this address",
                        "ADD"
                    )
                }
                DialogType.DEL_FAV -> {
                    setDialogDetails(
                        getString(R.string.remove_from_favorite),
                        R.drawable.ic_delete,
                        getString(R.string.remove_from_favorite_confirmation),
                        getString(R.string.dialog_on_confirm_default))

                }
                DialogType.GUEST_MODE ->{
                    setDialogDetails(
                        getString(R.string.guest_mode),
                        R.drawable.baseline_person_24,
                        getString(R.string.you_wont_have_access_to_features),
                        getString(R.string.continue_as_guest))
                    binding.apply {
                        btnOk.setBackgroundResource(R.drawable.button_selector_default)
                        btnCancel.setBackgroundResource(R.drawable.button_selector_logout)
                    }
                }

            }
            btnCancel.setOnClickListener{
                dismiss()
            }
            btnOk.setOnClickListener{
                function()
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