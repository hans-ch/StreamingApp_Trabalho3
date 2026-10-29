package com.example.mystreaming

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.mystreaming.data.MediaItem
import com.example.mystreaming.data.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val AzulFundo = Color(0xFF1E2A78)
private val AzulTopBar = Color(0xFF091152)
private val AzulBotao = Color(0xFF5375D6)

/**
 * Tela de Detalhes do filme.
 *
 * A tela recebe apenas o **id** do filme (veio pela navegacao) e busca o objeto
 * direto no banco Room (Requisitos.txt, secao 3.3). As acoes da tela gravam de
 * verdade: "+ lista" (UPDATE), editar (UPDATE) e excluir (DELETE).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalhesScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    filmeId: Int
) {
    val scope = rememberCoroutineScope()

    var filme by remember { mutableStateOf<MediaItem?>(null) }
    var reviews by remember { mutableStateOf<List<Review>>(emptyList()) }
    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf<String?>(null) }

    var mostrarEdicao by remember { mutableStateOf(false) }
    var mostrarExclusao by remember { mutableStateOf(false) }

    // Editar filme
    var tituloEdicao by remember { mutableStateOf("") }
    var anoEdicao by remember { mutableStateOf("") }
    var notaEdicao by remember { mutableStateOf("") }

    // Busca o filme pelo id, direto do banco
    LaunchedEffect(filmeId) {
        scope.launch(Dispatchers.IO) {
            try {
                val f = mainViewModel.buscarFilmePorId(filmeId)
                val lista = mainViewModel.listarReviews(filmeId)
                withContext(Dispatchers.Main) {
                    filme = f
                    reviews = lista
                    carregando = false
                    if (f == null) erro = "Filme nao encontrado no banco"
                }
            } catch (e: Exception) {
                Log.e("DetalhesScreen", "Erro ao buscar o filme $filmeId no banco", e)
                withContext(Dispatchers.Main) {
                    carregando = false
                    erro = "Nao foi possivel carregar o filme"
                }
            }
        }
    }

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
                    IconButton(
                        onClick = {
                            filme?.let {
                                tituloEdicao = it.title
                                anoEdicao = it.year
                                notaEdicao = it.rating
                                mostrarEdicao = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Editar filme",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { mostrarExclusao = true }) {
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

            if (carregando) {
                Text(
                    text = "Carregando do banco...",
                    fontSize = 14.sp,
                    color = Color.White
                )
                return@Column
            }

            val filmeAtual = filme

            if (filmeAtual == null) {
                Text(
                    text = erro ?: "Filme nao encontrado",
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
                        text = filmeAtual.title,
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
                            text = filmeAtual.year,
                            fontSize = 12.sp,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = filmeAtual.rating,
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
                    onClick = { navController.navigate(Rotas.avaliacao(filmeAtual.id)) },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBotao),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Avaliar", fontSize = 10.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            try {
                                mainViewModel.alternarMinhaLista(filmeAtual)
                            } catch (e: Exception) {
                                Log.e("DetalhesScreen", "Erro ao atualizar a lista", e)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBotao),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Text(
                        text = if (filmeAtual.naMinhaLista) "- lista" else "+ lista",
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

            if (reviews.isEmpty()) {
                Text(
                    text = "Nenhum comentario ainda.",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                reviews.take(2).forEach { review ->
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
                    .clickable { navController.navigate(Rotas.comentarios(filmeAtual.id)) },
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

    if (mostrarEdicao) {
        AlertDialog(
            onDismissRequest = { mostrarEdicao = false },
            title = { Text("Editar filme") },
            text = {
                Column {
                    OutlinedTextField(
                        value = tituloEdicao,
                        onValueChange = { tituloEdicao = it },
                        label = { Text("Titulo") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = anoEdicao,
                        onValueChange = { anoEdicao = it },
                        label = { Text("Ano") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = notaEdicao,
                        onValueChange = { notaEdicao = it },
                        label = { Text("Nota") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val alvo = filme ?: return@TextButton

                        val atualizado = alvo.copy(
                            title = tituloEdicao.trim().ifBlank { alvo.title },
                            year = anoEdicao.trim().ifBlank { alvo.year },
                            rating = notaEdicao.trim().ifBlank { alvo.rating }
                        )

                        scope.launch(Dispatchers.IO) {
                            try {
                                mainViewModel.atualizarFilme(atualizado)
                                withContext(Dispatchers.Main) { filme = atualizado }
                            } catch (e: Exception) {
                                Log.e("DetalhesScreen", "Erro ao atualizar o filme", e)
                            }
                        }

                        mostrarEdicao = false
                    }
                ) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarEdicao = false }) { Text("Cancelar") }
            }
        )
    }

    // ------------------------------------------------------------------ Excluir

    if (mostrarExclusao) {
        AlertDialog(
            onDismissRequest = { mostrarExclusao = false },
            title = { Text("Excluir filme") },
            text = { Text("Esse filme sera removido do banco. As avaliacoes dele tambem serao apagadas.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val alvo = filme
                        mostrarExclusao = false

                        if (alvo != null) {
                            scope.launch(Dispatchers.IO) {
                                try {
                                    mainViewModel.deletarFilme(alvo)
                                } catch (e: Exception) {
                                    Log.e("DetalhesScreen", "Erro ao excluir o filme", e)
                                }
                            }
                        }

                        navController.popBackStack()
                    }
                ) { Text("Excluir") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarExclusao = false }) { Text("Cancelar") }
            }
        )
    }
}

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

@Preview(showBackground = true)
@Composable
fun TelaDetalhesPreview(){
    MaterialTheme{
        DetalhesScreen(navController = rememberNavController(), mainViewModel = MainViewModel(previewApplication()), filmeId = 1)
    }
}
