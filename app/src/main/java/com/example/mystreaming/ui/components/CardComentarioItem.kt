package com.example.mystreaming.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mystreaming.data.local.Review

/**
 * Card compacto de comentario, usado no bloco de comentarios da tela de Detalhes.
 * Componente puro de interface: nao conhece banco nem ViewModel.
 */
@Composable
fun CardComentarioItem(
    review: Review,
    onClick: () -> Unit,
    limiteCaracteres: Int = 100
) {

    val textoExibido = if (review.comentario.length > limiteCaracteres) {
        "${review.comentario.take(limiteCaracteres)}..."
    } else {
        review.comentario
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(
            text = review.usuario,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black)
                .padding(12.dp)
        ) {
            Text(
                text = textoExibido,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                color = Color.White
            )
        }
    }
}
