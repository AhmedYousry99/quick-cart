package com.senseicoder.quickcart.core.dialogs

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.WindowManager
import androidx.appcompat.app.ActionBar.LayoutParams
import androidx.appcompat.app.AlertDialog
import com.senseicoder.quickcart.R

class CircularProgressIndicatorDialog(private val activity: Activity) {
    private var alertDialog: AlertDialog? = null

    fun startProgressBar() {
        val builder = AlertDialog.Builder(
            activity
        )

        val layoutInflater = activity.layoutInflater
        builder.setView(layoutInflater.inflate(R.layout.circular_progress_dialog, null, true))
        builder.setCancelable(false)
        alertDialog = builder.create()
/*        val width = (activity.resources.displayMetrics.widthPixels * 0.5).toInt()
        val layoutParams = WindowManager.LayoutParams(
            LayoutParams.MATCH_PARENT
        )*/
        alertDialog!!.window!!.setLayout(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
        )
        alertDialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog!!.show()
    }

    fun dismissProgressBar() {
        if (alertDialog != null) alertDialog!!.dismiss()
        alertDialog = null
    }

}