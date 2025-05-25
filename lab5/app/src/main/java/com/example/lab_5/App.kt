package com.example.lab_5

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lab_5.ui.FlightSearchScreen
import com.example.lab_5.ui.FlightSearchViewModel

@Composable
fun FlightSearchApp(modifier: Modifier = Modifier) {
    val context = LocalContext.current.applicationContext as Application
    val viewModel: FlightSearchViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return FlightSearchViewModel(context) as T
            }
        }
    )
    FlightSearchScreen(viewModel, modifier = modifier)
} 