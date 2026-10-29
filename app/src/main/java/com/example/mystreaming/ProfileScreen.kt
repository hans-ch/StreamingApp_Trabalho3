package com.example.mystreaming

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Tela de perfil.
 *
 * Alem dos dados fixos, mostra as estatisticas que vem do banco Room
 * (quantos filmes estao salvos na "Sua lista").
 */
@Composable
fun ProfileScreen(mainViewModel: MainViewModel) {
    val scope = rememberCoroutineScope()

    val minhaLista by mainViewModel.minhaLista.collectAsState()

    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                mainViewModel.carregarMinhaLista()
            } catch (e: Exception) {
                Log.e("ProfileScreen", "Erro ao carregar as estatisticas do banco", e)
            }
        }
    }

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

            Spacer(modifier = Modifier.height(12.dp))

            // Dado real vindo do banco Room
            Text(
                text = "Filmes salvos na sua lista: ${minhaLista.size}",
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
