package com.hobsinnovations.hobsinn.ui.scheduling

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GeneralSlotScreen() {
    var estimateBags by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Register for General Slot", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Available community slot: Saturdays, 8:00 AM")
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = estimateBags,
            onValueChange = { estimateBags = it },
            label = { Text("Estimated Weekly Bags") }
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { /* Register */ }) {
            Text("Register Slot")
        }
    }
}
