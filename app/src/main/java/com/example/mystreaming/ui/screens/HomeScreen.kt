package com.example.mystreaming.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import com.example.mystreaming.ui.components.MediaSection
import com.example.mystreaming.ui.navigation.Rotas
import com.example.mystreaming.viewmodel.FilmesViewModel

private val AzulTopBar = Color(0xFF091152)

/**
 * Tela inicial.
 *
 * A tela faz apenas duas coisas: coleta o `uiState` do ViewModel para saber o
 * que desenhar, e chama os metodos do ViewModel quando o usuario interage.
 * Nenhum DAO, nenhuma CoroutineScope, nenhum Dispatchers.IO aqui dentro.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: FilmesViewModel,
    onVerMinhaListaClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home") },
                actions = {
                    IconButton(onClick = { viewModel.abrirFormulario() }) {
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

            if (uiState.erro != null) {
                Text(
                    text = uiState.erro!!,
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (uiState.carregando && uiState.filmes.isEmpty()) {
                Text(
                    text = "Carregando do banco...",
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }

            MediaSection(
                title = "Recomendações",
                items = uiState.filmes,
                onFilmeClick = { filme -> navController.navigate(Rotas.descricao(filme.id)) }
            )

            MediaSection(
                title = "Novidades",
                items = uiState.filmes,
                onFilmeClick = { filme -> navController.navigate(Rotas.descricao(filme.id)) }
            )

            MediaSection(
                title = "Sua lista",
                items = uiState.minhaLista,
                onFilmeClick = { filme -> navController.navigate(Rotas.descricao(filme.id)) },
                onTitleClick = onVerMinhaListaClick,
                isClicavel = true
            )
        }
    }

    // ------------------------------------------------------------------ Formulario

    if (uiState.mostrarFormulario) {
        AlertDialog(
            onDismissRequest = { viewModel.fecharFormulario() },
            title = { Text("Adicionar filme") },
            text = {
                Column {
                    OutlinedTextField(
                        value = uiState.titulo,
                        onValueChange = { viewModel.onTituloChange(it) },
                        label = { Text("Titulo") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.ano,
                        onValueChange = { viewModel.onAnoChange(it) },
                        label = { Text("Ano") },
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.nota,
                        onValueChange = { viewModel.onNotaChange(it) },
                        label = { Text("Nota") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.adicionarFilme() }) { Text("Salvar") }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.fecharFormulario() }) { Text("Cancelar") }
            }
        )
    }
}
