package com.example.mystreaming

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mystreaming.data.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val AzulFundo = Color(0xFF1E2A78)
private val AzulTopBar = Color(0xFF091152)

/**
 * Tela de comentarios de um filme.
 *
 * Os comentarios sao buscados no banco pelo id do filme (dao.buscarPorFilme) e
 * cada card pode ser excluido de verdade (dao.deletar).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComentariosScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    filmeId: Int
) {
    val scope = rememberCoroutineScope()

    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var tituloFilme by remember { mutableStateOf("") }
    var carregando by remember { mutableStateOf(true) }

    // Busca os comentarios do filme direto do banco
    LaunchedEffect(filmeId) {
        scope.launch(Dispatchers.IO) {
            try {
                val lista = mainViewModel.listarReviews(filmeId)
                val filme = mainViewModel.buscarFilmePorId(filmeId)
                withContext(Dispatchers.Main) {
                    reviews = lista
                    tituloFilme = filme?.title ?: ""
                    carregando = false
                }
            } catch (e: Exception) {
                Log.e("ComentariosScreen", "Erro ao carregar os comentarios do banco", e)
                withContext(Dispatchers.Main) { carregando = false }
            }
        }
    }

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
                text = if (tituloFilme.isBlank()) "Reviews" else "Reviews de $tituloFilme",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (carregando) {
                Text(
                    text = "Carregando do banco...",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp)
                )
            } else if (reviews.isEmpty()) {

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

                    items(reviews) { review ->

                        ReviewCard(
                            review = review,
                            onClick = {
                                navController.navigate(Rotas.comentarioDetalhes(review.id))
                            },
                            onDelete = {
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        mainViewModel.deletarReview(review)
                                    } catch (e: Exception) {
                                        Log.e("ComentariosScreen", "Erro ao excluir o comentario", e)
                                    }
                                }
                            }
                        )
                    }

                }
            }
        }
    }
}

@Composable
fun ReviewCard(
    review: Review,
    onClick: () -> Unit = {},
    onDelete: () -> Unit = {}
) {

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = review.usuario,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                // Exclui o comentario de verdade (dao.deletar)
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Excluir comentario",
                        tint = Color.Red,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Row {

                repeat(review.nota) {
                    Text(
                        text = "★",
                        color = Color(0xFFFFC107),
                        fontSize = 18.sp
                    )
                }

                repeat(5 - review.nota) {
                    Text(
                        text = "★",
                        color = Color.LightGray,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = review.comentario,
                color = Color.DarkGray,
                fontSize = 14.sp,

                // Limita o texto na lista
                maxLines = 3,

                // Coloca "..." quando ultrapassar o limite
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true)
@Composable
fun ComentariosScreenPreview() {
    MaterialTheme {
        ComentariosScreen(
            navController = rememberNavController(),
            mainViewModel = MainViewModel(previewApplication()),
            filmeId = 1
        )
    }
}
