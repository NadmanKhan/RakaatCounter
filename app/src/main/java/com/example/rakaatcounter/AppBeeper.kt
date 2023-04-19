package com.example.rakaatcounter

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.CountDownTimer

private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 200)

fun beep(times: Int = 1, durationMs: Int = 200) {
    if (times <= 0 || durationMs <= 0) return

    val contDownInterval: Long = durationMs + 150L

    object : CountDownTimer(contDownInterval * times, contDownInterval) {

        override fun onTick(millisUntilFinished: Long) {
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, durationMs)
        }

        override fun onFinish() {

        }
    }.start()
}