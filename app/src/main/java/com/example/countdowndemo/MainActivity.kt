package com.example.countdowndemo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private lateinit var countdownText: TextView
    private lateinit var startButton: Button
    private var count = 5

    private val countdownRunnable = object : Runnable {
        override fun run() {
            if (count > 0) {
                startButton.isEnabled = false
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

        startButton.setOnClickListener {
            count = 5
            handler.post(countdownRunnable)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 防止内存泄漏
        handler.removeCallbacks(countdownRunnable)
    }
}