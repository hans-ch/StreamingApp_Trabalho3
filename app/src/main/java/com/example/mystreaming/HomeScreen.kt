package com.example.mystreaming

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import com.example.mystreaming.sampleMediaList


//Item review e seus atributos
data class Review(
    val id: Int,
    val filmeId: Int,
    val usuario: String,
    val comentario: String,
    val nota: Int
)

private val AzulFundo = Color(0xFF172B9E)
private val AzulTopBar = Color(0xFF091152)
private val AzulBotao = Color(0xFF5375D6)

val sampleReviews = listOf(
    Review(1, 1, "Joséfa",
        "As historias da cultura chinesa são muito loucas... Otimo filme",
        5),
    Review(2, 1, "Irineu",
        "Quem era o vilão no final...? Vc não sabe, nem eu",
        4),
    Review(3, 2, "Aristides",
        "Esse filme nem existe. Incrivel 10/10",
        5))


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    minhaLista: List<MediaItem> = emptyList(),
    onFilmeClick: (MediaItem) -> Unit = {},
    onVerMinhaListaClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
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
                    containerColor = AzulTopBar,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E2A78))
                .verticalScroll(rememberScrollState())
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LogoApp",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            MediaSection(
                title = "Recomendações",
                items = sampleMediaList,
                onFilmeClick = onFilmeClick
            )

            MediaSection(
                title = "Novidades",
                items = sampleMediaList,
                onFilmeClick = onFilmeClick
            )

            MediaSection(
                title = "Sua lista",
                items = minhaLista,
                onFilmeClick = onFilmeClick,
                onTitleClick = onVerMinhaListaClick,
                isClicavel = true
            )
        }
    }
}

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

@Preview(showBackground = true)
@Composable
fun TelaHomePreview(){
    MaterialTheme{
        HomeScreen(navController = rememberNavController())
    }
}
