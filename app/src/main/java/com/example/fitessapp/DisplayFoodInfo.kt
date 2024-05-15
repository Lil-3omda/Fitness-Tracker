package com.example.fitessapp


import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONException

class DisplayFoodInfo : AppCompatActivity() {

    private lateinit var jsonResponseTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_food_info)

        jsonResponseTextView = findViewById(R.id.JsonResponse)

        // Retrieve JSON response from intent extras
        val jsonResponse = intent.getStringExtra("jsonResponse")
        if (jsonResponse != null) {
            displayJsonResponse(jsonResponse)
        } else {
            Log.e(TAG, "JSON response is null")
            jsonResponseTextView.text = "Error: JSON response is null"
        }
    }

    private fun displayJsonResponse(response: String) {
        try {
            // Parse JSON response
            val jsonResponse = JSONArray(response)
            if (jsonResponse.length() > 0) {
                val firstItem = jsonResponse.getJSONObject(0)
                val foodLabel = firstItem.getString("label")
                // Display the food label in the JsonResponse TextView
                jsonResponseTextView.text = foodLabel
            } else {
                Log.e(TAG, "Empty JSON response")
                jsonResponseTextView.text = "Empty response"
            }
        } catch (e: JSONException) {
            Log.e(TAG, "Error parsing JSON response: ${e.message}")
            jsonResponseTextView.text = "Error parsing response"
        }
    }

    companion object {
        private const val TAG = "DisplayFoodInfoActivity"
    }
}
