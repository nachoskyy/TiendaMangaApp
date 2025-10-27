package com.example.tiendamangaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.tiendamangaapp.core.ServiceLocator
import com.example.tiendamangaapp.ui.theme.TiendaMangaAppTheme
import com.example.tiendamangaapp.ui.theme.navigation.AppNavHost
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.tiendamangaapp.core.Bootstrap

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ServiceLocator.init(this)

        lifecycleScope.launch(Dispatchers.IO) {
            Bootstrap.ensureSeeded(ServiceLocator.db)
        }

        setContent {
            TiendaMangaAppTheme {
                val nav = rememberNavController()
                AppNavHost(navController = nav)
            }
        }
    }
}