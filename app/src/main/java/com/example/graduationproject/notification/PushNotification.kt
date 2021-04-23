package com.example.graduationproject.notification

import com.example.graduationproject.models.notification.Notification

data class PushNotification(
    val data: Notification,
    var to: String? = null
)