package com.example.mystreaming

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mystreaming.data.Review
import com.example.mystreaming.data.USUARIO_LOGIN
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val AzulFundo = Color(0xFF1E2A78)
private val AzulTopBar = Color(0xFF091152)
private val AzulBotao = Color(0xFF5375D6)

/**
 * Tela de avaliacao do filme.
 *
 * O formulario (estrelas + comentario) que ja existia no Trabalho 2 agora grava
 * de verdade no banco Room: o botao ENVIAR chama dao.inserir com o filmeId
 * recebido pela navegacao.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    filmeId: Int
) {
    val scope = rememberCoroutineScope()

    var notaEstrelas by remember { mutableStateOf(3) } // Padrão 3 estrelas pré-selecionadas como na imagem
    var comentarioTexto by remember { mutableStateOf("") }
    var nomeFilme by remember { mutableStateOf("") }
    var enviado by remember { mutableStateOf(false) }

    // Carrega o nome do filme direto do banco
    LaunchedEffect(filmeId) {
        scope.launch(Dispatchers.IO) {
            try {
                val filme = mainViewModel.buscarFilmePorId(filmeId)
                withContext(Dispatchers.Main) { nomeFilme = filme?.title ?: "" }
            } catch (e: Exception) {
                Log.e("ReviewScreen", "Erro ao buscar o filme $filmeId no banco", e)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Avaliar Filme", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_negativo),
                        contentDescription = "Logo Vihanny",
                        modifier = Modifier
                            .height(90.dp)
                            .padding(end = 16.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulTopBar
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AzulFundo)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- TÍTULO DO FILME DINÂMICO ---
            Text(
                text = if (nomeFilme.isBlank()) "Filme" else nomeFilme,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // --- POSTER/CAPA PRETA DO FILME ---
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(300.dp)
                    .background(Color.Black, shape = RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- SISTEMA DE AVALIAÇÃO POR ESTRELAS (1 A 5 ESTRELAS) ---
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= notaEstrelas) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Estrela $i",
                        tint = if (i <= notaEstrelas) Color.Yellow else Color.Black,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { notaEstrelas = i } // Permite ao usuário selecionar a nota
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- CAIXA DE TEXTO (COMENTÁRIO ESTILO O DA IMAGEM) ---
            TextField(
                value = comentarioTexto,
                onValueChange = { comentarioTexto = it },
                placeholder = { Text("Comentário...", color = Color.DarkGray, fontSize = 18.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(4.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF8E8E8E),
                    unfocusedContainerColor = Color(0xFF8E8E8E),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- BOTÃO ENVIAR ---
            Button(
                onClick = {
                    if (enviado) return@Button

                    val novaReview = Review(
                        filmeId = filmeId,
                        usuario = USUARIO_LOGIN,
                        comentario = comentarioTexto.trim().ifBlank { "Sem comentario" },
                        nota = notaEstrelas
                    )

                    scope.launch(Dispatchers.IO) {
                        try {
                            mainViewModel.inserirReview(novaReview)
                            withContext(Dispatchers.Main) { enviado = true }
                        } catch (e: Exception) {
                            Log.e("ReviewScreen", "Erro ao salvar a avaliacao no banco", e)
                        }
                    }

                    // Executa a ação de envio e volta para a tela de detalhes
                    navController.popBackStack()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5375D6)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(48.dp)
            ) {
                Text("ENVIAR", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ReviewScreenPreview() {
    MaterialTheme {
        ReviewScreen(
            navController = rememberNavController(),
            mainViewModel = MainViewModel(previewApplication()),
            filmeId = 1
        )
    }
}
