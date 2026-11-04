package com.example.mystreaming.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mystreaming.data.local.Review

/**
 * Card de uma avaliacao (autor, estrelas e texto), usado na tela de Comentarios.
 * Componente puro de interface: nao conhece banco nem ViewModel.
 */
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
