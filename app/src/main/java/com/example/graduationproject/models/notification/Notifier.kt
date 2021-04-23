package com.example.graduationproject.models.notification
import android.graphics.Bitmap

data class Notifier (
    var id: String? = null,
    var imageBitmap: Bitmap? = null,
    var name: String? = null,
    var imageUrl: String? = null
)
