package com.example.mystreaming

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mystreaming.MainViewModel
import com.example.mystreaming.sampleMediaList

@Composable
fun AppNavigation(mainViewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()

    // Guarda o ID do filme escolhido.
    var filmeId by rememberSaveable {
        mutableStateOf(sampleMediaList.first().id)
    }

    // Observa o estado reativo da "Sua lista" a partir do ViewModel
    val minhaLista by mainViewModel.minhaLista.collectAsState()

    var filmeIdSelecionado by remember { mutableStateOf(sampleMediaList.first().id) }
    val filmeAtual = sampleMediaList.firstOrNull { it.id == filmeIdSelecionado } ?: sampleMediaList.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        NavHost(
            navController = navController,
            startDestination = Rotas.HOME,
            modifier = Modifier.weight(1f)
        ) {
            composable(Rotas.HOME) {
                HomeScreen(
                    navController = navController,
                    minhaLista = minhaLista,
                    onFilmeClick = { filme ->
                        filmeIdSelecionado = filme.id
                        navController.navigate(Rotas.DESCRICAO)
                    },
                    onVerMinhaListaClick = {
                        navController.navigate(Rotas.MINHA_LISTA_GRID)
                    }
                )
            }

            composable(Rotas.DESCRICAO) {
                DetalhesScreen(
                    navController = navController,
                    filme = filmeAtual,
                    isNaMinhaLista = mainViewModel.isNaMinhaLista(filmeAtual.id),
                    onAdicionarOuRemoverMinhaLista = { filme: MediaItem ->
                        mainViewModel.toggleMinhaLista(filme)
                    }
                )
            }

            composable(Rotas.COMENTARIOS){
                ComentariosScreen(
                    navController = navController,
                    filme = sampleMediaList.first{ it.id == filmeId }
                )
            }

            composable(
                route = "${Rotas.COMENTARIO_DETALHES}/{reviewId}"
            ) { backStackEntry ->

                val reviewId = backStackEntry
                    .arguments
                    ?.getString("reviewId")
                    ?.toIntOrNull()

                val review = sampleReviews.firstOrNull {
                    it.id == reviewId
                }

                if (review != null) {

                    ComentarioDetalhesScreen(
                        navController = navController,
                        review = review
                    )
                }
            }


            composable(Rotas.MINHA_LISTA_GRID) {
                MinhaListaGridScreen(
                    navController = navController,
                    minhaLista = minhaLista,
                    onFilmeClick = { filme ->
                        filmeId = filme.id
                        navController.navigate(Rotas.DESCRICAO)
                    }
                )
            }

            composable("perfil") {
                ProfileScreen()
            }

            composable("avaliacao") {
                ReviewScreen(navController = navController)
            }
        }

        // Apenas Início e Perfil na barra inferior.
        Row(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                onClick = {
                    navController.popBackStack(Rotas.HOME, false)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Início")
            }

            TextButton(
                onClick = {
                    navController.navigate("perfil") {
                        launchSingleTop = true
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Perfil")
            }
        }
    }
}
