package com.example.mystreaming

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val AzulFundo = Color(0xFF1E2A78)
private val AzulTopBar = Color(0xFF091152)

/**
 * Tela "Minha Lista" (grid).
 *
 * A lista vem do banco (dao.buscarDaMinhaLista) e o X em cada card remove o
 * filme da lista do usuario (UPDATE da coluna naMinhaLista).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinhaListaGridScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    val minhaLista by mainViewModel.minhaLista.collectAsState()

    // Busca a lista do banco assim que a tela abre
    LaunchedEffect(Unit) {
        scope.launch(Dispatchers.IO) {
            try {
                mainViewModel.carregarMinhaLista()
            } catch (e: Exception) {
                Log.e("MinhaListaGridScreen", "Erro ao carregar a lista do banco", e)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Minha Lista") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
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
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AzulFundo)
                .padding(innerPadding)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (minhaLista.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sua lista está vazia",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(minhaLista) { filme ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Color.Black)
                                .clickable { navController.navigate(Rotas.descricao(filme.id)) }
                        ){
                            Text(
                                text = filme.title,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 2,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.75f))
                                    .padding(6.dp)
                            )

                            // Remove o filme da "Sua lista" (UPDATE no banco)
                            IconButton(
                                onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        try {
                                            mainViewModel.alternarMinhaLista(filme)
                                        } catch (e: Exception) {
                                            Log.e("MinhaListaGridScreen", "Erro ao remover da lista", e)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Remover da lista",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MinhaListaGridScreenPreview() {
    MaterialTheme {
        MinhaListaGridScreen(navController = rememberNavController(), mainViewModel = MainViewModel(previewApplication()))
    }
}
