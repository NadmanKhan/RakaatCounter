package com.example.rakaatcounter


import android.app.Activity
import android.hardware.Sensor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import android.widget.TextView

class MeasuringActivity : AppCompatActivity() {

    private lateinit var lightSensorListener: AppSensorListener
    private var values: MutableList<Float> = mutableListOf()

    private lateinit var textViewRemSec: TextView
    private lateinit var textViewProgressStatus: TextView

    private val timerStarting = object : CountDownTimer(6000L, 10) {
        override fun onTick(millisUntilFinished: Long) {
            textViewRemSec.text = (millisUntilFinished / 1000).toString()
        }

        override fun onFinish() {
            beep(1)
            textViewRemSec.text = 5.toString()
            textViewProgressStatus.text = "Measuring"
            timerMeasuring.start()
        }
    }

    private val timerMeasuring = object : CountDownTimer(6000L, 1) {
        override fun onTick(millisUntilFinished: Long) {
            textViewRemSec.text = (millisUntilFinished / 1000).toString()
            values.add(lightSensorListener.value())
        }

        override fun onFinish() {
            beep(2)
            textViewProgressStatus.text = "Done"
            terminate(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measuring)

        textViewRemSec = findViewById(R.id.textViewRemSec)
        textViewProgressStatus = findViewById(R.id.textViewProgressStatus)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        lightSensorListener = AppSensorListener(this, Sensor.TYPE_LIGHT)

        startMeasuring()
    }

    override fun onResume() {
        super.onResume()
        lightSensorListener.register()
    }

    override fun onPause() {
        super.onPause()
        lightSensorListener.unregister()
        terminate(false)
    }

    private fun terminate(done: Boolean) {
        if (done) {
            intent.putExtra("values", values.toFloatArray())
        }
        setResult(if (done) Activity.RESULT_OK else Activity.RESULT_CANCELED, intent)
        timerMeasuring.cancel()
        timerStarting.cancel()
        finish()
    }

    private fun startMeasuring() {
        textViewRemSec.text = 5.toString()
        textViewProgressStatus.text = "Starting"
        timerStarting.start()
    }
}
