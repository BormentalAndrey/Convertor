package com.example.russianpath

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.russianpath.data.repository.DatabaseSeeder
import com.example.russianpath.presentation.navigation.NavGraph
import com.example.russianpath.presentation.theme.RussianPathTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Заполняем базу начальными данными
        lifecycleScope.launch {
            databaseSeeder.seed()
        }

        setContent {
            RussianPathTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    NavGraph()
                }
            }
        }
    }
}
