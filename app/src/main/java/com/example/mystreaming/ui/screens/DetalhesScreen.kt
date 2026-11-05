package com.example.mystreaming.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.example.mystreaming.R
import com.example.mystreaming.ui.components.CardComentarioItem
import com.example.mystreaming.ui.navigation.Rotas
import com.example.mystreaming.viewmodel.FilmesViewModel
import com.example.mystreaming.viewmodel.ReviewsViewModel

private val AzulFundo = Color(0xFF1E2A78)
private val AzulTopBar = Color(0xFF091152)
private val AzulBotao = Color(0xFF5375D6)

/**
 * Tela de Detalhes do filme.
 *
 * O filme e os comentarios chegam pelo `uiState` dos ViewModels: o ViewModel de
 * filmes buscou o filme no banco pelo id da rota e o ViewModel de reviews
 * observa a lista (em Flow) das avaliacoes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesScreen(
    navController: NavHostController,
    filmesViewModel: FilmesViewModel,
    reviewsViewModel: ReviewsViewModel
) {
    val filmesState by filmesViewModel.uiState.collectAsStateWithLifecycle()
    val reviewsState by reviewsViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { filmesViewModel.abrirEdicao() }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar filme",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { filmesViewModel.pedirExclusao() }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Excluir filme",
                            tint = Color.White
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_logo_negativo),
                        contentDescription = "Logo Vihanny",
                        modifier = Modifier
                            .height(90.dp)
                            .padding(end = 16.dp)
                    )
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
                .padding(padding)
                .background(AzulFundo)
                .safeDrawingPadding()
                .padding(20.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            if (filmesState.carregando) {
                Text(
                    text = "Carregando do banco...",
                    fontSize = 14.sp,
                    color = Color.White
                )
                return@Column
            }

            val filme = filmesState.filmeSelecionado

            if (filme == null) {
                Text(
                    text = filmesState.erro ?: "Filme nao encontrado no banco",
                    fontSize = 14.sp,
                    color = Color.White
                )
                return@Column
            }

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(180.dp)
                        .background(Color.Black)
                ) {
                    Text(
                        text = "+",
                        color = Color.White,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = filme.title,
                        fontSize = 24.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "14",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .background(Color(0xFFFF9800))
                                .padding(2.dp)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = filme.year,
                            fontSize = 12.sp,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = filme.rating,
                            fontSize = 12.sp,
                            color = Color.Black
                        )

                        Text(
                            text = "★",
                            fontSize = 12.sp,
                            color = Color.Yellow
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
                        "Sed do eiusmod tempor incididunt ut labore et dolore magna " +
                        "aliqua. Ut enim ad minim veniam, quis nostrud exercitation " +
                        "ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
                        "Duis aute irure dolor in reprehenderit in voluptate velit " +
                        "esse cillum dolore eu fugiat nulla pariatur.",
                fontSize = 10.sp,
                lineHeight = 12.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- BOTÕES DE AÇÃO ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { navController.navigate(Rotas.avaliacao(filme.id)) },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBotao),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Avaliar", fontSize = 10.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { filmesViewModel.alternarMinhaLista(filme) },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBotao),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Text(
                        text = if (filme.naMinhaLista) "- lista" else "+ lista",
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { /* Ação Compartilhar */ },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBotao),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1.1f)
                ) {
                    Text("Compartilhar", fontSize = 10.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- SEÇÃO DE COMENTÁRIOS ---
            Text(
                text = "Comentários",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (reviewsState.reviews.isEmpty()) {
                Text(
                    text = "Nenhum comentario ainda.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                reviewsState.reviews.take(2).forEach { review ->
                    CardComentarioItem(
                        review = review,
                        onClick = {
                            navController.navigate(Rotas.comentarioDetalhes(review.id))
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable { navController.navigate(Rotas.comentarios(filme.id)) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Mais comentários",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // ------------------------------------------------------------------ Editar

    if (filmesState.mostrarEdicao) {
        AlertDialog(
            onDismissRequest = { filmesViewModel.fecharEdicao() },
            title = { Text("Editar filme") },
            text = {
                Column {
                    OutlinedTextField(
                        value = filmesState.tituloEdicao,
                        onValueChange = { filmesViewModel.onTituloEdicaoChange(it) },
                        label = { Text("Titulo") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = filmesState.anoEdicao,
                        onValueChange = { filmesViewModel.onAnoEdicaoChange(it) },
                        label = { Text("Ano") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = filmesState.notaEdicao,
                        onValueChange = { filmesViewModel.onNotaEdicaoChange(it) },
                        label = { Text("Nota") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { filmesViewModel.salvarEdicao() }) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { filmesViewModel.fecharEdicao() }) { Text("Cancelar") }
            }
        )
    }

    // ------------------------------------------------------------------ Excluir

    if (filmesState.mostrarExclusao) {
        AlertDialog(
            onDismissRequest = { filmesViewModel.cancelarExclusao() },
            title = { Text("Excluir filme") },
            text = { Text("Esse filme sera removido do banco. As avaliacoes dele tambem serao apagadas.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        filmesViewModel.confirmarExclusao()
                        navController.popBackStack()
                    }
                ) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { filmesViewModel.cancelarExclusao() }) { Text("Cancelar") }
            }
        )
    }
}
