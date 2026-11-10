package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.HealthTechApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.HealthTechViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val healthTechViewModel: HealthTechViewModel = viewModel()
        HealthTechApp(viewModel = healthTechViewModel)
      }
    }
  }
}

