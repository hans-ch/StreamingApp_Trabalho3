package com.example.mystreaming

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mystreaming.sampleMediaList


//Data Class para os comentários
data class Comentario(
    val id: String,
    val usuario: String,
    val texto: String
)

// Lista de exemplo
val listaComentariosExemplo = listOf(
    Comentario("1", "Usuário 1", "Lorem ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the Industry's standard dummy..."),
    Comentario("2", "Usuário 1", "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
            "Sed do eiusmod tempor incididunt ut labore et dolore magna " +
            "aliqua. Ut enim ad minim veniam, quis nostrud exercitation " +
            "ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
            "Duis aute irure dolor in reprehenderit in voluptate velit " +
            "esse cillum dolore eu fugiat nulla pariatur."),
    Comentario("3", "Usuário 2", "Excelente filme! Recomendo a todos.")
)

private val AzulFundo = Color(0xFF1E2A78)
private val AzulTopBar = Color(0xFF091152)
private val AzulBotao = Color(0xFF5375D6)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesScreen(
    navController: NavHostController,
    filme: MediaItem = sampleMediaList.first(),
    isNaMinhaLista: Boolean = false,
    onAdicionarOuRemoverMinhaLista: (MediaItem) -> Unit = {}
) {
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
                    onClick = { navController.navigate("avaliacao") },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBotao),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Avaliar", fontSize = 10.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onAdicionarOuRemoverMinhaLista(filme) },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBotao),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Text("+ lista", fontSize = 10.sp, color = Color.White)
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

            listaComentariosExemplo.take(2).forEach { comentario ->
                CardComentarioItem(
                    comentario = comentario,
                    onClick = {
                        navController.navigate("comentarios")
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .clickable { navController.navigate("comentarios") },
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
}

@Composable
fun CardComentarioItem(
    comentario: Comentario,
    onClick: () -> Unit,
    limiteCaracteres: Int = 100
) {

    val textoExibido = if (comentario.texto.length > limiteCaracteres) {
        "${comentario.texto.take(limiteCaracteres)}..."
    } else {
        comentario.texto
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Text(
            text = comentario.usuario,
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

@Preview(showBackground = true)
@Composable
fun TelaDetalhesPreview(){
    MaterialTheme{
        DetalhesScreen(navController = rememberNavController(),
            isNaMinhaLista = false,
            onAdicionarOuRemoverMinhaLista = {})
    }
}
