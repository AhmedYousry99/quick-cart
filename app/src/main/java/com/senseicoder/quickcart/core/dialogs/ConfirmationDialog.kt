package com.senseicoder.quickcart.core.dialogs

import android.app.Activity
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.senseicoder.quickcart.R

class ConfirmationDialog(
    private val activity: Activity,
    val onCancel: (() -> Unit)?,
    val onConfirm: (() -> Unit)?,
) {
    private var alertDialog: AlertDialog? = null
    private lateinit var confirmButton: Button
    private lateinit var cancelButton: Button
    private lateinit var contentTextView: TextView
    private var view: View

    var message: String? = null
    var onConfirmText: String? = null
    var onCancelText: String? = null

    init {
        val builder = AlertDialog.Builder(activity)

        val layoutInflater = activity.layoutInflater
        view = layoutInflater.inflate(R.layout.dialog_confirmation, null)
        builder.setView(view)
        alertDialog = builder.create()
        alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog!!.window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    fun showDialog() {
        alertDialog!!.show()
        confirmButton = view.findViewById<Button>(R.id.dialogConfirmButton)
        cancelButton = view.findViewById<Button>(R.id.dialogCancelButton)
        contentTextView = view.findViewById<TextView>(R.id.dialogContentTextView)

        contentTextView.text = message
        confirmButton.text = if (onConfirmText != null) onConfirmText else activity.getString(R.string.dialog_on_confirm_default)
        cancelButton.text = if (onCancelText != null) onCancelText else activity.getString(R.string.dialog_on_cancel_default)

        confirmButton.setOnClickListener{
            onConfirm?.invoke()
            dismissDialog()
        }

        cancelButton.setOnClickListener{
            onCancel?.invoke()
            dismissDialog()
        }

        alertDialog!!.setOnDismissListener { dialog: DialogInterface? ->
            onCancel?.invoke()
        }
    }

    fun dismissDialog() {
        alertDialog?.dismiss()
    }
}