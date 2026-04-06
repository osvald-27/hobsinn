package com.hobsinnovations.hobsinn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hobsinnovations.hobsinn.ui.scheduling.SpecialCallScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Simple routing representation
                    var currentScreen by remember { mutableStateOf("home") }

                    Column {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(onClick = { currentScreen = "home" }) { Text("Home User") }
                            Button(onClick = { currentScreen = "pickup" }) { Text("Pickup User") }
                            Button(onClick = { currentScreen = "leaderboard" }) { Text("Battles") }
                        }
                        
                        when (currentScreen) {
                            "home" -> SpecialCallScreen()
                            "pickup" -> PickupUserSetupScreen()
                            "leaderboard" -> com.hobsinnovations.hobsinn.ui.gamification.LeaderboardScreen()
                        }
                    }
                }
            }
        }
    }
}
