package com.example.rakaatcounter


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class AppSensorListener(
    context: Context,
    sensorType: Int
) : SensorEventListener {

    private val sensorManager: SensorManager
    private val lightSensor: Sensor
    private var value: Float = 0F

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(sensorType)
    }

    fun value() = value

    fun register() {
        sensorManager.registerListener(this, lightSensor, 1, 1)
    }

    fun unregister() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(sensorEvent: SensorEvent?) {
        if (sensorEvent != null) {
            value = sensorEvent.values[0]
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}