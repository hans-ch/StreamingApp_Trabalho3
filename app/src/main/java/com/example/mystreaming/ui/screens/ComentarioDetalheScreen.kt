package com.example.mystreaming.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.mystreaming.viewmodel.ReviewsViewModel

private val AzulFundo = Color(0xFF1E2A78)
private val AzulTopBar = Color(0xFF091152)

/**
 * Tela de detalhe de um comentario.
 *
 * O comentario chega pelo `uiState` (buscado no banco pelo id da rota).
 * Permite editar e excluir.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComentarioDetalheScreen(
    navController: NavHostController,
    viewModel: ReviewsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                    IconButton(onClick = { viewModel.abrirEdicao() }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar comentario",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            val review = uiState.reviewSelecionado ?: return@IconButton
                            viewModel.excluirReview(review)
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

        if (uiState.carregando) {
            Text(
                text = "Carregando do banco...",
                color = Color.White,
                fontSize = 14.sp
            )
            return@Column
        }

        val review = uiState.reviewSelecionado

        if (review == null) {
            Text(
                text = uiState.erro ?: "Comentario nao encontrado",
                color = Color.White,
                fontSize = 14.sp
            )
            return@Column
        }

        Text(
            text = review.usuario,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        Row {

            repeat(review.nota) {

                Text(
                    text = "★",
                    color = Color(0xFFFFC107),
                    fontSize = 22.sp
                )
            }

            repeat(5 - review.nota) {

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
                text = review.comentario,
                color = Color.Black,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

    // ------------------------------------------------------------------ Editar

    if (uiState.mostrarEdicao) {
        AlertDialog(
            onDismissRequest = { viewModel.fecharEdicao() },
            title = { Text("Editar comentario") },
            text = {
                Column {
                    OutlinedTextField(
                        value = uiState.textoEdicao,
                        onValueChange = { viewModel.onTextoEdicaoChange(it) },
                        label = { Text("Comentario") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        for (i in 1..5) {
                            Icon(
                                imageVector = if (i <= uiState.notaEdicao) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "Estrela $i",
                                tint = if (i <= uiState.notaEdicao) Color(0xFFFFC107) else Color.LightGray,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { viewModel.onNotaEdicaoChange(i) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.salvarEdicao() }) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.fecharEdicao() }) { Text("Cancelar") }
            }
        )
    }
}
