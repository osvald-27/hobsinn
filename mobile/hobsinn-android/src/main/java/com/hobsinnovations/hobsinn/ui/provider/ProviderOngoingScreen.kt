package com.hobsinnovations.hobsinn.ui.provider

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProviderOngoingScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Ongoing Jobs", style = MaterialTheme.typography.titleLarge)
        
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Special Call", style = MaterialTheme.typography.titleMedium)
                        Text("Location: Lat: 4.15, Lng: 9.24")
                        Text("Time: 2026-04-10T14:00")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(onClick = { /* Start Job */ }) { Text("Start") }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(onClick = { /* Complete */ }) { Text("Complete with Photo") }
                        }
                    }
                }
            }
        }
    }
}
