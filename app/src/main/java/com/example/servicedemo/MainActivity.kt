package com.example.servicedemo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import com.example.servicedemo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var isTracking = false
    private var currentTimeInMillis = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            btnStartTimer.setOnClickListener {
                toggleRun()
            }

            btnStopTimer.setOnClickListener {
                stopTimer()
            }

            btnStopTimer.visibility = View.GONE
        }

        subscribeToObservers()
    }

    private fun updateTracking(isTracking: Boolean) {
        this.isTracking = isTracking
        if(!isTracking) {
            binding.btnStartTimer.text = "Resume Timer"
            binding.tvTimerStatus.text = "Paused"
        } else {
            binding.btnStartTimer.text = "Pause Timer"
            binding.tvTimerStatus.text = "Running"
            binding.btnStopTimer.visibility = View.VISIBLE
        }
    }

    private fun stopTimer() {
        sendCommandToService(Commands.STOP_SERVICE)
        isTracking = false
        binding.btnStopTimer.visibility = View.GONE
        binding.btnStartTimer.text = "Start Timer"
        binding.tvTimerStatus.text = "Stopped"
        binding.tvTimer.text = "00:00:00:00"
    }

    private fun toggleRun() {
        if(isTracking) {
            sendCommandToService(Commands.PAUSE_SERVICE)
        } else {
            sendCommandToService(Commands.START_OR_RESUME_SERVICE)
        }
    }

    private fun subscribeToObservers() {
        TimerService.isTracking.observe(this, Observer {
            updateTracking(it)
        })

        TimerService.timeRunInMillis.observe(this, Observer {
            currentTimeInMillis = it
            val formattedTime = TimerUtility.getFormattedStopWatchTime(currentTimeInMillis, true)
            binding.tvTimer.text = formattedTime
        })
    }

    private fun sendCommandToService(action: String) {
        Intent(applicationContext, TimerService::class.java).also {
            it.action = action
            applicationContext.startService(it)
        }
    }
}