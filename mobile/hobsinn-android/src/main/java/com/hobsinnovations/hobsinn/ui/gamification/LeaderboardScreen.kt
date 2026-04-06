package com.hobsinnovations.hobsinn.ui.gamification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LeaderboardScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F6F4))
            .padding(16.dp)
    ) {
        Text("Community Cleanup Battles \uD83C\uDFC6", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF182A18))
        Spacer(modifier = Modifier.height(8.dp))
        Text("See who is keeping the community clean and safe from diseases.", fontSize = 14.sp, color = Color.Gray)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A73E8))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Your Stats", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("450", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Eco Points", fontSize = 10.sp, color = Color(0xFFD2E3FC))
                    }
                    Column {
                        Text("45 kg", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Waste Cleared", fontSize = 10.sp, color = Color(0xFFD2E3FC))
                    }
                    Column {
                        Row {
                            Text("\uD83C\uDF96️", fontSize = 18.sp)
                            Text("\uD83E\uDD81", fontSize = 18.sp)
                        }
                        Text("Badges", fontSize = 10.sp, color = Color(0xFFD2E3FC))
                    }
                }
            }
        }

        Text("Top Waste Warriors", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF182A18))
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(10) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(30.dp).background(if(index < 3) Color(0xFFFFCC00) else Color(0xFFDDEADD), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("#${index + 1}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = if(index < 3) Color.DarkGray else Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(if(index == 0) "Joseph Kamga \uD83D\uDC51" else "User $index", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(if(index % 2 == 0) "Molyko" else "Bonaberi", fontSize = 12.sp, color = Color.Gray)
                        }
                        Text("${1000 - (index * 50)} pts", fontWeight = FontWeight.Bold, color = Color(0xFF1DB954))
                    }
                }
            }
        }
    }
}
