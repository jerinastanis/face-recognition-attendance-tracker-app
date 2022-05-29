package com.example.fare

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val button:Button=findViewById(R.id.button)
        val myButton:Button=findViewById(R.id.button2)

        button.setOnClickListener(){
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            resultLauncher.launch( cameraIntent)
        }
        myButton.setOnClickListener(){
            val intent = Intent(this, secondactivity::class.java)
            startActivity(intent)

        }

        }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SimpleDateFormat")
    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val data= result.data
        val uri = intent.data
        val python = Python.getInstance()
        val pythonFile = python.getModule("Attendance" )
        if (result.resultCode == Activity.RESULT_OK) {

            if (result.resultCode == Activity.RESULT_OK) {
                val are= intent.data
                val baos = ByteArrayOutputStream()
                val fis: FileInputStream
                try {
                    fis = FileInputStream(are!!.path?.let { File(it) })
                    val buf = ByteArray(1024)
                    var n: Int
                    while (-1 != fis.read(buf).also { n = it }) baos.write(buf, 0, n)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                val bbytes = baos.toByteArray()
                val bos = ByteArrayOutputStream();
                val out =  ObjectOutputStream(bos);
                out.writeObject(uri)
                out.flush();
                val yourBytes = bos.toByteArray();
                val mediaStorageDir = File("Mymera")
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d("Mymera", "failed to create directory")
                    }
                }
                val timeStamp: String = SimpleDateFormat("s").format(Date())
                val mediaFile = File(mediaStorageDir.path + File.separator +
                                "i_" + timeStamp + ".jpg")
                val array1 = PyObject.fromJava(yourBytes)
                pythonFile.callAttr("main",array1)
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Video recording cancelled.",
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Failed to record video",
                    Toast.LENGTH_LONG).show()
            }
        }
    }}




