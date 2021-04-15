package com.example.servicedemo

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class DemoService : Service() {

    val logTag = "DemoService"

    override fun onCreate() {
        Log.d(logTag, "Service created")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(logTag, "Service command execuded")
        val dataString = intent?.getStringExtra("SERVICE_DATA")
        dataString?.let {
            Log.d(logTag, dataString)
        }
        return super.onStartCommand(intent, flags, startId)

        // Run some complex action
        Thread {
            while(true) {}
        }.start()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(logTag, "Something is binded tgo service")
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.d(logTag, "Disconnected from service")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        Log.d(logTag, "Service destroyed")
        super.onDestroy()
    }
}