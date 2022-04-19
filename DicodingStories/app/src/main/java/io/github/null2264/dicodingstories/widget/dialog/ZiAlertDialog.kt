package io.github.null2264.dicodingstories.widget.dialog

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import io.github.null2264.dicodingstories.R
import io.github.null2264.dicodingstories.lib.Common
import io.github.null2264.dicodingstories.widget.button.RoundedButton

// https://github.com/TutorialsAndroid/KAlertDialog
class ZiAlertDialog(context: Context) : AlertDialog(context), View.OnClickListener {

    fun interface ZiOnClickListener {
        fun onClick(alertDialog: ZiAlertDialog)
    }

    var isCancelEnabled: Boolean = false
    var isConfirmEnabled: Boolean = true

    var confirmText: String = ""
    var cancelText: String = ""

    private var onConfirmListener: ZiOnClickListener? = null
    private var onCancelListener: ZiOnClickListener? = null

    var title: String? = null
    var content: String? = null

    var type: Int = SUCCESS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alert_dialog)

        val confirmButton: RoundedButton? = findViewById(R.id.alert_confirm)
        if (isConfirmEnabled) {
            confirmButton?.visibility = View.VISIBLE
            confirmButton?.setOnClickListener(this)
            if (confirmText.isNotEmpty())
                confirmButton?.text = confirmText
        } else
            confirmButton?.visibility = View.GONE

        val cancelButton: RoundedButton? = findViewById(R.id.alert_cancel)
        if (isCancelEnabled) {
            cancelButton?.visibility = View.VISIBLE
            cancelButton?.setOnClickListener(this)
            if (cancelText.isNotEmpty())
                cancelButton?.text = cancelText
        } else {
            cancelButton?.visibility = View.GONE
        }

        val bothDisabled = !isCancelEnabled && !isConfirmEnabled
        setCancelable(bothDisabled)
        setCanceledOnTouchOutside(bothDisabled)

        val titleTextView: TextView? = findViewById(R.id.alert_title)
        titleTextView?.text = title ?: ""
        val contentTextView: TextView? = findViewById(R.id.alert_content)
        contentTextView?.text = content ?: ""

        val imgView: ImageView? = findViewById(R.id.alert_status)
        imgView?.setImageDrawable(
            when (type) {
                SUCCESS -> {
                    imgView.contentDescription = context.getString(R.string.alert_status_success)
                    Common.getDrawableWithTint(context, R.drawable.ic_check_circle_fill, R.color.green_200)
                }
                ERROR -> {
                    imgView.contentDescription = context.getString(R.string.alert_status_error)
                    Common.getDrawableWithTint(context,
                        R.drawable.ic_warning_circle_fill,
                        android.R.color.holo_red_light)
                }
                else -> {// for now else = WARNING
                    imgView.contentDescription = context.getString(R.string.alert_status_warning)
                    Common.getDrawableWithTint(context, R.drawable.ic_warning_fill, android.R.color.holo_orange_light)
                }
            }
        )
        this.window?.setBackgroundDrawable(
            Common.getDrawableWithAttrTint(context, R.drawable.bg_clickable_focusable, R.attr.backgroundColor)
        )
    }

    fun setOnConfirmListener(listener: ZiOnClickListener?): ZiAlertDialog {
        onConfirmListener = listener
        return this
    }

    fun setOnCancelListener(listener: ZiOnClickListener?): ZiAlertDialog {
        onCancelListener = listener
        return this
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.alert_confirm -> {
                if (onConfirmListener != null)
                    onConfirmListener!!.onClick(this)
                this.dismiss()
            }
            R.id.alert_cancel -> {
                if (onCancelListener != null)
                    onCancelListener!!.onClick(this)
                this.cancel()
            }
        }
    }

    companion object {
        const val SUCCESS = 0
        const val ERROR = 1
        const val WARNING = 2
    }
}