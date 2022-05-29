package com.example.fare

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.file.Paths

class secondactivity : AppCompatActivity() {
    // Array of strings that's used to display on screen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secondactivity)
        val minput = InputStreamReader(assets.open("Attendance.csv"))
        val reader = BufferedReader(minput)

        var line : String?
        var displayData: String = " "

        while (reader.readLine().also { line = it } != null){
            val row : List<String> = line!!.split(",")
            displayData = displayData + row[0] + "\t\t" + row[1] + "\n"
        }

        val txtData = findViewById(R.id.data) as TextView
        txtData.text = displayData
    }
}