package com.example.mystreaming

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.ui.text.style.TextOverflow


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComentariosScreen(
    navController: NavHostController,
    filme: MediaItem
) {
    // Pega somente os comentários desse filme
    val reviewsDoFilme = sampleReviews.filter {
        it.filmeId == filme.id
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
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E2A78))
                .padding(padding)
        ) {

            Text(
                text = "Reviews de ${filme.title}",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (reviewsDoFilme.isEmpty()) {

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

                    items(reviewsDoFilme) { review ->

                        ReviewCard(
                            review = review,
                            onClick = {
                                navController.navigate(
                                    "${Rotas.COMENTARIO_DETALHES}/${review.id}"
                                )
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
    onClick: () -> Unit = {}
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

            Text(
                text = review.usuario,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

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

