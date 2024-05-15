package com.example.fitessapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.text.DecimalFormat

class BMIactivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmiactivity)

        val height= findViewById<EditText>(R.id.eTHeight)
        val weight= findViewById<EditText>(R.id.eTWeight)
        val btnCalc= findViewById<Button>(R.id.btnBMI)
        val resultText= findViewById<TextView>(R.id.tVResult)

        btnCalc.setOnClickListener{
            try {
                val h = height.text.toString().toFloat() / 100
                val w = weight.text.toString().toFloat()

                if (h <= 0 || w <= 0) {
                    throw IllegalArgumentException("Height and weight must be positive numbers.")
                }

                val bmi = w / (h * h)
                val formattedBMI = DecimalFormat("#.##").format(bmi)
                resultText.text = "Your BMI is $formattedBMI"

                if (bmi < 18.5) {
                    resultText.append("\n\nYou are very underweight and possibly malnourished.")
                }else if(bmi > 18.5 && bmi < 24.9 ){
                    resultText.append("\n\nYou have a healthy weight range for young and middle-aged adults.")
                }
                else if(bmi > 25 && bmi < 29.9 ){
                    resultText.append("\n\nyou are overweight.")
                }else {
                    resultText.append("\n\nyou are obese.")
                }
            } catch (e: NumberFormatException) {
                resultText.text = "Invalid input. Please enter valid numbers for height and weight."
            } catch (e: IllegalArgumentException) {
                resultText.text = e.message
            }
        }
    }
}