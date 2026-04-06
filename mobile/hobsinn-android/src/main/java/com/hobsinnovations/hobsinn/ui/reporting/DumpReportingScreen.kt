package com.hobsinnovations.hobsinn.ui.reporting

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DumpReportingScreen() {
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Acquiring GPS...") }
    
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Report Major Dump", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Location: $location")
        Button(onClick = { location = "Lat: 4.15, Lng: 9.24" }) {
            Text("Acquire GPS")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { /* Capture Photo */ }) {
            Text("Take Photo")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { /* Submit Report */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Submit Report")
        }
    }
}
