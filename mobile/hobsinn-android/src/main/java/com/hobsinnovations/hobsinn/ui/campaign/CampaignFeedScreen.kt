package com.hobsinnovations.hobsinn.ui.campaign

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CampaignFeedScreen() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Campaign Feed", style = MaterialTheme.typography.titleLarge)
        
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { /* Scan QR */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Ambassador: Scan Attendance QR")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Buea Central Dump Cleanup", style = MaterialTheme.typography.titleMedium)
                        Text("Target: 100,000 XAF | Raised: 45,000 XAF")
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(onClick = { /* Volunteer */ }) { Text("Volunteer") }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(onClick = { /* Contribute MoMo */ }) { Text("Donate") }
                        }
                    }
                }
            }
        }
    }
}
