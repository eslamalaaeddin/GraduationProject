package com.example.graduationproject.helper

import android.app.ProgressDialog
import android.content.Context

object Utils {
    fun showProgressDialog(context: Context, message: String): ProgressDialog {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage(message)
        progressDialog.show()
        return progressDialog
    }
}