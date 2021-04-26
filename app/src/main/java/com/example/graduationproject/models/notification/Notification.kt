package com.example.graduationproject.models.notification
import android.graphics.Bitmap
import java.util.*

data class Notification(
    var notificationType: String? = null,
    var notifierId: String? = null,
    var notifiedId: String? = null,//it is used to delete notificatoin
    var imageBitmap: Bitmap? = null,
    var notifierName: String? = null,
    var notifierImageUrl: String? = null,
    var movieId: Long? = 0,
    var movieName: String? = null
)
