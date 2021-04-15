package com.example.servicedemo

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object TimerUtility {
    val formatterWithoutMillis = SimpleDateFormat("HH:mm:ss")
    val formatterWithMillis = SimpleDateFormat("HH:mm:ss:SSS")

    fun getFormattedStopWatchTime(ms: Long, includeMillis: Boolean = false) : String {
        var formattedTime: String

        var milliseconds = ms

        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)

        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)

        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)

        if(!includeMillis) {
            formattedTime = "${if(hours < 10) "0" else ""}$hours:" +
                    "${if(minutes < 10) "0" else ""}$minutes:" +
                    "${if(seconds < 10) "0" else ""}$seconds"
        } else {
            milliseconds -= TimeUnit.SECONDS.toMillis(seconds)
            milliseconds /= 10
            formattedTime = "${if (hours < 10) "0" else ""}$hours:" +
                    "${if (minutes < 10) "0" else ""}$minutes:" +
                    "${if (seconds < 10) "0" else ""}$seconds:" +
                    "${if (milliseconds < 10) "0" else ""}$milliseconds"
        }

        return formattedTime
    }

}