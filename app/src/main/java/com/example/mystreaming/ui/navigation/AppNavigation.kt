package com.example.mystreaming.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mystreaming.data.repository.RepositoryProvider
import com.example.mystreaming.ui.screens.AvaliacaoScreen
import com.example.mystreaming.ui.screens.ComentarioDetalheScreen
import com.example.mystreaming.ui.screens.ComentariosScreen
import com.example.mystreaming.ui.screens.DetalhesScreen
import com.example.mystreaming.ui.screens.HomeScreen
import com.example.mystreaming.ui.screens.MinhaListaGridScreen
import com.example.mystreaming.ui.screens.PerfilScreen
import com.example.mystreaming.viewmodel.FilmesViewModel
import com.example.mystreaming.viewmodel.FilmesViewModelFactory
import com.example.mystreaming.viewmodel.ReviewsViewModel
import com.example.mystreaming.viewmodel.ReviewsViewModelFactory

/**
 * Navegacao principal do app.
 *
 * As telas nao recebem o objeto inteiro pela navegacao: elas recebem apenas o
 * **id** e o ViewModel busca o conteudo no banco Room.
 *
 * Aqui e onde os ViewModels sao criados: cada tela recebe o seu, com a Factory
 * que injeta o Repository certo.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    val filmesRepository = remember(context) { RepositoryProvider.filmesRepository(context) }
    val reviewsRepository = remember(context) { RepositoryProvider.reviewsRepository(context) }
    val seedRepository = remember(context) { RepositoryProvider.seedRepository(context) }

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
                val factory = remember(filmesRepository, seedRepository) {
                    FilmesViewModelFactory(filmesRepository, seedRepository)
                }

                HomeScreen(
                    navController = navController,
                    viewModel = viewModel(factory = factory),
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

                val filmesFactory = remember(filmesRepository, seedRepository, filmeId) {
                    FilmesViewModelFactory(filmesRepository, seedRepository, filmeId)
                }
                val reviewsFactory = remember(reviewsRepository, filmeId) {
                    ReviewsViewModelFactory(reviewsRepository, filmesRepository, filmeId)
                }

                DetalhesScreen(
                    navController = navController,
                    filmesViewModel = viewModel(factory = filmesFactory),
                    reviewsViewModel = viewModel(factory = reviewsFactory)
                )
            }

            composable(
                route = Rotas.AVALIACAO,
                arguments = listOf(navArgument("filmeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val filmeId = backStackEntry.arguments?.getInt("filmeId") ?: 0

                val reviewsFactory = remember(reviewsRepository, filmeId) {
                    ReviewsViewModelFactory(reviewsRepository, filmesRepository, filmeId)
                }

                AvaliacaoScreen(
                    navController = navController,
                    viewModel = viewModel(factory = reviewsFactory)
                )
            }

            composable(
                route = Rotas.COMENTARIOS,
                arguments = listOf(navArgument("filmeId") { type = NavType.IntType })
            ) { backStackEntry ->
                val filmeId = backStackEntry.arguments?.getInt("filmeId") ?: 0

                val reviewsFactory = remember(reviewsRepository, filmeId) {
                    ReviewsViewModelFactory(reviewsRepository, filmesRepository, filmeId)
                }

                ComentariosScreen(
                    navController = navController,
                    viewModel = viewModel(factory = reviewsFactory)
                )
            }

            composable(
                route = Rotas.COMENTARIO_DETALHES,
                arguments = listOf(navArgument("reviewId") { type = NavType.IntType })
            ) { backStackEntry ->
                val reviewId = backStackEntry.arguments?.getInt("reviewId") ?: 0

                val reviewsFactory = remember(reviewsRepository, reviewId) {
                    ReviewsViewModelFactory(reviewsRepository, filmesRepository, reviewId = reviewId)
                }

                ComentarioDetalheScreen(
                    navController = navController,
                    viewModel = viewModel(factory = reviewsFactory)
                )
            }

            composable(Rotas.MINHA_LISTA_GRID) {
                val factory = remember(filmesRepository, seedRepository) {
                    FilmesViewModelFactory(filmesRepository, seedRepository)
                }

                MinhaListaGridScreen(
                    navController = navController,
                    viewModel = viewModel(factory = factory)
                )
            }

            composable(Rotas.PERFIL) {
                val factory = remember(filmesRepository, seedRepository) {
                    FilmesViewModelFactory(filmesRepository, seedRepository)
                }

                PerfilScreen(viewModel = viewModel(factory = factory))
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
