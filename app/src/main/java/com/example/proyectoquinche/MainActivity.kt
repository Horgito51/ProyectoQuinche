package com.example.proyectoquinche

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.proyectoquinche.ui.compose.QuincheApp

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels { MainViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(android.R.style.Theme_Material_Light_NoActionBar)
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent { QuincheApp(viewModel) }
        if (savedInstanceState == null && viewModel.isLoggedIn.value) viewModel.requestSync()
    }
}
