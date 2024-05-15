package com.example.fitessapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView

class CardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)

        supportActionBar?.hide()

        val bmi = findViewById<ImageView>(R.id.imgBMI)
        val sensor= findViewById<ImageView>(R.id.imgPedoMeter)
        val food= findViewById<ImageView>(R.id.imgdiet)
        val med= findViewById<ImageView>(R.id.imgMedical)

        bmi.setOnClickListener{
            val i= Intent(this, BMIactivity::class.java)
            startActivity(i)
        }

        sensor.setOnClickListener{
            val i2= Intent(this, PedoSensor::class.java)
            startActivity(i2)
        }

        food.setOnClickListener{
            val i3= Intent(this, FoodRecognition::class.java)
            startActivity(i3)
        }

        med.setOnClickListener{
            val i4= Intent(this, Medicine::class.java)
            startActivity(i4)
        }
    }
}