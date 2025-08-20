package com.example.xogame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.xogame.data.db.AppDatabase
import com.example.xogame.data.db.GameRepository
import com.example.xogame.data.repo.LocalRepository
import com.example.xogame.ui.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getInstance(applicationContext)
        val repo = GameRepository(db)

        setContent {
            MaterialTheme {
                Surface {
                    CompositionLocalProvider(LocalRepository provides repo) {
                        val nav = rememberNavController()
                        NavHost(navController = nav, startDestination = "home") {
                            composable("home") {
                                HomeScreen(
                                    onStartGame = { size -> nav.navigate("game/$size") },
                                    onOpenReplay = { sessionId -> nav.navigate("replay/$sessionId") }
                                )
                            }
                            

                        }

                    }
                }
            }
        }
    }
}