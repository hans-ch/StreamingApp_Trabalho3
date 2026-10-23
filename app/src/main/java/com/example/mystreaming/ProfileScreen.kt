package com.example.mystreaming

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        //avatar simples sem adicionar uma imagem
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFF1E2A78), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "A",
                color = Color.White,
                fontSize = 48.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Andrey Martins",
            color = Color.Black,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Telefone: (41) 99999-9999", color = Color.Black)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Localização: Curitiba", color = Color.Black)

            Spacer(modifier = Modifier.height(12.dp))

            Text("Preferencias: Ação, Aventura e Terror", color = Color.Black)
        }
    }
}