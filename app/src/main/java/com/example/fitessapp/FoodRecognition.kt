package com.example.fitessapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class FoodRecognition : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_recognition)

        val btnCheckFood = findViewById<Button>(R.id.checkFood)
        btnCheckFood.setOnClickListener {
            // Navigate to CameraActivity when the button is clicked
            val intent = Intent(this, Camera::class.java)
            startActivity(intent)
        }
    }
}
