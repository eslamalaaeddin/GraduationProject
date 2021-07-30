package com.example.graduationproject.models
//A wrapper class around any HTTP response to help me get its message in addition to its response code.
data class ResponseMessage(var message: String? = null, var responseCode: Int? = null)