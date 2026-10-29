package com.example.mystreaming

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Navegacao principal do app.
 *
 * A partir do Trabalho 3 as telas nao recebem mais o objeto inteiro pela
 * navegacao: elas recebem apenas o **id** e buscam o conteudo direto no banco
 * Room (ver Requisitos.txt, secao 3.3).
 */
@Composable
fun AppNavigation(mainViewModel: MainViewModel = viewModel()) {
    val navController = rememberNavController()

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
                    mainViewModel = mainViewModel,
                    onVerMinhaListaClick = {
                        navController.navigate(Rotas.MINHA_LISTA_GRID)
                    }
                )
            }

            composable(
                route = Rotas.DESCRICAO,
                arguments = listOf(navArgument("filmeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val filmeId = backStackEntry.arguments?.getInt("filmeId") ?: 0

                DetalhesScreen(
                    navController = navController,
                    mainViewModel = mainViewModel,
                    filmeId = filmeId
                )
            }

            composable(
                route = Rotas.AVALIACAO,
                arguments = listOf(navArgument("filmeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val filmeId = backStackEntry.arguments?.getInt("filmeId") ?: 0

                ReviewScreen(
                    navController = navController,
                    mainViewModel = mainViewModel,
                    filmeId = filmeId
                )
            }

            composable(
                route = Rotas.COMENTARIOS,
                arguments = listOf(navArgument("filmeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val filmeId = backStackEntry.arguments?.getInt("filmeId") ?: 0

                ComentariosScreen(
                    navController = navController,
                    mainViewModel = mainViewModel,
                    filmeId = filmeId
                )
            }

            composable(
                route = Rotas.COMENTARIO_DETALHES,
                arguments = listOf(navArgument("reviewId") { type = NavType.IntType })
            ) { backStackEntry ->
                val reviewId = backStackEntry.arguments?.getInt("reviewId") ?: 0

                ComentarioDetalhesScreen(
                    navController = navController,
                    mainViewModel = mainViewModel,
                    reviewId = reviewId
                )
            }

            composable(Rotas.MINHA_LISTA_GRID) {
                MinhaListaGridScreen(
                    navController = navController,
                    mainViewModel = mainViewModel
                )
            }

            composable(Rotas.PERFIL) {
                ProfileScreen(mainViewModel = mainViewModel)
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
                    navController.navigate(Rotas.PERFIL) {
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
