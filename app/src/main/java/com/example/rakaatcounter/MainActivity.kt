package com.example.rakaatcounter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
//    private var profiles: MutableList<Profile> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        populateProfiles()

        findViewById<Button>(R.id.buttonStart).setOnClickListener {
            startActivity(Intent(this, MakeProfileActivity::class.java))
        }
    }

    override fun onStop() {
        super.onStop()
//        saveProfiles()
    }

//    private fun populateProfiles() {
//        val listOfStringToProfile: (List<String>) -> Profile = { list ->
//            Profile(list[0], list[1].toFloat(), list[2].toFloat())
//        }
//        profiles = openFileInput("profiles").bufferedReader().useLines { lines ->
//            lines.map { line ->
//                listOfStringToProfile(line.split(" "))
//            }.toMutableList()
//        }
//    }
//
//    private fun saveProfiles() {
//        openFileOutput("profiles", Context.MODE_PRIVATE).use { out ->
//            val lines = profiles.map { profile ->
//                "${profile.name} ${profile.mean} ${profile.stddev}\n"
//            }
//            out.write(
//                lines.fold("") { prev, curr ->
//                    prev + curr
//                }.toByteArray()
//            )
//        }
//    }
}
