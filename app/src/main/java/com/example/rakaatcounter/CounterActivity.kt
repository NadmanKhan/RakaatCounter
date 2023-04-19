package com.example.rakaatcounter


import android.hardware.Sensor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import java.lang.Float.max

class CounterActivity : AppCompatActivity() {
    private val tolerance: Float = 1F
    private var mean: Float = 0F
    private var stddev: Float = 0F

    private val timeoutMillis: Long = 1000L * 60 * 10
    private val intervalMillis: Long = 10L

    private val countValuesToConsider: Int = 10
    private var prostrationCount: Int = 0
    private var isProstrating: Boolean = false

    private var values: MutableList<Float> = mutableListOf()
    private var meanValues: MutableList<Float> = mutableListOf()
    private var isProstratingValues: MutableList<Boolean> = mutableListOf()
    private var prostrationCounts: MutableList<Int> = mutableListOf()
    private var rakaatCounts: MutableList<Int> = mutableListOf()

    private var sumValues: Float = 0F

    private lateinit var textViewMeanTar: TextView
    private lateinit var textViewMeanCur: TextView
    private lateinit var textViewStddev: TextView
    private lateinit var textViewValue: TextView
    private lateinit var textViewFlag: TextView
    private lateinit var textViewCount: TextView

    private lateinit var lightSensorListener: AppSensorListener

    private val timerCounter = object : CountDownTimer(timeoutMillis, intervalMillis) {

        override fun onTick(millisUntilFinished: Long) {
            values.add(lightSensorListener.value())
            sumValues += values.last()
            findViewById<TextView>(R.id.textViewValue).text = values.last().toString()
            if (values.size <= countValuesToConsider) {
                meanValues.add(0F)
                isProstratingValues.add(false)
                prostrationCounts.add(0)
                rakaatCounts.add(0)
                return
            }
            sumValues -= values[values.size - countValuesToConsider - 1]
            detect()
        }

        override fun onFinish() {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_counter)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        textViewMeanTar = findViewById(R.id.textViewMeanTar)
        textViewMeanCur = findViewById(R.id.textViewMeanCur)
        textViewStddev = findViewById(R.id.textViewStddev)
        textViewValue = findViewById(R.id.textViewValue)
        textViewFlag = findViewById(R.id.textViewFlag)
        textViewCount = findViewById(R.id.textViewCount)

        mean = intent.getFloatExtra("mean", 10F)
        stddev = max(intent.getFloatExtra("stddev", 2F), 2F)

        textViewMeanTar.text = "mean: " + mean.toString()
        textViewStddev.text = "stddev: " + stddev.toString()

        lightSensorListener = AppSensorListener(this, Sensor.TYPE_LIGHT)

        SystemClock.sleep(300L)
    }

    override fun onResume() {
        super.onResume()
        lightSensorListener.register()
        timerCounter.start()
    }

    override fun onPause() {
        super.onPause()
        lightSensorListener.unregister()
        timerCounter.cancel()

        logValues()

        finish()
    }

    private fun logValues() {
        var s = values.fold("values\n") { acc, el ->
            acc + el.toString() + "\n"
        }
        s += meanValues.fold("mean values\n") { acc, el ->
            acc + el.toString() + "\n"
        }
        s += isProstratingValues.fold("is prostrating values\n") { acc, el ->
            acc + (if (el) "100" else "0") + "\n"
        }
        s += prostrationCounts.fold("prostration counts\n") { acc, el ->
            acc + el.toString() + "\n"
        }
        s += rakaatCounts.fold("rakat counts\n") { acc, el ->
            acc + el.toString() + "\n"
        }

        Log.d("log\n", s)
    }

    private fun isProstrating(currentMean: Float): Boolean = currentMean - mean <= tolerance * stddev

    private fun detect() {
        val currentMean: Float = sumValues / countValuesToConsider

        textViewMeanCur.text = "current mean: " + currentMean.toString()

        isProstrating =
            if (isProstrating(currentMean)) {
                if (!isProstrating) increaseProstrationCount()
                true
            } else {
                false
            }

        textViewFlag.text = "prostrating?" + if (isProstrating) "yes" else "no"

        meanValues.add(currentMean)
        isProstratingValues.add(isProstrating)
        prostrationCounts.add(prostrationCount)
        rakaatCounts.add(prostrationCount / 2)
    }

    private fun increaseProstrationCount() {
        ++prostrationCount
        if (prostrationCount % 2 == 0) {
            textViewCount.text = "Rakats: " + (prostrationCount / 2).toString()
            beep(prostrationCount / 2)
        }
    }
}