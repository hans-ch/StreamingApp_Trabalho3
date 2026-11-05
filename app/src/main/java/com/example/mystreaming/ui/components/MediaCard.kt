package com.example.mystreaming.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mystreaming.data.local.MediaItem

/**
 * Secao horizontal de filmes (carrossel) usada na Home.
 * Componente puro de interface: nao conhece banco nem ViewModel.
 */
@Composable
fun MediaSection(
    title: String,
    items: List<MediaItem>,
    onFilmeClick: (MediaItem) -> Unit = {},
    onTitleClick: () -> Unit = {},
    isClicavel: Boolean = false
) {
    Column(modifier = Modifier.padding(vertical = 12.dp)) {
        // Título da Seção (Clicável se isClicavel == true)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (isClicavel) Modifier.clickable { onTitleClick() } else Modifier)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            if (isClicavel) {
                Text(
                    text = "Ver tudo >",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
            }
        }

        if (items.isEmpty() && isClicavel) {
            Text(
                text = "Nenhum filme adicionado à sua lista.",
                fontSize = 12.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        } else {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    MediaCard(
                        item = item,
                        onClick = { onFilmeClick(item) }
                    )
                }
            }
        }
    }
}

/** Card de um filme (capa, titulo, ano e nota). */
@Composable
fun MediaCard(item: MediaItem, onClick: () -> Unit = {}) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .height(180.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(6.dp)
            ) {
                Text(
                    text = item.title,
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = item.year, color = Color.LightGray, fontSize = 9.sp)
                    Text(text = "★ ${item.rating}", color = Color.Yellow, fontSize = 9.sp)
                }
            }
        }
    }
}
