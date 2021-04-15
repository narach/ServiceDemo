package com.example.servicedemo

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class TimerService : LifecycleService() {

    var isFirstRun = true

    private var isTimerEnabled = false
    private var lapTime = 0L
    private var timeRun = 0L
    private var timeStarted = 0L
    private var lastSecondTimestamp = 0L

    private val timeRunInSeconds = MutableLiveData<Long>()

    companion object {
        val timeRunInMillis = MutableLiveData<Long>()
        val isTracking = MutableLiveData<Boolean>()
    }

    fun postInitialValues() {
        isTracking.postValue(false)
        timeRunInSeconds.postValue(0)
        timeRunInMillis.postValue(0)
    }

    override fun onCreate() {
        super.onCreate()
        postInitialValues()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when(it.action) {
                Commands.START_OR_RESUME_SERVICE -> {
                    if(isFirstRun) {
                        startForegroundService()
                        isFirstRun = false
                    } else {
                        Timber.d("Resuming service...")
                        startTimer()
                    }
                }
                Commands.PAUSE_SERVICE -> {
                    Timber.d("Pausing service")
                    pauseService()
                }
                Commands.STOP_SERVICE -> {
                    Timber.d("Stopping service")
                    stopService()
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun pauseService() {
        isTracking.postValue(false)
        isTimerEnabled = false
    }

    private fun stopService() {
        stopForeground(true)
        postInitialValues()
        isFirstRun = true
    }

    private fun startTimer() {
        timeStarted = System.currentTimeMillis()
        isTracking.postValue(true)
        isTimerEnabled = true

        CoroutineScope(Dispatchers.Main).launch {
            while(isTracking.value!!) {
                // Разница между текущим моментом времени и временем запуска таймера
                lapTime = System.currentTimeMillis() - timeStarted
                timeRunInMillis.postValue(timeRun + lapTime)
                // Обновляем время в секундах только если с последнего апдейта прошла 1 полная секунда
                if (timeRunInMillis.value!! >= lastSecondTimestamp + 1000L) {
                    timeRunInSeconds.postValue(timeRunInSeconds.value!! + 1)
                    lastSecondTimestamp += 1000L
                }

                delay(Constants.TIMER_UPDATE_INTERVAL)
            }

            timeRun += lapTime
        }
    }

    private fun startForegroundService() {
        startTimer()
        isTracking.postValue(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        val notificationBuilder = NotificationCompat.Builder(this, Constants.CHANNEL_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setSmallIcon(R.drawable.ic_timer_foreground)
            .setContentTitle("Timer")
            .setContentText("00:00:00")
            .setContentIntent(getMainActivityPendingIntent())

        startForeground(Constants.NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getMainActivityPendingIntent() = PendingIntent.getActivity(
        this,
        0,
        Intent(this, MainActivity::class.java).also {  },
        FLAG_UPDATE_CURRENT
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(
            Constants.CHANNEL_ID,
            Constants.CHANNEL_NAME,
            IMPORTANCE_LOW
        )
        notificationManager.createNotificationChannel(channel)
    }
}