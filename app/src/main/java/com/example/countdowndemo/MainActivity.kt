package com.example.countdowndemo

import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var startBackgroundTaskButton: Button
    private lateinit var handler: Handler
    private lateinit var handlerThread: HandlerThread
    private lateinit var countdownText: TextView
    private lateinit var startButton: Button
    private var count = 5

    private val countdownRunnable = object : Runnable {
        override fun run() {
            if (count > 0) {
                //startButton.isEnabled = false
                countdownText.text = "$count"
                count--
                handler.postDelayed(this, 1000)
            } else {
                countdownText.text = "Start！🎉"
                startButton.isEnabled = true
                // 任务结束后不再post
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        handler = Handler(Looper.getMainLooper())

        startButton = findViewById(R.id.startButton)
        countdownText = findViewById(R.id.countdownText)
        startBackgroundTaskButton = findViewById(R.id.startBackgroundTaskButton)

        startButton.setOnClickListener {
            startButton.isEnabled = false //it's better to disable the button here, immediately after click
            count = 5
            handler.post(countdownRunnable)
        }

        startBackgroundTaskButton.setOnClickListener {
            startBackgroundLogging()
        }
    }

    private fun startBackgroundLogging() {
        handlerThread = HandlerThread("BackgroundThread")
        handlerThread.start()

        val backgroundHandler = Handler(handlerThread.looper)
        backgroundHandler.post(object: Runnable {

            override fun run() {
                // 模拟后台任务
                for (i in 1..5) {
                    Thread.sleep(1000) // 模拟耗时操作
                    println("Background task running in ${Thread.currentThread().name}: $i")
                }
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        // 防止内存泄漏
        handler.removeCallbacks(countdownRunnable)
        handlerThread.quitSafely()
    }
}