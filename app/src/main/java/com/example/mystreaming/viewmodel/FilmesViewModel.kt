package com.example.mystreaming.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mystreaming.data.local.MediaItem
import com.example.mystreaming.data.repository.FilmesRepository
import com.example.mystreaming.data.repository.SeedRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel das telas de filme (Home, Minha Lista, Detalhes e Perfil).
 *
 * Ele nao guarda os dados: apenas reflete o banco. As listas chegam por Flow
 * (o Room reemite sozinho a cada mudanca) e todas as escritas vao para o
 * Repository dentro de `viewModelScope.launch { }`.
 *
 * O estado da tela vive inteiro em [FilmesUiState].
 */
class FilmesViewModel(
    private val repository: FilmesRepository,
    private val seedRepository: SeedRepository,
    private val filmeId: Int? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(FilmesUiState())
    val uiState: StateFlow<FilmesUiState> = _uiState.asStateFlow()

    init {
        // Seed do banco: popula os dados de exemplo apenas na primeira execucao.
        viewModelScope.launch {
            try {
                seedRepository.popularBancoSeVazio()
            } catch (e: Exception) {
                Log.e("FilmesViewModel", "Erro ao popular o banco", e)
            }
        }

        // As listas vem do banco em Flow: nao precisa buscar de novo na mao.
        // O filme em foco (tela de Detalhes) tambem e derivado daqui, entao ele
        // acompanha o banco sozinho: editar, salvar na lista ou excluir atualiza
        // a tela sem nenhum "carregar de novo".
        viewModelScope.launch {
            combine(repository.filmes, repository.minhaLista) { filmes, minhaLista ->
                filmes to minhaLista
            }.collect { (filmes, minhaLista) ->
                _uiState.update { estado ->
                    estado.copy(
                        filmes = filmes,
                        minhaLista = minhaLista,
                        carregando = false,
                        filmeSelecionado = filmeId?.let { id ->
                            filmes.firstOrNull { it.id == id }
                        }
                    )
                }
            }
        }
    }

    // ------------------------- formulario de adicionar -------------------------

    fun abrirFormulario() = _uiState.update {
        it.copy(mostrarFormulario = true, titulo = "", ano = "", nota = "")
    }

    fun fecharFormulario() = _uiState.update { it.copy(mostrarFormulario = false) }

    fun onTituloChange(valor: String) = _uiState.update { it.copy(titulo = valor) }

    fun onAnoChange(valor: String) = _uiState.update { it.copy(ano = valor) }

    fun onNotaChange(valor: String) = _uiState.update { it.copy(nota = valor) }

    fun adicionarFilme() {
        val estado = _uiState.value
        if (estado.titulo.isBlank()) return

        viewModelScope.launch {
            try {
                repository.inserir(
                    MediaItem(
                        title = estado.titulo.trim(),
                        year = estado.ano.trim().ifBlank { "—" },
                        rating = estado.nota.trim().ifBlank { "—" },
                        naMinhaLista = true
                    )
                )
                _uiState.update {
                    it.copy(mostrarFormulario = false, titulo = "", ano = "", nota = "")
                }
            } catch (e: Exception) {
                Log.e("FilmesViewModel", "Erro ao salvar o filme", e)
                _uiState.update { it.copy(erro = "Nao foi possivel salvar o filme") }
            }
        }
    }

    // ------------------------------ edicao ------------------------------

    fun abrirEdicao() {
        val filme = _uiState.value.filmeSelecionado ?: return
        _uiState.update {
            it.copy(
                mostrarEdicao = true,
                tituloEdicao = filme.title,
                anoEdicao = filme.year,
                notaEdicao = filme.rating
            )
        }
    }

    fun fecharEdicao() = _uiState.update { it.copy(mostrarEdicao = false) }

    fun onTituloEdicaoChange(valor: String) = _uiState.update { it.copy(tituloEdicao = valor) }

    fun onAnoEdicaoChange(valor: String) = _uiState.update { it.copy(anoEdicao = valor) }

    fun onNotaEdicaoChange(valor: String) = _uiState.update { it.copy(notaEdicao = valor) }

    fun salvarEdicao() {
        val estado = _uiState.value
        val filme = estado.filmeSelecionado ?: return

        viewModelScope.launch {
            try {
                val atualizado = filme.copy(
                    title = estado.tituloEdicao.trim().ifBlank { filme.title },
                    year = estado.anoEdicao.trim().ifBlank { filme.year },
                    rating = estado.notaEdicao.trim().ifBlank { filme.rating }
                )
                repository.atualizar(atualizado)
                _uiState.update { it.copy(filmeSelecionado = atualizado, mostrarEdicao = false) }
            } catch (e: Exception) {
                Log.e("FilmesViewModel", "Erro ao atualizar o filme", e)
                _uiState.update { it.copy(erro = "Nao foi possivel atualizar o filme") }
            }
        }
    }

    // ------------------------------ exclusao ------------------------------

    fun pedirExclusao() = _uiState.update { it.copy(mostrarExclusao = true) }

    fun cancelarExclusao() = _uiState.update { it.copy(mostrarExclusao = false) }

    fun confirmarExclusao() {
        val filme = _uiState.value.filmeSelecionado ?: return
        viewModelScope.launch {
            try {
                repository.deletar(filme)
            } catch (e: Exception) {
                Log.e("FilmesViewModel", "Erro ao excluir o filme", e)
            }
        }
    }

    // ---------------------------- "Sua lista" ----------------------------

    /** Salva ou remove o filme da "Sua lista" (UPDATE da coluna naMinhaLista). */
    fun alternarMinhaLista(filme: MediaItem) {
        viewModelScope.launch {
            try {
                repository.atualizar(filme.copy(naMinhaLista = !filme.naMinhaLista))
            } catch (e: Exception) {
                Log.e("FilmesViewModel", "Erro ao atualizar a lista", e)
            }
        }
    }
}
