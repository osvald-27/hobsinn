package com.hobsinnovations.hobsinn.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PickupUserSetupScreen() {
    val scrollState = rememberScrollState()
    var specialCallEnabled by remember { mutableStateOf(true) }
    var weeklyPrice by remember { mutableStateOf("350") }
    var specialCallPrice by remember { mutableStateOf("600") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B2414))
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Step 1: Availability
        Text("01 - Profile & Availability Days", color = Color.Gray, fontSize = 12.sp)
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F6F4))) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Which days are you available?", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    val days = listOf("TUE", "WED", "THU", "FRI", "SAT")
                    for (day in days) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(2.dp)
                                .background(Color(0xFF1DB954), RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(day, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("Which time slots will you cover?", fontWeight = FontWeight.Bold)
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), border = BorderStroke(1.dp, Color(0xFF1DB954)), colors = CardDefaults.cardColors(containerColor = Color(0xFFD4F5E0))) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("✓", color = Color.White, modifier = Modifier.background(Color(0xFF1DB954), RoundedCornerShape(50)).padding(4.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("6:00 AM - 8:00 AM", fontWeight = FontWeight.Bold)
                            Text("Avg. 4-6 bookings per day", fontSize = 10.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }

        // Step 2: Pricing
        Text("02 - Pricing & MoMo Setup", color = Color.Gray, fontSize = 12.sp)
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Set your price per bag \uD83D\uDCB0", fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = weeklyPrice,
                    onValueChange = { weeklyPrice = it },
                    label = { Text("Weekly Slot (XAF)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
                OutlinedTextField(
                    value = specialCallPrice,
                    onValueChange = { specialCallPrice = it },
                    label = { Text("Special Call (XAF)") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text("Your MoMo number", fontWeight = FontWeight.Bold, color = Color(0xFF6B4F00))
                OutlinedTextField(
                    value = "677 432 891",
                    onValueChange = { },
                    leadingIcon = { Text("+237") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Step 3: Special Calls
        Text("03 - Special Calls & Go Live", color = Color.Gray, fontSize = 12.sp)
        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Accept Special Calls", fontWeight = FontWeight.Bold)
                        Text("Urgent requests outside set slots", fontSize = 11.sp, color = Color.Gray)
                    }
                    Switch(checked = specialCallEnabled, onCheckedChange = { specialCallEnabled = it })
                }
                
                if (specialCallEnabled) {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).background(Color(0xFFE8F0FE), RoundedCornerShape(8.dp)).padding(12.dp)) {
                        Column {
                            Text("ℹ️ What is a Special Call?", color = Color(0xFF1A73E8), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text("An urgent, on-demand pickup request outside normal slots. Charge a higher rate. No penalty for declining.", fontSize = 11.sp, color = Color.DarkGray)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* Go Live */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1DB954))
                ) {
                    Text("\uD83D\uDE80 Go Live — Start Receiving Jobs")
                }
            }
        }
    }
}
