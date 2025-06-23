package com.example.recdeckapp.utils

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.recdeckapp.R

object AlertDialogUtils {

    fun showCancelDialog(
        context: Context,
        message: String = "Are you sure you want to cancel?",
        title: String? = "Alert!",
        onYesClicked: (() -> Unit)? = null,
        onNoClicked: (() -> Unit)? = null, // Optional No handler
        onCrossClicked: (() -> Unit)? = null,
        onContinueClicked: (() -> Unit)? = null,
        showContinue: Boolean = false,
        customIconResId: Int? = null
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.alert_dialog_custom)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.setCancelable(false)

        // Add these lines
        dialog.window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.92).toInt(),
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        val btnNo = dialog.findViewById<Button>(R.id.btnNo)
        val btnYes = dialog.findViewById<Button>(R.id.btnYes)
        val ivCross = dialog.findViewById<ImageView>(R.id.ivCross)
        val btnAlertContinue = dialog.findViewById<Button>(R.id.btnAlertContinue)
        val tvMessage = dialog.findViewById<TextView>(R.id.tvAlerDialogMessage)
        val tvTitle = dialog.findViewById<TextView>(R.id.tvAlerDialogTitle)
        val ivAlert = dialog.findViewById<ImageView>(R.id.ivAlert)

        tvMessage.text = message
        tvTitle.text = title


        // 👇 Handle image icon
        customIconResId?.let {
            ivAlert.setImageResource(it)
        }

        // 👇 Show/hide buttons dynamically
        if (showContinue) {
            btnAlertContinue.visibility = View.VISIBLE
            btnYes.visibility = View.GONE
            btnNo.visibility = View.GONE
        } else {
            btnAlertContinue.visibility = View.GONE
            btnYes.visibility = View.VISIBLE
            btnNo.visibility = View.VISIBLE
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
            onNoClicked?.invoke() // If provided, run NO block
        }

        btnYes.setOnClickListener {
            dialog.dismiss()
            onYesClicked?.invoke()
        }
        ivCross.setOnClickListener {
            dialog.dismiss()
            onCrossClicked?.invoke()
        }
        btnAlertContinue.setOnClickListener {
            dialog.dismiss()
            onContinueClicked?.invoke()
        }
        dialog.show()
    }

}