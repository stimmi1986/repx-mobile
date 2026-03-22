package com.hi.repx_mobile

import android.os.Bundle
import androidx.activity.*
import androidx.compose.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.hi.repx_mobile.ui.navigation.NavGraph
import com.hi.repx_mobile.ui.theme.RepXTheme
import com.hi.repx_mobile.viewmodel.RepXViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RepXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: RepXViewModel = viewModel()

                    NavGraph(
                        navController = navController,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}