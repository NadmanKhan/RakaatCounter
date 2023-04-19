package com.example.rakaatcounter


import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class MakeProfileActivity : AppCompatActivity() {
    private val maxAllowedVariance: Float = 40F

    private var values: MutableList<List<Float>> = mutableListOf()
    private var isResultSent: Boolean = false
    private var status: MakeProfileStatus = MakeProfileStatus.STARTING

    private lateinit var textViewComment: TextView
    private lateinit var buttonMeasure: Button
    private lateinit var buttonSave: Button
    private lateinit var checkBox1: CheckBox
    private lateinit var checkBox2: CheckBox

    private val valuesGetter =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            isResultSent = if (result == null) {
                false
            } else {
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent: Intent? = result.data
                    if (intent == null) {
                        false
                    } else {
                        val resultValues = intent.getFloatArrayExtra("values")
                        if (resultValues == null) {
                            false
                        } else {
                            values.add(resultValues.toList())
                            true
                        }
                    }
                } else {
                    false
                }
            }
            updateStatus()
            updateViews()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_make_profile)

        textViewComment = findViewById(R.id.textViewComment)
        buttonMeasure = findViewById(R.id.buttonMeasure)
        buttonSave = findViewById(R.id.buttonSave)
        checkBox1 = findViewById(R.id.checkBox1)
        checkBox2 = findViewById(R.id.checkBox2)

        buttonSave.text = "Start prayer"

        checkBox1.isChecked = false
        checkBox2.isChecked = false

        val intentMeasuringActivity = Intent(this, MeasuringActivity::class.java)

        buttonMeasure.setOnClickListener {
            valuesGetter.launch(intentMeasuringActivity)
        }

        buttonSave.setOnClickListener {
            val intent: Intent = Intent(this, CounterActivity::class.java)
            intent.putExtra("mean", values[1].average().toFloat())
            intent.putExtra("stddev", values[1].stddev().toFloat())
            startActivity(intent)
        }

        updateViews()
    }

    private fun updateStatus() {
        if (!isResultSent) {
            status = if (checkBox1.isChecked) {
                MakeProfileStatus.ERROR_NOT_COMPLETED_MEASURING_SUJOOD_VALUES
            } else {
                MakeProfileStatus.ERROR_NOT_COMPLETED_MEASURING_NORMAL_VALUES
            }
        } else if (values.size == 1) {
            status = if (values[0].variance() > maxAllowedVariance) {
                MakeProfileStatus.ERROR_TOO_VARIANT_NORMAL_VALUES
            } else {
                MakeProfileStatus.NORMAL_VALUES_OKAY
            }
        } else if (values.size == 2) {
            status = if (values[1].variance() > maxAllowedVariance) {
                MakeProfileStatus.ERROR_TOO_VARIANT_SUJOOD_VALUES
            } else {
                if (abs(values[1].average() - values[0].average()) <= values[1].stddev()) {
                    MakeProfileStatus.ERROR_TOO_CLOSE_VALUES
                } else {
                    MakeProfileStatus.ALL_VALUES_OKAY
                }
            }
        } else {
            status = MakeProfileStatus.ERROR_UNCHECKED_CASE
        }

        when (status) {
            MakeProfileStatus.ERROR_TOO_VARIANT_NORMAL_VALUES,
            MakeProfileStatus.ERROR_TOO_VARIANT_SUJOOD_VALUES,
            MakeProfileStatus.ERROR_TOO_CLOSE_VALUES -> {
                values.removeLast()
            }
            else -> {}
        }
    }

    private fun updateViews() {
        buttonMeasure.isEnabled = status != MakeProfileStatus.ALL_VALUES_OKAY
        buttonSave.isEnabled = !buttonMeasure.isEnabled
        textViewComment.text = when (status) {
            MakeProfileStatus.ERROR_NOT_COMPLETED_MEASURING_NORMAL_VALUES -> "Did not complete measuring for the full 5 seconds."
            MakeProfileStatus.ERROR_TOO_VARIANT_NORMAL_VALUES -> "Too variant."
            MakeProfileStatus.ERROR_TOO_VARIANT_SUJOOD_VALUES -> "Too variant."
            MakeProfileStatus.ERROR_TOO_CLOSE_VALUES -> "Too close values."
            MakeProfileStatus.ERROR_UNCHECKED_CASE -> "Unchecked error case! Please inform the developer."
            else -> ""
        }
        checkBox1.isChecked = checkBox1.isChecked || when (status) {
            MakeProfileStatus.ALL_VALUES_OKAY -> true
            MakeProfileStatus.NORMAL_VALUES_OKAY -> true
            MakeProfileStatus.ERROR_NOT_COMPLETED_MEASURING_SUJOOD_VALUES -> true
            MakeProfileStatus.ERROR_TOO_VARIANT_SUJOOD_VALUES -> true
            MakeProfileStatus.ERROR_TOO_CLOSE_VALUES -> true
            else -> false
        }
        checkBox2.isChecked = status == MakeProfileStatus.ALL_VALUES_OKAY
    }

    private fun List<Float>.variance(): Double =
        this.sumOf { abs(this.average() - it).pow(2) } / this.size

    private fun List<Float>.stddev(): Double = sqrt(this.variance())

}
