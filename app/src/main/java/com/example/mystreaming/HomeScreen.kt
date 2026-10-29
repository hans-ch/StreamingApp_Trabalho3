package com.example.mystreaming

import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.mystreaming.data.MediaItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val AzulTopBar = Color(0xFF091152)

/**
 * Tela inicial.
 *
 * As secoes "Recomendacoes", "Novidades" e "Sua lista" sao preenchidas com o
 * que vem do banco Room (MainViewModel -> dao.buscarTodos / dao.buscarDaMinhaLista).
 * O botao "+" da TopAppBar abre o formulario de adicionar filme, que grava de
 * verdade no banco (dao.inserir).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel,
    onVerMinhaListaClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    val filmes by mainViewModel.filmes.collectAsState()
    val minhaLista by mainViewModel.minhaLista.collectAsState()

    var carregando by remember { mutableStateOf(true) }
    var erro by remember { mutableStateOf<String?>(null) }

    // Formulario de adicionar filme (dialogo, sem criar uma tela nova)
    var mostrarFormulario by remember { mutableStateOf(false) }
    var titulo by remember { mutableStateOf("") }
    var ano by remember { mutableStateOf("") }
    var nota by remember { mutableStateOf("") }

    // Busca os dados do banco assim que a tela abre
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                mainViewModel.carregarFilmes()
                mainViewModel.carregarMinhaLista()
            } catch (e: Exception) {
                Log.e("HomeScreen", "Erro ao carregar os filmes do banco", e)
                withContext(Dispatchers.Main) { erro = "Nao foi possivel carregar os filmes" }
            }
            withContext(Dispatchers.Main) { carregando = false }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(onClick = { mostrarFormulario = true }) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Adicionar filme",
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
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E2A78))
                .padding(padding)
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

            if (erro != null) {
                Text(
                    text = erro!!,
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (carregando && filmes.isEmpty()) {
                Text(
                    text = "Carregando do banco...",
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            MediaSection(
                title = "Recomendações",
                items = filmes,
                onFilmeClick = { filme -> navController.navigate(Rotas.descricao(filme.id)) }
            )

            MediaSection(
                title = "Novidades",
                items = filmes,
                onFilmeClick = { filme -> navController.navigate(Rotas.descricao(filme.id)) }
            )

            MediaSection(
                title = "Sua lista",
                items = minhaLista,
                onFilmeClick = { filme -> navController.navigate(Rotas.descricao(filme.id)) },
                onTitleClick = onVerMinhaListaClick,
                isClicavel = true
            )
        }
    }

    // ------------------------------------------------------------------ Formulario

    if (mostrarFormulario) {
        AlertDialog(
            onDismissRequest = { mostrarFormulario = false },
            title = { Text("Adicionar filme") },
            text = {
                Column {
                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Titulo") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ano,
                        onValueChange = { ano = it },
                        label = { Text("Ano") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = nota,
                        onValueChange = { nota = it },
                        label = { Text("Nota") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (titulo.isBlank()) return@TextButton

                        val novo = MediaItem(
                            title = titulo.trim(),
                            year = ano.trim().ifBlank { "—" },
                            rating = nota.trim().ifBlank { "—" },
                            naMinhaLista = true
                        )

                        scope.launch(Dispatchers.IO) {
                            try {
                                mainViewModel.inserirFilme(novo)
                            } catch (e: Exception) {
                                Log.e("HomeScreen", "Erro ao salvar o filme no banco", e)
                            }
                        }

                        titulo = ""
                        ano = ""
                        nota = ""
                        mostrarFormulario = false
                    }
                ) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarFormulario = false }) { Text("Cancelar") }
            }
        )
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
        HomeScreen(navController = rememberNavController(), mainViewModel = MainViewModel(previewApplication()))
    }
}
