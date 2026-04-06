package com.hobsinnovations.hobsinn.ui.scheduling

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun SpecialCallScreen() {
    var bagCount by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Acquiring GPS...") }
    var dateTime by remember { mutableStateOf("Select Date & Time") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Schedule Special Call", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(text = "Location: $location")
        Button(onClick = { location = "Lat: 4.15, Lng: 9.24" }) {
            Text("Acquire GPS")
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = bagCount,
            onValueChange = { bagCount = it },
            label = { Text("Number of Bags") }
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { dateTime = "2026-04-10T14:00" }) {
            Text(dateTime)
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            scope.launch {
                // Mock API Call
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Request Pickup & Pay via MoMo")
        }
    }
}
