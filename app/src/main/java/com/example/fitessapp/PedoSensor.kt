package com.example.fitessapp

import android.content.Context
import android.Manifest
import android.hardware.Sensor
import com.tbruyelle.rxpermissions2.RxPermissions
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import android.util.Log


class PedoSensor : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedo_sensor)

        // Request ACTIVITY_RECOGNITION permission
        requestActivityRecognitionPermission()

        // Initialize sensor manager and get step counter sensor
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // Check if step sensor is available
        if (stepSensor == null) {
            Toast.makeText(this, "Step counter sensor not available", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Register sensor listener
        stepSensor?.let { sensor ->
            sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onPause() {
        super.onPause()
        // Unregister sensor listener
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER && it.values.isNotEmpty()) {
                val stepsTaken = it.values[0].toInt()
                findViewById<TextView>(R.id.tv_stepsTaken).text = stepsTaken.toString()
                Log.d("PedoSensor", "Steps taken: $stepsTaken")
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Handle changes in sensor accuracy if necessary
    }

    private fun requestActivityRecognitionPermission() {
        RxPermissions(this)
            .request(Manifest.permission.ACTIVITY_RECOGNITION)
            .subscribe { isGranted ->
                Log.d("PedoSensor", "Is ACTIVITY_RECOGNITION permission granted: $isGranted")
            }
    }
}