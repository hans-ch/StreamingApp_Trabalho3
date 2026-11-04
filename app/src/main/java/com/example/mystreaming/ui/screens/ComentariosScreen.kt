package com.example.mystreaming.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.mystreaming.ui.components.ReviewCard
import com.example.mystreaming.ui.navigation.Rotas
import com.example.mystreaming.viewmodel.ReviewsViewModel

private val AzulFundo = Color(0xFF1E2A78)
private val AzulTopBar = Color(0xFF091152)

/**
 * Tela de comentarios de um filme.
 *
 * Os comentarios chegam pelo `uiState` (que espelha o Flow do banco) e cada
 * card pode ser excluido de verdade.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComentariosScreen(
    navController: NavHostController,
    viewModel: ReviewsViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Comentários")
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AzulTopBar,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AzulFundo)
                .padding(padding)
        ) {

            Text(
                text = if (uiState.tituloFilme.isBlank()) "Reviews" else "Reviews de ${uiState.tituloFilme}",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (uiState.carregando) {
                Text(
                    text = "Carregando do banco...",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (uiState.reviews.isEmpty()) {

                Text(
                    text = "Nenhum comentário ainda.",
                    color = Color.White,
                    modifier = Modifier.padding(16.dp)
                )

            } else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    items(uiState.reviews) { review ->

                        ReviewCard(
                            review = review,
                            onClick = {
                                navController.navigate(Rotas.comentarioDetalhes(review.id))
                            },
                            onDelete = {
                                viewModel.excluirReview(review)
                            }
                        )
                    }

                }
            }
        }
    }
}
