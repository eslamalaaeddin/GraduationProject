package com.example.graduationproject.dummy

import android.content.Intent

interface PostAttachmentListener {
    fun onAttachmentAdded(data: Intent?, dataType: String, fromCamera:Boolean)
}