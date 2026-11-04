package com.example.mystreaming.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystreaming.data.local.Review
import com.example.mystreaming.data.local.USUARIO_LOGIN
import com.example.mystreaming.data.repository.FilmesRepository
import com.example.mystreaming.data.repository.ReviewsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel das telas de avaliacao e comentario (Detalhes, Avaliar, Comentarios
 * e Comentario).
 *
 * Ele recebe dois repositories: o de reviews (para as avaliacoes) e o de filmes
 * (apenas para o titulo do filme, que vive na tabela `filmes`).
 *
 * O estado da tela vive inteiro em [ReviewsUiState].
 */
class ReviewsViewModel(
    private val repository: ReviewsRepository,
    private val filmesRepository: FilmesRepository,
    private val filmeId: Int? = null,
    private val reviewId: Int? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewsUiState())
    val uiState: StateFlow<ReviewsUiState> = _uiState.asStateFlow()

    init {
        if (filmeId != null) {
            // Titulo do filme (vem da tabela `filmes`).
            viewModelScope.launch {
                try {
                    val filme = filmesRepository.buscarPorId(filmeId)
                    _uiState.update {
                        it.copy(filmeId = filmeId, tituloFilme = filme?.title ?: "")
                    }
                } catch (e: Exception) {
                    Log.e("ReviewsViewModel", "Erro ao buscar o filme $filmeId", e)
                }
            }

            // Lista reativa das avaliacoes do filme: atualiza sozinha.
            viewModelScope.launch {
                try {
                    repository.buscarPorFilme(filmeId).collect { lista ->
                        _uiState.update { it.copy(reviews = lista, carregando = false) }
                    }
                } catch (e: Exception) {
                    Log.e("ReviewsViewModel", "Erro ao carregar os comentarios", e)
                    _uiState.update { it.copy(erro = "Nao foi possivel carregar os comentarios") }
                }
            }
        }

        // Comentario em foco (tela de Comentario): derivado do Flow do banco,
        // entao continua em dia depois de editar ou excluir.
        if (reviewId != null) {
            viewModelScope.launch {
                try {
                    repository.reviews.collect { lista ->
                        _uiState.update { estado ->
                            estado.copy(
                                reviewSelecionado = lista.firstOrNull { it.id == reviewId },
                                carregando = false
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ReviewsViewModel", "Erro ao buscar o comentario $reviewId", e)
                    _uiState.update { it.copy(erro = "Nao foi possivel carregar o comentario") }
                }
            }
        }
    }

    // ------------------------ formulario de avaliacao ------------------------

    fun onNotaChange(nota: Int) = _uiState.update { it.copy(nota = nota) }

    fun onComentarioChange(texto: String) = _uiState.update { it.copy(comentario = texto) }

    fun enviarAvaliacao() {
        val estado = _uiState.value
        if (estado.filmeId == 0) return

        viewModelScope.launch {
            try {
                repository.inserir(
                    Review(
                        filmeId = estado.filmeId,
                        usuario = USUARIO_LOGIN,
                        comentario = estado.comentario.trim().ifBlank { "Sem comentario" },
                        nota = estado.nota
                    )
                )
                _uiState.update { it.copy(enviando = true, comentario = "") }
            } catch (e: Exception) {
                Log.e("ReviewsViewModel", "Erro ao salvar a avaliacao", e)
                _uiState.update { it.copy(erro = "Nao foi possivel salvar a avaliacao") }
            }
        }
    }

    // -------------------------------- edicao --------------------------------

    fun abrirEdicao() {
        val review = _uiState.value.reviewSelecionado ?: return
        _uiState.update {
            it.copy(
                mostrarEdicao = true,
                textoEdicao = review.comentario,
                notaEdicao = review.nota
            )
        }
    }

    fun fecharEdicao() = _uiState.update { it.copy(mostrarEdicao = false) }

    fun onTextoEdicaoChange(texto: String) = _uiState.update { it.copy(textoEdicao = texto) }

    fun onNotaEdicaoChange(nota: Int) = _uiState.update { it.copy(notaEdicao = nota) }

    fun salvarEdicao() {
        val estado = _uiState.value
        val review = estado.reviewSelecionado ?: return

        viewModelScope.launch {
            try {
                val atualizado = review.copy(
                    comentario = estado.textoEdicao.trim().ifBlank { review.comentario },
                    nota = estado.notaEdicao
                )
                repository.atualizar(atualizado)
                _uiState.update { it.copy(reviewSelecionado = atualizado, mostrarEdicao = false) }
            } catch (e: Exception) {
                Log.e("ReviewsViewModel", "Erro ao atualizar o comentario", e)
                _uiState.update { it.copy(erro = "Nao foi possivel atualizar o comentario") }
            }
        }
    }

    // ------------------------------- exclusao -------------------------------

    fun excluirReview(review: Review) {
        viewModelScope.launch {
            try {
                repository.deletar(review)
            } catch (e: Exception) {
                Log.e("ReviewsViewModel", "Erro ao excluir o comentario", e)
            }
        }
    }
}
