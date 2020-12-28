package com.example.graduationproject

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*

const val accessTokenExpirationDate = "Sun, 27 Dec 2020 15:42:25 GMT"
const val refreshTokenExpirationDate = "Mon, 11 Jan 2021 15:27:25 GMT"
const val MINUTES_15 = 900
const val DAYS_14 = 1209600

fun main() {
//        val accessTokenTimeStamp =
//            convertServerTimeToTimestamp(accessTokenExpirationDate)
//        val refreshTokenTimeStamp =
//            convertServerTimeToTimestamp(refreshTokenExpirationDate)
//        println(isAccessTokenExpired(accessTokenTimeStamp))
//        println(isRefreshTokenExpired(refreshTokenTimeStamp))

    val str_date = "27-12-2020"
    val formatter: DateFormat = SimpleDateFormat("dd-MM-yyyy")
    val date: Date = formatter.parse(str_date) as Date
    println("Today is " + date.time / 1000)

    }

    private fun convertServerTimeToTimestamp(serverTime: String): Long{
        val dateOnly = serverTime.substring(5, 25)
        val formatter: DateFormat = SimpleDateFormat("dd MMM yyyy HH:mm:ss")
        val date: Date = formatter.parse(dateOnly) as Date
        return date.time / 1000
    }

    private fun isAccessTokenExpired(accessTokenTimestamp: Long): Boolean {
        val currentTimeInSeconds = System.currentTimeMillis()/1000
        return (currentTimeInSeconds - accessTokenTimestamp) >= MINUTES_15
    }

    private fun isRefreshTokenExpired(refreshTokenTimestamp: Long): Boolean {
        val currentTimeInSeconds = System.currentTimeMillis()/1000
        return (currentTimeInSeconds - refreshTokenTimestamp) >= DAYS_14
    }
