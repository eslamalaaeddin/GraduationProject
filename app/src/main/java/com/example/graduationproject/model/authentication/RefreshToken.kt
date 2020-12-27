package com.example.graduationproject.model.authentication

data class RefreshToken (var oldToken: String? = null, var refreshToken: String? = null)