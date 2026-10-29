package com.example.mystreaming

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.mystreaming.data.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val AzulFundo = Color(0xFF1E2A78)
private val AzulTopBar = Color(0xFF091152)

/**
 * Tela de detalhe de um comentario.
 *
 * Recebe apenas o **id** da avaliacao e busca o objeto no banco Room
 * (dao.buscarPorId). Permite editar (dao.atualizar) e excluir (dao.deletar).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComentarioDetalhesScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    reviewId: Int
) {
    val scope = rememberCoroutineScope()

    var review by remember { mutableStateOf<Review?>(null) }
    var carregando by remember { mutableStateOf(true) }

    var mostrarEdicao by remember { mutableStateOf(false) }
    var textoEdicao by remember { mutableStateOf("") }
    var notaEdicao by remember { mutableStateOf(3) }

    // Busca a avaliacao pelo id, direto do banco
    LaunchedEffect(reviewId) {
        scope.launch(Dispatchers.IO) {
            try {
                val r = mainViewModel.buscarReviewPorId(reviewId)
                withContext(Dispatchers.Main) {
                    review = r
                    carregando = false
                }
            } catch (e: Exception) {
                Log.e("ComentarioDetalhesScreen", "Erro ao buscar o comentario $reviewId", e)
                withContext(Dispatchers.Main) { carregando = false }
            }
        }
    }

    Scaffold(
        topBar = {

            TopAppBar(
                title = {
                    Text("Comentário")
                },

                navigationIcon = {

                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },

                actions = {
                    IconButton(
                        onClick = {
                            review?.let {
                                textoEdicao = it.comentario
                                notaEdicao = it.nota
                                mostrarEdicao = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar comentario",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            val alvo = review ?: return@IconButton
                            scope.launch(Dispatchers.IO) {
                                try {
                                    mainViewModel.deletarReview(alvo)
                                } catch (e: Exception) {
                                    Log.e("ComentarioDetalhesScreen", "Erro ao excluir o comentario", e)
                                }
                            }
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Excluir comentario",
                            tint = Color.White
                        )
                    }
                },

                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulTopBar,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ))
        }


    ) { padding ->

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AzulFundo)
            .padding(padding)
            .padding(20.dp)
    ) {

        if (carregando) {
            Text(
                text = "Carregando do banco...",
                color = Color.White,
                fontSize = 14.sp
            )
            return@Column
        }

        val reviewAtual = review

        if (reviewAtual == null) {
            Text(
                text = "Comentario nao encontrado",
                color = Color.White,
                fontSize = 14.sp
            )
            return@Column
        }

        Text(
            text = reviewAtual.usuario,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row {

            repeat(reviewAtual.nota) {

                Text(
                    text = "★",
                    color = Color(0xFFFFC107),
                    fontSize = 22.sp
                )
            }

            repeat(5 - reviewAtual.nota) {

                Text(
                    text = "★",
                    color = Color.LightGray,
                    fontSize = 22.sp
                )
            }
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {

            Text(
                text = reviewAtual.comentario,
                color = Color.Black,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

    // ------------------------------------------------------------------ Editar

    if (mostrarEdicao) {
        AlertDialog(
            onDismissRequest = { mostrarEdicao = false },
            title = { Text("Editar comentario") },
            text = {
                Column {
                    OutlinedTextField(
                        value = textoEdicao,
                        onValueChange = { textoEdicao = it },
                        label = { Text("Comentario") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= notaEdicao) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Estrela $i",
                                tint = if (i <= notaEdicao) Color(0xFFFFC107) else Color.LightGray,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { notaEdicao = i }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val alvo = review ?: return@TextButton

                        val atualizado = alvo.copy(
                            comentario = textoEdicao.trim().ifBlank { alvo.comentario },
                            nota = notaEdicao
                        )

                        scope.launch(Dispatchers.IO) {
                            try {
                                mainViewModel.atualizarReview(atualizado)
                                withContext(Dispatchers.Main) { review = atualizado }
                            } catch (e: Exception) {
                                Log.e("ComentarioDetalhesScreen", "Erro ao atualizar o comentario", e)
                            }
                        }

                        mostrarEdicao = false
                    }
                ) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarEdicao = false }) { Text("Cancelar") }
            }
        )
    }
}
