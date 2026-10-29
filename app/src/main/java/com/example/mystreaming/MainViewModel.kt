package com.example.mystreaming

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystreaming.data.AppDatabase
import com.example.mystreaming.data.MediaItem
import com.example.mystreaming.data.Review
import com.example.mystreaming.data.filmesDeExemplo
import com.example.mystreaming.data.reviewsDeExemplo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Fonte unica de verdade da UI.
 *
 * Antes (Trabalho 2) a "Sua lista" vivia em um MutableStateFlow e sumia quando o
 * app era destruido. Agora o ViewModel apenas reflete o conteudo do banco Room:
 * cada escrita vai para o banco e os fluxos sao recarregados a partir dos DAOs.
 *
 * Todas as funcoes `suspend` daqui sao chamadas pelas telas dentro de
 * `CoroutineScope(Dispatchers.IO).launch { ... }`.
 */
class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val db = AppDatabase.getInstance(app)
    private val filmesDao = db.filmesDao()
    private val reviewsDao = db.reviewsDao()

    // Lista com os filmes disponiveis (agora vinda do banco)
    private val _filmes = MutableStateFlow<List<MediaItem>>(emptyList())
    val filmes: StateFlow<List<MediaItem>> = _filmes.asStateFlow()

    // Estado reativo da "Sua lista" (agora espelho da tabela filmes)
    private val _minhaLista = MutableStateFlow<List<MediaItem>>(emptyList())
    val minhaLista: StateFlow<List<MediaItem>> = _minhaLista.asStateFlow()

    // Avaliacoes dos filmes
    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                popularBancoInicial()
                carregarFilmes()
                carregarMinhaLista()
            } catch (e: Exception) {
                Log.e("MainViewModel", "Erro ao preparar o banco de dados", e)
            }
        }
    }

    // ------------------------------------------------------------------ Leituras

    suspend fun carregarFilmes() {
        _filmes.value = filmesDao.buscarTodos()
    }

    suspend fun carregarMinhaLista() {
        _minhaLista.value = filmesDao.buscarDaMinhaLista()
    }

    suspend fun carregarReviews(filmeId: Int) {
        _reviews.value = reviewsDao.buscarPorFilme(filmeId)
    }

    /** Lista as avaliacoes de um filme sem passar pelo StateFlow (uso das telas de detalhe). */
    suspend fun listarReviews(filmeId: Int): List<Review> = reviewsDao.buscarPorFilme(filmeId)

    suspend fun buscarFilmePorId(id: Int): MediaItem? = filmesDao.buscarPorId(id)

    suspend fun buscarReviewPorId(id: Int): Review? = reviewsDao.buscarPorId(id)

    // ------------------------------------------------------------------ Escritas

    suspend fun inserirFilme(filme: MediaItem) {
        filmesDao.inserir(filme)
        carregarFilmes()
        carregarMinhaLista()
    }

    suspend fun atualizarFilme(filme: MediaItem) {
        filmesDao.atualizar(filme)
        carregarFilmes()
        carregarMinhaLista()
    }

    suspend fun deletarFilme(filme: MediaItem) {
        filmesDao.deletar(filme)
        carregarFilmes()
        carregarMinhaLista()
    }

    /** Salva ou remove o filme da "Sua lista" (UPDATE da coluna naMinhaLista). */
    suspend fun alternarMinhaLista(filme: MediaItem) {
        filmesDao.atualizar(filme.copy(naMinhaLista = !filme.naMinhaLista))
        carregarFilmes()
        carregarMinhaLista()
    }

    suspend fun inserirReview(review: Review) {
        reviewsDao.inserir(review)
        carregarReviews(review.filmeId)
    }

    suspend fun atualizarReview(review: Review) {
        reviewsDao.atualizar(review)
        carregarReviews(review.filmeId)
    }

    suspend fun deletarReview(review: Review) {
        reviewsDao.deletar(review)
        carregarReviews(review.filmeId)
    }

    // ------------------------------------------------------------------ Seed

    /**
     * Popula o banco apenas na primeira execucao (quando as tabelas estao vazias),
     * usando os proprios DAOs — sem SQL manual.
     */
    private suspend fun popularBancoInicial() {
        if (filmesDao.buscarTodos().isEmpty()) {
            filmesDeExemplo.forEach { filmesDao.inserir(it) }
            reviewsDeExemplo.forEach { reviewsDao.inserir(it) }
        }
    }
}
