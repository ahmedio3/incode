package com.incode.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.incode.app.ui.MainScreen
import com.incode.app.ui.navigation.NavGraph
import com.incode.app.ui.theme.IncodeBackground
import com.incode.app.ui.theme.IncodeTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            IncodeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = IncodeBackground
                ) {
                    val navController = rememberNavController()

                    MainScreen(navController = navController) {
                        NavGraph(navController = navController)
                    }
                }
            }
        }
    }
}
